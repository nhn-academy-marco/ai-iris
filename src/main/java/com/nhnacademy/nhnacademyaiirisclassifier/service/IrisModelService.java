package com.nhnacademy.nhnacademyaiirisclassifier.service;

import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisRequest;
import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrisModelService {
    private static final String MODEL_PATH = "model/iris-model.zip";
    private MultiLayerNetwork model;

    public void setModel(MultiLayerNetwork model) {
        this.model = model;
    }

    @EventListener(ApplicationReadyEvent.class) // [1] 서버 시작 직후 실행
    public void initModel() throws IOException {
        File modelFile = new File(MODEL_PATH);

        if (modelFile.exists()) {
            log.info("학습된 모델이 이미 존재합니다. 자동 학습을 건너뜁니다.");
            return;
        }

        log.info("모델 파일이 없습니다. 학습을 시작합니다...");
        trainAndSaveModel();
    }

    private void trainAndSaveModel() throws IOException {
        // 1. 데이터 준비 (DataSetIterator)
        DataSetIterator trainIter = new IrisDataSetIterator(150, 150);

        // 2. 신경망 설계 (Configuration)
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                .list()
                .layer(new DenseLayer.Builder().nIn(4).nOut(10).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(10).nOut(3).build())
                .build();

        // 3. 모델 초기화 및 학습
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        for(int i = 0; i < 300; i++) {
            model.fit(trainIter);
        }

        // 4. 모델 저장 (Serialization)
        ModelSerializer.writeModel(model, new File(MODEL_PATH), true);
        log.info("모델 학습 및 저장이 완료되었습니다: {}", MODEL_PATH);
    }

    public IrisResponse predict(IrisRequest request) {
        // [3] 입력을 INDArray 행렬로 변환 (1행 4열)
        INDArray input = Nd4j.create(new double[][]{{
                request.getSepalLength(),
                request.getSepalWidth(),
                request.getPetalLength(),
                request.getPetalWidth()
        }});

        // [4] 모델 추론 수행
        // 만약 모델이 null이라면 (서버 시작 시점에 파일이 없었던 경우), 
        // 여기서는 간단히 예외를 던지거나 적절한 처리를 합니다.
        if (model == null) {
            throw new IllegalStateException("모델이 로드되지 않았습니다. 먼저 모델을 학습시켜주세요.");
        }

        INDArray output = model.output(input);

        // [5] 결과 해석 (확률 분포 추출)
        double[] probabilities = output.toDoubleVector();
        String[] species = {"Setosa", "Versicolor", "Virginica"};

        Map<String, Double> resultMap = new HashMap<>();
        int maxIndex = 0;
        for (int i = 0; i < probabilities.length; i++) {
            resultMap.put(species[i], probabilities[i]);
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }

        return new IrisResponse(species[maxIndex], resultMap);
    }
}
# Step 4: 예측 서비스 엔진 개발 (Service & Controller)

이 단계에서는 Step 3에서 저장한 모델 파일(`iris-model.zip`)을 불러와서, 실제 사용자의 입력값에 대해 결과를 예측하는 **추론(Inference) 엔진**과 이를 외부에서 사용할 수 있게 해주는 **REST API**를 구축합니다.

## 0. 주요 개념 정리 (Vocabulary)

- **추론 (Inference)**: 이미 학습이 완료된 모델에 새로운 데이터를 넣어서 결과(예측값)를 얻어내는 과정입니다.
- **역직렬화 (Deserialization)**: 파일 형태(zip)로 저장된 모델을 다시 자바 객체 메모리로 읽어오는 과정입니다.
- **@Configuration & @Bean**: 스프링 설정 클래스에서 자바 객체(Bean)를 생성하고 스프링 컨테이너가 관리하도록 등록할 때 사용합니다. 이를 통해 모델 객체를 필요한 곳에 자동으로 주입(DI)받아 사용할 수 있습니다.
- **INDArray**: DL4J에서 수치 데이터를 다루는 행렬 객체입니다. 딥러닝 연산은 내부적으로 행렬 계산을 통해 이루어지므로, 일반적인 `double` 배열을 이 형태로 변환해야 합니다.
- **REST Controller**: 웹브라우저나 외부 시스템의 요청을 받아 JSON 형태로 데이터를 주고받는 창구입니다.

---

## 1. 학습 목표

- `@Configuration`과 `@Bean`을 활용한 모델 객체의 Spring Bean 등록 이해
- `@RequiredArgsConstructor`를 통한 의존성 주입(Dependency Injection) 활용
- 사용자의 입력 데이터를 신경망 연산이 가능한 `INDArray`로 변환
- 서비스 로직을 외부로 노출하기 위한 Spring REST API 구현

---

## 2. 모델 로드 및 추론 로직 구현

Step 3에서 만든 `IrisModelService`에 모델을 불러오고 예측하는 기능을 추가합니다. 모델 파일은 크기가 클 수 있으므로, 요청이 올 때마다 불러오는 것이 아니라 서버가 시작될 때 **Spring Bean으로 한 번만 로드**하여 주입받아 사용하는 것이 핵심입니다.

### 2.1 MultiLayerNetworkConfig 생성

모델 파일을 읽어 Spring Bean으로 등록하는 설정 클래스를 `config` 패키지에 생성합니다.
서버가 시작될 때 모델을 한 번만 로드하여 빈(Bean)으로 등록해두면, 여러 요청에서 효율적으로 재사용할 수 있습니다.

특히, `IrisModelService`와 `MultiLayerNetwork` 사이의 순환 참조를 방지하기 위해 **수정자 주입(Setter Injection)** 패턴을 활용합니다.

```java
package com.nhnacademy.nhnacademyaiirisclassifier.config;

import com.nhnacademy.nhnacademyaiirisclassifier.service.IrisModelService;
import lombok.RequiredArgsConstructor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class MultiLayerNetworkConfig {
    private static final String MODEL_PATH = "model/iris-model.zip";
    private final IrisModelService irisModelService;

    @Bean
    public MultiLayerNetwork multiLayerNetwork() throws IOException {
        File modelFile = new File(MODEL_PATH);

        MultiLayerNetwork model = null;
        if (modelFile.exists()) {
            // [1] 저장된 모델 파일을 읽어와서 객체로 복원(역직렬화)
            model = ModelSerializer.restoreMultiLayerNetwork(modelFile);
        }

        // [2] 서비스에 로드된 모델을 설정해줍니다. 
        // 순환 참조 방지를 위해 생성자 주입 대신 수정자(Setter)를 활용합니다.
        irisModelService.setModel(model);

        return model;
    }
}
```

### 2.2 IrisModelService 보강 코드

이제 등록된 `MultiLayerNetwork` 빈을 주입받아 사용하도록 서비스를 수정합니다.

```java
package com.nhnacademy.nhnacademyaiirisclassifier.service;

import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisRequest;
import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class IrisModelService {
    private MultiLayerNetwork model; 

    // [3] 순환 참조 해결을 위한 수정자(Setter)
    public void setModel(MultiLayerNetwork model) {
        this.model = model;
    }

    // ... (기존 initModel 및 trainAndSaveModel 메서드)

    public IrisResponse predict(IrisRequest request) {
        // [4] 입력을 INDArray 행렬로 변환 (1행 4열)
        INDArray input = Nd4j.create(new double[][]{{
                request.getSepalLength(),
                request.getSepalWidth(),
                request.getPetalLength(),
                request.getPetalWidth()
        }});

        // [5] 모델 추론 수행
        if (model == null) {
            throw new IllegalStateException("모델이 로드되지 않았습니다. 먼저 모델을 학습시켜주세요.");
        }
        INDArray output = model.output(input);

        // [6] 결과 해석 (확률 분포 추출)
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
```

### 2.3 코드 상세 분석

1.  **순환 참조 해결**: `IrisModelService`는 `MultiLayerNetwork`가 필요하고, `MultiLayerNetwork` 설정 과정에서는 `IrisModelService`가 필요할 수 있습니다. 이를 해결하기 위해 서비스에서 `final`을 제거하고 `setModel` 메서드를 통해 나중에 모델을 전달받도록 설계했습니다.
2.  **`@Configuration & @Bean`**: 스프링이 켜질 때 `multiLayerNetwork()` 메서드를 실행하고, 반환된 모델 객체를 스프링 컨테이너가 관리합니다.
3.  **`Nd4j.create(...)`**: 자바의 기본 `double` 데이터를 딥러닝 엔진이 이해할 수 있는 행렬(`INDArray`)로 변환합니다. 우리 모델은 4개의 특징을 입력받으므로 1x4 행렬을 만듭니다.
4.  **`model.output(input)`**: 신경망에 데이터를 통과시켜 결과를 얻습니다. 결과값은 각 품종일 확률을 담은 행렬로 나옵니다.
5.  **결과 해석**: 모델이 준 확률 값들 중 가장 높은 값을 가진 품종을 찾아 `IrisResponse`에 담아 반환합니다.

---

## 3. REST Controller 구현

이제 사용자가 웹을 통해 데이터를 보낼 수 있도록 API 창구를 만듭니다. `controller` 패키지에 `IrisController`를 생성합니다.

```java
package com.nhnacademy.nhnacademyaiirisclassifier.controller;

import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisRequest;
import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisResponse;
import com.nhnacademy.nhnacademyaiirisclassifier.service.IrisModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/iris")
@RequiredArgsConstructor
public class IrisController {
    private final IrisModelService irisModelService;

    @PostMapping("/predict")
    public IrisResponse predict(@RequestBody IrisRequest request) {
        return irisModelService.predict(request);
    }
}
```

- **`@RestController`**: 이 클래스의 모든 응답은 JSON 형태로 반환됩니다.
- **`@PostMapping("/predict")`**: 사용자가 품종 예측 데이터를 POST 방식으로 보낼 주소를 정의합니다.
- **`@RequestBody`**: 클라이언트가 보낸 JSON 데이터를 자바의 `IrisRequest` 객체로 자동으로 변환해 줍니다.

---

## 4. 확인 사항 및 테스트

1.  서버 실행 시 로그에 모델 로드 관련 메시지가 나오나요?
2.  **HTTP Client를 이용한 테스트**:
    `http/iris-test.http` 파일을 사용하여 API가 정상적으로 동작하는지 확인할 수 있습니다. IntelliJ IDEA나 VS Code의 REST Client 확장을 사용하면 편리합니다.

    **[http/iris-test.http]**
    ```http
    ### Iris 품종 예측 API 테스트

    # @name predict-setosa
    # Setosa 품종 예측 테스트
    POST http://localhost:8080/api/iris/predict
    Content-Type: application/json

    {
      "sepalLength": 5.1,
      "sepalWidth": 3.5,
      "petalLength": 1.4,
      "petalWidth": 0.2
    }

    ###

    # @name predict-versicolor
    # Versicolor 품종 예측 테스트
    POST http://localhost:8080/api/iris/predict
    Content-Type: application/json

    {
      "sepalLength": 6.0,
      "sepalWidth": 2.2,
      "petalLength": 4.0,
      "petalWidth": 1.0
    }

    ###

    # @name predict-virginica
    # Virginica 품종 예측 테스트
    POST http://localhost:8080/api/iris/predict
    Content-Type: application/json

    {
      "sepalLength": 6.3,
      "sepalWidth": 3.3,
      "petalLength": 6.0,
      "petalWidth": 2.5
    }
    ```

3.  `probabilities` 맵에 3가지 품종의 확률이 모두 포함되어 있고, 그 합이 거의 1(100%)에 가까운지 확인해 보세요.
4.  **단위 테스트 코드를 통한 검증**:
    컨트롤러가 요청을 받아 서비스로 잘 전달하고 응답을 반환하는지 테스트 코드를 통해 확인할 수 있습니다.

    **[src/test/java/com/nhnacademy/nhnacademyaiirisclassifier/controller/IrisControllerTest.java]**
    ```java
    package com.nhnacademy.nhnacademyaiirisclassifier.controller;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisRequest;
    import com.nhnacademy.nhnacademyaiirisclassifier.dto.IrisResponse;
    import com.nhnacademy.nhnacademyaiirisclassifier.service.IrisModelService;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;

    import java.util.Map;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.BDDMockito.given;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @WebMvcTest(IrisController.class)
    class IrisControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private IrisModelService irisModelService;

        @Test
        @DisplayName("Iris 품종 예측 API 테스트")
        void predictTest() throws Exception {
            // given
            IrisRequest request = new IrisRequest(5.1, 3.5, 1.4, 0.2);
            IrisResponse response = new IrisResponse("Setosa", Map.of(
                    "Setosa", 0.9,
                    "Versicolor", 0.05,
                    "Virginica", 0.05
            ));

            given(irisModelService.predict(any(IrisRequest.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/iris/predict")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.predictedSpecies").value("Setosa"))
                    .andExpect(jsonPath("$.probabilities.Setosa").value(0.9));
        }
    }
    ```

5.  `MultiLayerNetworkConfig`에서 `@DependsOn("irisModelService")`를 제거하면 어떤 현상이 발생하나요? (파일이 생성되기 전에 로드를 시도하여 `null`이 반환되는지 확인해 보세요.)

---

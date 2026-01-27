# Step 3: ML 모델 학습 및 저장 (Modeling)

이 단계에서는 Java 코드를 사용하여 인공 신경망(Artificial Neural Network)을 설계하고, 데이터를 학습시킨 뒤 그 결과를 파일(`iris-model.zip`)로 저장하는 과정을 배웁니다.

## 0. 왜 학습을 하고, 왜 저장하나요? (The Big Picture)

본격적인 실습에 앞서, 우리가 왜 이 과정을 거치는지 전체적인 흐름을 이해하는 것이 중요합니다.

### 1) 왜 신경망으로 '학습'을 하나요?
전통적인 프로그램은 "꽃잎 길이가 5cm 이상이면 A 품종이다"라고 사람이 직접 규칙을 정해줍니다. 하지만 자연의 데이터는 훨씬 복잡하고 예외가 많습니다.
**신경망 학습**은 컴퓨터가 수만 번의 반복을 통해 스스로 **데이터 속에 숨겨진 복잡한 규칙**을 찾아내게 만드는 과정입니다. 우리는 정답이 있는 데이터를 던져주고, "이걸 보고 규칙을 알아내봐!"라고 시키는 것이죠.

### 2) 왜 모델을 '저장'하나요?
학습은 컴퓨터에게 아주 힘든 작업입니다. 붓꽃 데이터는 작아서 금방 끝나지만, 실제 현업 데이터는 며칠, 몇 주가 걸리기도 합니다.
- **재사용**: 매번 앱을 켤 때마다 다시 공부시킬 수는 없습니다. 공부가 끝난 결과물(모델)을 파일로 저장해두면, 나중에 필요할 때 **0.1초 만에 꺼내서 바로 사용**할 수 있습니다.
- **분리**: "공부하는 환경(학습)"과 "실제 서비스하는 환경(추론)"을 나눌 수 있습니다.

### 3) 전체 머신러닝 흐름
1. **데이터 준비**: 공부할 재료를 모읍니다. (Step 1)
2. **신경망 설계**: 인공지능의 뇌 구조를 만듭니다. (Step 3)
3. **학습(Training)**: 데이터를 먹여주며 규칙을 깨닫게 합니다. (Step 3)
4. **저장(Saving)**: 깨달은 지식을 파일(`iris-model.zip`)로 저장합니다. (Step 3)
5. **추론(Inference)**: 저장된 파일을 읽어와 실제 예측에 사용합니다. (Step 4)

## 1. 주요 개념 정리 (Vocabulary)

본격적인 코딩에 앞서 딥러닝 모델을 만들 때 사용하는 필수 용어들을 알아봅니다.

- **인공 신경망 (Artificial Neural Network)**: 생물학적 뇌의 뉴런 구조를 모방한 수학적 모델입니다. 여러 개의 '층(Layer)'으로 구성됩니다.
- **레이어 (Layer, 층)**: 신경망의 구성 단위입니다.
    - **입력층 (Input Layer)**: 데이터를 처음 받아들이는 곳 (꽃잎/꽃받침 수치 4개).
    - **은닉층 (Hidden Layer)**: 데이터를 처리하고 특징을 추출하는 중간층.
    - **출력층 (Output Layer)**: 최종 결과를 내보내는 곳 (품종 3개 중 하나).
- **활성화 함수 (Activation Function)**: 입력된 신호의 합을 출력 신호로 변환할 때 사용하는 함수입니다. (예: `ReLU`, `Softmax`)
- **손실 함수 (Loss Function)**: 모델의 예측값과 실제 정답이 얼마나 차이 나는지 계산하는 함수입니다. 이 값이 작아지도록 학습합니다.
- **에포크 (Epoch)**: 전체 학습 데이터를 한 번 모두 훑는 것을 '1 에포크'라고 합니다. 보통 여러 번 반복해서 학습합니다.
- **직렬화 (Serialization)**: 메모리에 있는 객체(학습된 모델)를 파일 형태로 저장하는 과정입니다.

## 1. 학습 목표

- Java 기반의 `MultiLayerConfiguration`을 활용한 신경망 설계
- 학습 데이터 피팅(`fit`) 과정을 통한 모델 생성
- 학습된 모델을 파일 시스템에 저장하는 방법 습득

## 2. 신경망 설계 (Neural Network Configuration)

신경망 설계는 인공지능의 **'뇌 구조'** 를 만드는 과정입니다. DL4J에서는 `MultiLayerConfiguration` 클래스를 사용하여 어떤 레이어를 쌓을지, 어떤 방식으로 학습할지 결정합니다.

우리 프로젝트에서는 **붓꽃(Iris) 데이터셋**을 사용합니다. 이 데이터셋은 통계학과 머신러닝에서 가장 유명한 예제 데이터 중 하나로, 붓꽃의 물리적인 수치를 보고 어떤 품종인지 맞추는 문제입니다.

- **4가지 특징 (입력 데이터)**: 
    - **꽃받침(Sepal)** 의 길이와 너비
    - **꽃잎(Petal)** 의 길이와 너비
- **3가지 품종 (출력 정답)**: 
    - 세토사(Setosa), 버시컬러(Versicolor), 버지니카(Virginica)

우리는 이 4가지 수치를 신경망에 입력하면, 신경망이 3가지 품종 중 하나로 분류하도록 모델을 설계할 것입니다.

### 2.1 설정 코드 상세 분석

```java
MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
    .seed(123) // [1] 랜덤 시드 설정
    .activation(Activation.RELU) // [2] 기본 활성화 함수
    .weightInit(WeightInit.XAVIER) // [3] 가중치 초기화 방법
    .updater(new Sgd(0.1)) // [4] 학습 알고리즘 및 학습률
    .list()
    .layer(new DenseLayer.Builder().nIn(4).nOut(10).build()) // [5] 은닉층 설정
    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) // [6] 출력층 설정
        .activation(Activation.SOFTMAX)
        .nIn(10).nOut(3).build())
    .build();
```

**각 설정이 의미하는 것:**

1.  **seed(123)**: 인공지능이 학습할 때 내부적으로 무작위 숫자를 사용합니다. 이 값을 고정하면 매번 실행할 때마다 똑같은 결과가 나오게 되어, 코드를 수정하며 테스트하기 좋습니다. (레시피를 고정하는 것과 같습니다.)
2.  **activation(Activation.RELU)**: 뇌세포(뉴런)가 다음 세포로 신호를 보낼 때, 신호의 강도를 조절하는 스위치 역할입니다. `RELU`는 현대 딥러닝에서 가장 성능이 좋아 널리 쓰이는 기본 스위치입니다.
3.  **weightInit(WeightInit.XAVIER)**: 신경망의 초기 상태를 똑똑하게 설정하는 방법입니다. 처음부터 엉뚱한 값을 가지고 시작하지 않도록 도와줍니다.
4.  **updater(new Sgd(0.1))**: 모델이 정답을 찾아가기 위해 '어떻게 공부할지'를 정합니다. `0.1`은 **학습률(Learning Rate)**로, 한 번 공부할 때 얼마나 크게 깨우칠지를 결정합니다. 너무 크면 정답을 지나치고, 너무 작으면 공부가 너무 오래 걸립니다.
5.  **DenseLayer (은닉층)**: 중간에서 데이터의 복잡한 특징을 찾아냅니다. 
    - `nIn(4)`: 입력 데이터(꽃잎/꽃받침 길이와 너비)가 4개임을 의미합니다.
    - `nOut(10)`: 이 층을 통과하며 10개의 추상적인 특징으로 변환됩니다.
6.  **OutputLayer (출력층)**: 최종 결론을 내립니다.
    - `nOut(3)`: 우리가 맞추려는 붓꽃 품종이 3종류이므로 3으로 설정합니다.
    - `Activation.SOFTMAX`: 출력값을 '확률'로 바꿔줍니다. (예: Setosa일 확률 90%, Virginica일 확률 5% 등)

---

## 3. 애플리케이션 시작 시 자동 학습 설정

실제 서비스에서는 서버가 켜질 때 모델이 준비되어 있어야 합니다. 매번 수동으로 학습 코드를 실행하는 대신, **Spring의 Application Event** 기능을 활용해 서버가 시작될 때 모델 파일이 있는지 확인하고, 없다면 자동으로 학습을 진행하도록 구성해 보겠습니다.

### 3.1 ApplicationReadyEvent란?
- Spring Boot 애플리케이션이 실행을 마치고 서비스할 준비가 된 직후에 발생하는 이벤트입니다.
- `@EventListener(ApplicationReadyEvent.class)` 어노테이션을 사용하면, "서버가 켜지자마자 이 일을 해!"라고 명령할 수 있습니다.

### 3.2 자동 학습 로직 설계
우리의 전략은 다음과 같습니다:
1.  서버가 시작된다.
2.  `model/iris-model.zip` 파일이 있는지 확인한다.
3.  **파일이 없다면**: 붓꽃 데이터를 가져와서 신경망을 학습시키고 파일을 저장한다.
4.  **파일이 있다면**: 이미 공부가 끝난 상태이므로 학습을 건너뛴다.

---

## 4. 데이터 준비 및 학습 구현 (Implementation)

이제 위에서 배운 개념들을 하나로 합쳐 실제 서비스를 담당할 `IrisModelService` 클래스를 만들어 보겠습니다.

### 4.1 IrisModelService 코드 예시

```java
@Service
@Slf4j
public class IrisModelService {
    private static final String MODEL_PATH = "model/iris-model.zip";

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

        for(int i = 0; i < 30; i++) {
            model.fit(trainIter);
        }

        // 4. 모델 저장 (Serialization)
        ModelSerializer.writeModel(model, new File(MODEL_PATH), true);
        log.info("모델 학습 및 저장이 완료되었습니다: {}", MODEL_PATH);
    }
}
```

### 4.2 주요 코드 포인트
- **`@Service`**: 이 클래스가 비즈니스 로직을 수행하는 서비스 객체임을 Spring에게 알립니다.
- **`@EventListener(ApplicationReadyEvent.class)`**: 애플리케이션 구동이 완료된 시점에 `initModel()` 메서드를 자동으로 호출합니다.
- **`File.exists()`**: 중복 학습을 방지하기 위해 파일 존재 여부를 먼저 체크합니다.
- **`log.info()`**: 학습 진행 상황을 콘솔 창에 출력하여 사용자가 알 수 있게 합니다.

---

## 5. 확인 사항

- 서버를 실행했을 때 콘솔 창에 "모델 파일이 없습니다. 학습을 시작합니다..."라는 로그가 출력되나요?
- 학습이 끝난 후 `src/main/resources/` 폴더에 `iris-model.zip` 파일이 생겼나요?
- 서버를 껐다가 다시 켰을 때는 "자동 학습을 건너뜁니다"라는 로그가 나오는지 확인해 보세요.

---



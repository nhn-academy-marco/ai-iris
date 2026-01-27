# Step 1: 프로젝트 기초 설정 (Setup)

이 단계에서는 JVM 기반의 머신러닝 엔진인 **Deeplearning4j(DL4J)** 를 구동하기 위한 환경을 구축하고, Spring Boot 프로젝트의 기본 구조를 설정합니다.

## 0. 주요 개념 정리

처음 접하는 학생들을 위해 프로젝트에서 사용되는 주요 용어들을 간단히 설명합니다.

- **머신러닝 (Machine Learning)**: 컴퓨터가 데이터를 통해 스스로 학습하고 패턴을 찾아내어 의사결정이나 예측을 수행하게 하는 기술입니다. 명시적인 프로그래밍 없이도 데이터를 기반으로 규칙을 만들어냅니다.
- **딥러닝 (Deep Learning)**: 머신러닝의 한 분야로, 인간의 뇌 구조를 모방한 **인공 신경망(Artificial Neural Networks)** 을 사용하여 복잡한 데이터를 처리합니다. 이번 프로젝트에서 사용할 DL4J가 바로 이 딥러닝 라이브러리입니다. 
- **모델 학습 (Training)**: 준비된 데이터를 머신러닝 알고리즘에 입력하여 컴퓨터가 패턴을 배우게 하는 과정입니다. 학습이 완료된 결과물을 '모델'이라고 부릅니다.
- **추론 (Inference)**: 학습된 모델에 새로운 데이터를 입력하여 결과를 예측하는 과정입니다. 예를 들어, 꽃잎의 길이를 넣고 "이 꽃은 붓꽃(Iris)이다" 라고 맞추는 것이 추론입니다.

## 1. 학습 목표

- JVM 기반 AI 엔진 구동을 위한 의존성 설정 (`pom.xml`)
- 머신러닝 모델 학습 및 서비스를 위한 프로젝트 패키지 구조 설계

## 2. 의존성 설정 (`pom.xml`)

머신러닝 및 AI 기능을 구현하기 위해 `pom.xml`에 다음 의존성들을 추가해야 합니다.

### Deeplearning4j & ND4J

머신러닝 프로젝트를 위해 자바 진영에서 가장 널리 쓰이는 라이브러리인 DL4J와 ND4J를 사용합니다.

- **Deeplearning4j (DL4J)**: 자바(JVM) 환경에서 딥러닝 모델을 만들고 실행할 수 있게 해주는 라이브러리입니다.
- **ND4J (N-Dimensional Arrays for Java)**: 고성능 수치 연산을 위한 라이브러리입니다. 파이썬의 `NumPy`와 비슷한 역할을 하며, 복잡한 행렬 계산을 빠르게 처리해 줍니다.

```xml
<properties>
    <dl4j-master.version>1.0.0-M2.1</dl4j-master.version>
</properties>

<dependencies>
    <!-- DL4J NN 모델 라이브러리 -->
    <dependency>
        <groupId>org.deeplearning4j</groupId>
        <artifactId>deeplearning4j-core</artifactId>
        <version>${dl4j-master.version}</version>
    </dependency>

    <!-- ND4J CPU Backend (수치 연산 엔진) -->
    <dependency>
        <groupId>org.nd4j</groupId>
        <artifactId>nd4j-native-platform</artifactId>
        <version>${dl4j-master.version}</version>
    </dependency>
</dependencies>
```

### 기타 라이브러리
프로젝트 전체 진행을 위해 필요한 주요 라이브러리는 다음과 같습니다:
- **Spring Web / Thymeleaf**: 웹 인터페이스 및 API를 제공하기 위한 프레임워크와 템플릿 엔진입니다.
- **Lombok**: 반복되는 코드(Getter, Setter 등)를 자동으로 생성해 주는 유용한 도구입니다.
- **Google AI SDK** (Step 6에서 사용 예정): 구글의 최신 AI 모델인 Gemini와 통신하기 위해 사용합니다.

## 3. 패키지 구조 생성

효율적인 개발을 위해 다음과 같이 패키지를 구성하는 것을 권장합니다. 각 패키지는 역할별로 코드를 나누어 관리하기 위함입니다.

- `controller`: 사용자의 요청을 받고 응답을 돌려주는 곳 (창구 역할)
- `service`: 실제 머신러닝 모델을 사용해 예측 로직을 수행하는 곳 (핵심 비즈니스 로직)
- `dto` (Data Transfer Object): 데이터를 주고받을 때 사용하는 바구니 같은 객체

```text
src/main/java/com/nhnacademy/nhnacademyaiirisclassifier/
├── controller/          # REST API 및 View 컨트롤러
├── service/             # ML 모델 로드 및 예측 로직
├── dto/                 # 데이터 전송 객체 (IrisRequest, IrisResponse)
└── config/              # 설정 관련 클래스
```

## 4. 확인 사항
- `mvn clean install` 명령어를 실행하여 의존성이 정상적으로 다운로드되는지 확인합니다. (처음에는 시간이 다소 걸릴 수 있습니다.)
- `Deeplearning4j`와 `ND4J` 관련 라이브러리가 프로젝트 클래스패스에 포함되었는지 확인합니다.
- Spring Boot 애플리케이션이 정상적으로 구동되는지 실행해 봅니다.

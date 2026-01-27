# Iris Classifier

Spring Boot 기반의 로컬 머신러닝 모델(DL4J)을 활용한 붓꽃(Iris) 품종 분류 프로젝트입니다.

### 서비스 데모
- **데모 주소**: [http://ai.java21.net/iris/](http://ai.java21.net/iris/)

---

### 교육 커리큘럼 (Documentation)
이 프로젝트는 단계별 학습을 위한 가이드를 제공합니다. `/docs` 하위의 문서들을 통해 실습을 진행할 수 있습니다.

- **[전체 학습 가이드 (Index)](docs/index.md)**
- **[Step 1: 프로젝트 기초 설정](docs/step1.md)** - DL4J 및 Spring Boot 환경 구축
- **[Step 2: 데이터 통신 객체 설계 (DTO)](docs/step2.md)** - 클라이언트-서버 간 데이터 규약 정의
- **[Step 3: ML 모델 학습 및 저장](docs/step3.md)** - Java 기반 신경망 학습 및 모델 직렬화
- **[Step 4: 예측 서비스 엔진 개발](docs/step4.md)** - 모델 역직렬화 및 실시간 추론 API 구현
- **[Step 5: 시각화 대시보드 구현](docs/step5.md)** - Chart.js를 이용한 결과 시각화

---

### 주요 기능
1. **로컬 머신러닝 추론**: 서버 실행 시 자동으로 학습된 신경망 모델을 로드하여 붓꽃 품종(Setosa, Versicolor, Virginica)을 예측합니다.
2. **동적 데이터 시각화**: 예측된 확률 데이터를 Chart.js를 통해 막대 그래프로 즉시 시각화합니다.
3. **비동기 통신**: Fetch API를 사용하여 페이지 새로고침 없이 빠른 예측 결과를 제공합니다.
4. **예제 데이터 제공**: 사용자가 쉽게 테스트해 볼 수 있도록 품종별 전형적인 데이터 입력 버튼을 제공합니다.

---

### 기술 스택
- **Backend**: Java 21, Spring Boot 3.4.2
- **Machine Learning**: Deeplearning4j (DL4J), ND4J
- **Frontend**: Thymeleaf, HTML5/CSS3, JavaScript (ES6+)
- **Visualization**: Chart.js 4.x
- **Build Tool**: Maven

---

### 시작하기

#### 요구 사항
- JDK 21 이상
- Maven

#### 실행 방법
1. 저장소를 클론합니다.
2. 프로젝트 루트 폴더에서 다음 명령어를 실행합니다.
   ```bash
   ./mvnw spring-boot:run
   ```
3. 브라우저에서 `http://localhost:8080`에 접속합니다.

---

### 테스트
프로젝트의 안정성을 확인하기 위해 통합 테스트를 포함하고 있습니다.
```bash
./mvnw test
```
- `IrisIntegrationTest`: 실제 모델 로드부터 API 응답까지 전체 흐름을 검증합니다.

# Project: Iris Classifier & Gemini AI Insight

**Spring Boot 기반의 로컬 머신러닝 및 생성형 AI 결합 서비스**

## 학습목표

1. **Java 기반 머신러닝 구현**
    - DL4J 프레임워크를 활용한 신경망 모델 설계 및 학습
    - 모델 직렬화/역직렬화를 통한 영구 저장 및 로드
    - 실시간 추론(Inference) 엔진 구현

2. **Spring MVC 웹 서비스 개발**
    - RESTful API 설계 및 구현
    - DTO 패턴을 통한 데이터 캡슐화
    - 비동기 통신 처리

3. **데이터 시각화 및 프론트엔드 통합**
    - Chart.js를 활용한 동적 그래프 구현
    - Thymeleaf 템플릿 엔진 활용
    - JavaScript Fetch API 기반 비동기 통신

4. **생성형 AI 서비스 연동**
    - Google Gemini API 통합
    - 프롬프트 엔지니어링 기법 습득
    - AI 응답 처리 및 동적 렌더링

## **Step 1: 프로젝트 기초 설정 (Setup)**

* **목표:** JVM 기반 AI 엔진 구동을 위한 환경 구축.
* **내용:** `pom.xml` 설정(DL4J, ND4J) 및 스프링 부트 기본 패키지 구조 생성.

## **Step 2: 데이터 통신 객체 설계 (DTO)**

* **목표:** 클라이언트-서버 간 데이터 약속 정의.
* **내용:** `IrisRequest`(입력 수치)와 `IrisResponse`(예측 결과 및 확률) 객체 설계.

## **Step 3: ML 모델 학습 및 저장 (Modeling)**

* **목표:** Java 코드로 인공신경망 학습 및 모델 파일 생성.
* **내용:** 신경망 설계, 학습(`fit`), `iris-model.zip` 직렬화 저장.

## **Step 4: 예측 서비스 엔진 개발 (Service & Controller)**

* **목표:** 저장된 모델을 로드하여 실시간 예측 API 구축.
* **내용:** `@PostConstruct`로 모델 로드, `INDArray` 행렬 연산을 통한 추론(Inference) 구현.

## **Step 5: 시각화 대시보드 구현 (Visualization)**

* **목표:** 예측 데이터를 차트로 시각화하여 사용자에게 전달.
* **내용:** * Thymeleaf 입력 폼 및 Fetch API 비동기 통신.
* **Chart.js**를 활용하여 품종별 확률 분포를 막대그래프로 시각화.


* **학습 포인트:** 수치 데이터를 직관적인 그래프로 변환하는 프론트엔드 연동 기술.

## **Step 6: Gemini AI 전문가 해설 연동 (GenAI Insight)**

* **목표:** LLM을 활용하여 예측 결과에 풍부한 스토리텔링 추가.
* **내용:** * **Gemini API 연동:** 예측된 품종명을 기반으로 상세 해설 요청.
* **Prompt Engineering:** "전문 식물학자" 페르소나를 부여하여 재배 팁, 꽃말 등의 콘텐츠 생성.
* **응답 처리:** 차트 아래에 Gemini가 생성한 텍스트 가이드를 동적으로 렌더링.


* **학습 포인트:** 외부 생성형 AI API 연동 방식과 프롬프트 엔지니어링의 기초.

---

## 단계별 학습 요약 및 참고 링크

| 단계 | 핵심 키워드 | 관련 링크 |
| --- | --- | --- |
| **Step 1-4** | DL4J, ND4J, Inference | [DL4J Examples](https://github.com/eclipse/deeplearning4j-examples) |
| **Step 5** | Chart.js, Async Fetch | [Chart.js Samples](https://www.chartjs.org/docs/latest/samples/bar/vertical.html) |
| **Step 6** | Gemini API, Prompt | [Google AI Studio](https://aistudio.google.com/) |

---

## 교육적 기대효과

이 커리큘럼을 통해 학생들은 **"정적인 수치 예측"** 에서 시작해 **"동적인 데이터 시각화"** 를 거쳐, 마지막에는 **"대화형 AI 서비스"** 로 진화하는 과정을 경험하게 됩니다. 이는 현대적인 백엔드 개발자가 갖춰야 할 통합적인 시각을 길러줍니다.

---


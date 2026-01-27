# Step 5: 시각화 대시보드 구현 (Visualization)

이 단계에서는 사용자가 웹 브라우저에서 직접 데이터를 입력하고, 예측 결과를 차트로 시각화하여 한눈에 확인할 수 있는 **웹 대시보드**를 구현합니다.

## 0. 주요 개념 정리 (Vocabulary)

- **Thymeleaf**: 스프링 부트에서 권장하는 서버 사이드 템플릿 엔진입니다. HTML 파일에 자바 데이터를 바인딩하여 동적인 페이지를 생성합니다.
- **Chart.js**: HTML5 Canvas를 활용하여 직관적이고 인터랙티브한 차트를 그릴 수 있게 해주는 오픈소스 JavaScript 라이브러리입니다.
- **Fetch API**: 자바스크립트를 사용하여 서버와 비동기적으로 통신(HTTP 요청)할 수 있게 해주는 최신 인터페이스입니다. 페이지 전체를 새로고침하지 않고도 데이터를 주고받을 수 있습니다.
- **Async/Await**: 자바스크립트에서 비동기 처리를 마치 동기적인 코드처럼 읽기 쉽고 깔끔하게 작성할 수 있게 해주는 문법입니다.

---

## 1. 학습 목표

- Thymeleaf를 활용한 사용자 입력 폼(Form) 구성
- Fetch API를 이용한 비동기 POST 요청 처리 및 JSON 데이터 파싱
- Chart.js를 이용한 확률 데이터의 시각화(막대 그래프)
- 정적 리소스(CSS, JS) 및 템플릿 파일의 배치 이해

---

## 2. 컨트롤러 확장 (View Mapping)

기존의 `IrisController`는 JSON 데이터를 반환하는 `@RestController`였습니다. 웹 페이지를 보여주기 위해 일반적인 `@Controller`를 추가하거나, 기존 컨트롤러에 뷰를 반환하는 메서드를 추가할 수 있습니다. 여기서는 `src/main/java/com/nhnacademy/nhnacademyaiirisclassifier/controller/IrisViewController.java`를 생성하여 화면 이동을 처리합니다.

```java
package com.nhnacademy.nhnacademyaiirisclassifier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IrisViewController {

    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html 파일을 찾아 렌더링합니다.
    }
}
```

---

## 3. 프론트엔드 구현

### 3.1 HTML 템플릿 (index.html)

`src/main/resources/templates/index.html` 파일을 생성하고, 입력 폼과 차트가 표시될 영역을 구성합니다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Iris Classifier Dashboard</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
        .input-group { margin-bottom: 10px; }
        label { display: inline-block; width: 250px; }
        #chart-container { width: 100%; height: 400px; margin-top: 30px; }
        .result-info { margin-top: 20px; font-weight: bold; color: #2c3e50; }
    </style>
</head>
<body>
    <h1>Iris Species Classifier</h1>
    
    <div id="input-form">
        <div class="input-group">
            <label>Example Cases:</label>
            <button type="button" onclick="fillAndPredict(5.1, 3.5, 1.4, 0.2)">Setosa</button>
            <button type="button" onclick="fillAndPredict(6.1, 2.8, 4.0, 1.3)">Versicolor</button>
            <button type="button" onclick="fillAndPredict(6.3, 3.3, 6.0, 2.5)">Virginica</button>
        </div>
        <hr>
        <div class="input-group">
            <label>Sepal Length (꽃받침 길이):</label>
            <input type="number" id="sepalLength" step="0.1" value="5.1">
        </div>
        <div class="input-group">
            <label>Sepal Width (꽃받침 너비):</label>
            <input type="number" id="sepalWidth" step="0.1" value="3.5">
        </div>
        <div class="input-group">
            <label>Petal Length (꽃잎 길이):</label>
            <input type="number" id="petalLength" step="0.1" value="1.4">
        </div>
        <div class="input-group">
            <label>Petal Width (꽃잎 너비):</label>
            <input type="number" id="petalWidth" step="0.1" value="0.2">
        </div>
        <button onclick="predict()">예측하기</button>
    </div>

    <div id="result-area" style="display:none;">
        <p class="result-info">예측 결과: <span id="predictedSpecies">-</span></p>
        <div id="chart-container">
            <canvas id="probabilityChart"></canvas>
        </div>
    </div>

    <hr style="margin-top: 50px;">
    <div id="project-info">
        <h3>프로젝트 소개</h3>
        <p>
            이 프로젝트는 <strong>Spring Boot</strong>와 <strong>Deeplearning4j(DL4J)</strong>를 활용한 로컬 머신러닝 서비스 구현 예제입니다.
            전통적인 붓꽃(Iris) 데이터셋을 사용하여 꽃받침(Sepal)과 꽃잎(Petal)의 크기에 따라 품종을 분류하는 신경망 모델을 학습하고 추론합니다.
        </p>
        <h3>데모 설명</h3>
        <ul>
            <li><strong>예제 데이터 입력:</strong> 상단의 버튼을 클릭하면 각 품종별 전형적인 측정값이 자동으로 입력됩니다.</li>
            <li><strong>실시간 예측:</strong> '예측하기' 버튼을 누르면 서버에 저장된 DL4J 모델이 실시간으로 추론을 수행합니다.</li>
            <li><strong>결과 시각화:</strong> 예측된 결과와 함께 각 품종별 확률 분포가 Chart.js를 통해 막대 그래프로 표시됩니다.</li>
            <li><strong>소스 코드:</strong> 본 프로젝트의 소스 코드는 <a href="https://github.com/nhn-academy-marco/ai-iris" target="_blank">GitHub 저장소</a>에서 확인할 수 있습니다.</li>
        </ul>
    </div>

    <script src="/js/iris-predict.js"></script>
</body>
</html>
```

### 3.2 JavaScript 비동기 통신 및 시각화 (iris-predict.js)

`src/main/resources/static/js/iris-predict.js` 파일을 생성하고 서버와의 통신 및 차트 렌더링 로직을 작성합니다.

```javascript
let chart = null;

function fillAndPredict(sl, sw, pl, pw) {
    document.getElementById('sepalLength').value = sl;
    document.getElementById('sepalWidth').value = sw;
    document.getElementById('petalLength').value = pl;
    document.getElementById('petalWidth').value = pw;
    predict();
}

async function predict() {
    const data = {
        sepalLength: document.getElementById('sepalLength').value,
        sepalWidth: document.getElementById('sepalWidth').value,
        petalLength: document.getElementById('petalLength').value,
        petalWidth: document.getElementById('petalWidth').value
    };

    try {
        const response = await fetch('/api/iris/predict', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        
        // 결과 텍스트 업데이트
        document.getElementById('result-area').style.display = 'block';
        document.getElementById('predictedSpecies').innerText = result.predictedSpecies;

        // 차트 그리기
        renderChart(result.probabilities);

    } catch (error) {
        console.error('Error:', error);
        alert('예측 중 오류가 발생했습니다.');
    }
}

function renderChart(probabilities) {
    const ctx = document.getElementById('probabilityChart').getContext('2d');
    const labels = Object.keys(probabilities);
    const values = Object.values(probabilities);

    if (chart) {
        chart.destroy(); // 기존 차트가 있으면 삭제 후 새로 생성
    }

    chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '품종별 확률',
                data: values,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.5)',
                    'rgba(54, 162, 235, 0.5)',
                    'rgba(75, 192, 192, 0.5)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(75, 192, 192, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: { beginAtZero: true, max: 1 }
            }
        }
    });
}
```

---

## 4. 코드 상세 분석

1.  **`Fetch API`**: `fetch('/api/iris/predict', ...)`를 통해 Step 4에서 만든 REST API를 호출합니다. 데이터를 JSON 문자열로 변환하여 보내고, 서버의 응답 역시 JSON으로 받아 처리합니다.
2.  **`Chart.js 렌더링`**: `new Chart(ctx, {...})`를 통해 캔버스에 그래프를 그립니다. `probabilities` 객체의 키(Setosa, Versicolor, Virginica)를 라벨로, 값(확률)을 데이터로 사용합니다.
3.  **`chart.destroy()`**: 새로운 예측을 할 때마다 기존 차트 객체를 제거하지 않으면 그래프가 겹쳐서 나타날 수 있습니다.
4.  **`Thymeleaf 정적 리소스`**: 스프링 부트는 기본적으로 `src/main/resources/static` 폴더 아래의 파일들을 `/` 경로로 매핑합니다. 따라서 자바스크립트 파일은 `<script src="/js/iris-predict.js"></script>`와 같이 불러올 수 있습니다.

---

## 5. 확인 사항 및 테스트

1.  애플리케이션을 실행하고 브라우저에서 `http://localhost:8080/`에 접속해 보세요.
2.  4가지 수치를 입력하고 '예측하기' 버튼을 눌렀을 때, 하단에 예측 결과와 함께 막대 그래프가 나타나나요?
3.  브라우저 개발자 도구(F12)의 **Network** 탭에서 `/api/iris/predict` 요청이 정상적으로 가고 오는지 확인해 보세요.
4.  서로 다른 수치를 입력하며 그래프의 높이가 실시간으로 변하는지 확인해 보세요.

---

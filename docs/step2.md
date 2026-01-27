# Step 2: 데이터 통신 객체 설계 (DTO)

이 단계에서는 클라이언트(사용자 브라우저)와 서버 간에 데이터를 어떻게 주고받을지 약속하고, 그 데이터를 담을 그릇인 **DTO(Data Transfer Object)** 를 설계합니다.

## 0. 주요 개념 정리

처음 개발을 접하는 학생들을 위해 데이터 통신에 필요한 개념들을 설명합니다.

- **DTO (Data Transfer Object)**: '데이터 전송 객체'라는 뜻으로, 프로세스 간에 데이터를 전달하기 위해 사용되는 객체입니다. 비유하자면, 택배를 보낼 때 물건을 안전하게 담아 보내는 **'택배 상자'** 와 같습니다.
- **클라이언트 (Client)**: 서비스를 사용하는 사용자(브라우저 등)를 말합니다.
- **서버 (Server)**: 클라이언트의 요청을 받아 머신러닝 예측과 같은 서비스를 제공하는 시스템을 말합니다.
- **어노테이션 (Annotation)**: `@Getter`, `@Setter`와 같이 `@`로 시작하는 코드로, 자바 프로그램에 추가적인 정보를 제공하거나 특정 기능을 자동으로 생성해 줍니다.

## 1. 학습 목표

- 클라이언트와 서버 간의 데이터 규약 이해
- Lombok 라이브러리를 활용한 효율적인 DTO 클래스 작성
- `IrisRequest`(입력 수치)와 `IrisResponse`(예측 결과) 객체 설계

## 2. 왜 DTO가 필요한가요?

단순히 변수 몇 개를 전달하는 것이 아니라, DTO라는 상자에 담아 전달하는 이유는 다음과 같습니다.

1. **데이터 규격화**: "반드시 이 형식으로 데이터를 보내야 한다"라는 약속을 정할 수 있습니다.
2. **캡슐화**: 내부적인 데이터 구조를 숨기고, 딱 필요한 정보만 외부에 노출할 수 있습니다.
3. **효율성**: 여러 개의 데이터를 하나의 객체에 묶어서 한 번에 전달할 수 있어 통신 횟수를 줄일 수 있습니다.

## 3. Lombok 어노테이션 이해하기

우리는 반복적인 코드(Getter, Setter, 생성자 등)를 직접 작성하지 않고 **Lombok**을 사용하여 코드를 깔끔하게 유지할 것입니다.

- `@Value`: 불변(Immutable) 객체를 만들 때 사용합니다. `@Getter`, `@AllArgsConstructor`, `@EqualsAndHashCode`, `@ToString` 등을 한 번에 적용하며, 모든 필드를 `private final`로 만듭니다.
- `@NoArgsConstructor`: 파라미터가 없는 기본 생성자를 만들어 줍니다. (JSON 직렬화/역직렬화 시 필요할 수 있습니다.)
- `@Builder`: 복잡한 객체를 생성할 때 가독성 좋게 객체를 만들 수 있도록 도와줍니다.

## 4. DTO 설계 및 구현

`com.nhnacademy.nhnacademyaiirisclassifier.dto` 패키지에 다음 두 클래스를 생성합니다.

### 4.1 IrisRequest (사용자가 입력하는 데이터)

사용자가 입력한 붓꽃의 수치 정보(꽃받침, 꽃잎의 길이와 너비)를 담는 객체입니다. 데이터의 무결성을 위해 `@Value`를 사용하여 불변 객체로 설계합니다.

```java
package com.nhnacademy.nhnacademyaiirisclassifier.dto;

import lombok.Value;

@Value
public class IrisRequest {
    // 붓꽃(Iris) 예측에 필요한 4가지 특징(Feature)
    double sepalLength; // 꽃받침 길이
    double sepalWidth;  // 꽃받침 너비
    double petalLength; // 꽃잎 길이
    double petalWidth;  // 꽃잎 너비
}
```

### 4.2 IrisResponse (서버가 돌려주는 예측 결과)

머신러닝 모델이 예측한 품종 이름과 각 품종별 확률 분포를 담는 객체입니다.

```java
package com.nhnacademy.nhnacademyaiirisclassifier.dto;

import lombok.Value;
import java.util.Map;

@Value
public class IrisResponse {
    String predictedSpecies;      // 예측된 품종 명 (예: Setosa)
    Map<String, Double> probabilities; // 각 품종별 확률 (예: {Setosa: 0.95, ...})
}
```

## 5. 확인 사항

- `dto` 패키지 안에 클래스들이 정상적으로 생성되었나요?
- Lombok 어노테이션을 사용할 때 빨간 줄(에러)이 뜨지 않나요? (IDE에 Lombok 플러그인이 설치되어 있어야 합니다.)
- 각 필드의 데이터 타입(`double`, `String`, `Map`)이 설계 의도와 맞는지 확인해 보세요.

---

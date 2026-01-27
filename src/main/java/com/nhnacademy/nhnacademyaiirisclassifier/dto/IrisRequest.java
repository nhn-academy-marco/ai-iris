package com.nhnacademy.nhnacademyaiirisclassifier.dto;

import lombok.Value;

/**
 * @Value는 불변(Immutable) 클래스를 생성하는 Lombok 어노테이션입니다.
 * 다음과 같은 기능을 자동으로 생성합니다:
 * - 모든 필드를 private final로 생성
 * - 모든 필드를 포함하는 생성자
 * - 각 필드의 getter 메서드
 * - equals(), hashCode(), toString() 메서드
 * - setter 메서드는 생성하지 않아 불변성 보장
 */
@Value
public class IrisRequest {
    // 붓꽃(Iris) 예측에 필요한 4가지 특징(Feature)
    double sepalLength; // 꽃받침 길이
    double sepalWidth;  // 꽃받침 너비
    double petalLength; // 꽃잎 길이
    double petalWidth;  // 꽃잎 너비
}

package com.nhnacademy.nhnacademyaiirisclassifier.dto;


import lombok.Value;

import java.util.Map;

@Value
public class IrisResponse {
    String predictedSpecies;      // 예측된 품종 명 (예: Setosa)
    Map<String, Double> probabilities; // 각 품종별 확률 (예: {Setosa: 0.95, ...})
}
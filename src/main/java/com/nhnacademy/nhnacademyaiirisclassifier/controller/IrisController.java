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

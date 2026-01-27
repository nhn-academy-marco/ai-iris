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

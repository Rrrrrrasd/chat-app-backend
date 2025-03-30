package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-error")
    public String triggerError() {
        try {
            throw new RuntimeException("강제 에러 발생");
        } catch (Exception e) {
            // 예외를 다시 던지거나 필요한 처리를 해줄 수 있습니다.
            throw e; // 예외를 다시 던져서 @ControllerAdvice로 처리되게 할 수 있습니다.
        }
    }
}

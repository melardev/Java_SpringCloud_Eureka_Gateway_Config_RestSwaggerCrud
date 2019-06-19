package com.melardev.spring.cloud.gateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping
    ResponseEntity<Map<String, Object>> fallbackAction() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("full_messages", new String[]{"[Gateway] Something went wrong"});
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

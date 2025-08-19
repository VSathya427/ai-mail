package com.email.aimail.utils;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://mail.google.com")
@RequestMapping("/api/email")
@AllArgsConstructor
public class GenerateEmailController {
    private final GenerateEmailService generateEmailService;
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody RequestEmail requestEmail){
        String response = generateEmailService.generateEmailReply(requestEmail);
        return ResponseEntity.ok(response);
    }
}

package com.email.aimail.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GenerateEmailService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String apiUrl;
    @Value("${gemini.api.key}")
    private String apiKey;

    public GenerateEmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(RequestEmail requestEmail){
        //Prompt Building
        String prompt = buildPrompt(requestEmail);
        //Craft a request
        Map<String,Object> requestBody = Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                            Map.of("text",prompt)
                        })
                }
        );
        //Do request and get response
        String response = webClient.post()
                .uri(apiUrl+apiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //Extract response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            return "Error processing request:" + e.getMessage();
        }
    }

    private String buildPrompt(RequestEmail requestEmail) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional yet human email reply for the following email content. Also do not provide the subject line for the email reply.");
        if(requestEmail.getEmailTone() != null && requestEmail.getEmailTone().isEmpty()){
            prompt.append("Use a ").append(requestEmail.getEmailTone()).append(" yet humane tone");
        }
        prompt.append("\nOriginal email :\n").append(requestEmail.getEmailContent());
        return prompt.toString();
    }
}

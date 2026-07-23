package com.charging.controller.miniapp;

import com.charging.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "小程序端-AI客服接口")
@Slf4j
@RestController
@RequestMapping("/miniapp/ai")
public class MiniAIController {

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.api-url:}")
    private String apiUrl;

    @Value("${ai.model:deepseek-chat}")
    private String model;

    @Value("${ai.system-prompt:你是一个专业的新能源充电站客服助手。}")
    private String systemPrompt;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Operation(summary = "AI客服对话")
    @PostMapping("/chat")
    public R<Map<String, String>> chat(@RequestBody Map<String, String> params) {
        String message = params.get("message");
        if (message == null || message.isEmpty()) {
            return R.error("消息不能为空");
        }

        if (apiKey == null || apiKey.isEmpty() || "your-api-key".equals(apiKey)) {
            Map<String, String> reply = new HashMap<>();
            reply.put("reply", "AI客服暂未开通，请配置API Key后使用。");
            return R.ok(reply);
        }

        try {
            String replyText = callAI(message);
            Map<String, String> result = new HashMap<>();
            result.put("reply", replyText);
            return R.ok(result);
        } catch (Exception e) {
            log.error("AI调用失败: {}", e.getMessage(), e);
            Map<String, String> reply = new HashMap<>();
            reply.put("reply", "AI服务异常：" + e.getMessage());
            return R.ok(reply);
        }
    }

    private String callAI(String userMessage) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", false);

        java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        body.put("messages", messages);

        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
        log.debug("AI请求: {}", json);

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IOException("AI接口返回空响应");
            }
            String responseBody = response.body().string();
            log.debug("AI响应: {}", responseBody);

            if (!response.isSuccessful()) {
                throw new IOException("AI接口HTTP错误: " + response.code() + ", body: " + responseBody);
            }

            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
            if (root.has("error")) {
                throw new IOException("AI接口错误: " + root.path("error").path("message").asText(responseBody));
            }
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }
}

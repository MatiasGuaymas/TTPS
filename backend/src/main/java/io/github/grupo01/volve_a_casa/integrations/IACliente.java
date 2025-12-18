package io.github.grupo01.volve_a_casa.integrations;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class IACliente {
    @Value("${groq.api.key}")
    private String groqApiKey;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public String ask (String prompt) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\"model\": \"llama-3.3-70b-versatile\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + groqApiKey)
                .post(okhttp3.RequestBody.create(requestBody, mediaType))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            
            JSONObject jsonObject = new JSONObject(responseBody);
            
            if (jsonObject.has("error")) {
                String errorMessage = jsonObject.getJSONObject("error").getString("message");
                throw new IOException("Groq API Error: " + errorMessage);
            }
            
            if (!jsonObject.has("choices")) {
                throw new IOException("Invalid Groq response: " + responseBody);
            }

            return jsonObject.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content").trim();
        }
    }

}

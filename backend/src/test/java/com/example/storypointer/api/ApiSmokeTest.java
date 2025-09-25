package com.example.storypointer.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApiSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void endToEndPredict() throws Exception {
        String uploadResponse = mockMvc.perform(post("/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceUrl\":\"https://example.com/video.mp4\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode uploadJson = objectMapper.readTree(uploadResponse);
        String videoId = uploadJson.get("videoId").asText();

        // wait for background transcription
        for (int i = 0; i < 10; i++) {
            String statusResponse = mockMvc.perform(get("/videos/" + videoId + "/status"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            if (objectMapper.readTree(statusResponse).get("status").asText().equals("ready")) {
                break;
            }
            Thread.sleep(200);
        }

        String predictResponse = mockMvc.perform(post("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"videoId\":\"" + videoId + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode predictJson = objectMapper.readTree(predictResponse);
        assertThat(predictJson.get("prediction").asText()).isNotBlank();
        assertThat(predictJson.get("highlights").isArray()).isTrue();
    }
}

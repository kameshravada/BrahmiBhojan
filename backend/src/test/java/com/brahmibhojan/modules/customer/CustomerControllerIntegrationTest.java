package com.brahmibhojan.modules.customer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDefaultUserAndAllowProfileUpdate() throws Exception {
        Map<String, Object> requestOtpPayload = Map.of("mobile", "+919888777666");

        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOtpPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.challengeId").isNotEmpty())
                .andExpect(jsonPath("$.otpPreview").isNotEmpty())
                .andReturn();

        Map<String, Object> otpResponse = objectMapper.readValue(
                otpRequestResult.getResponse().getContentAsString(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> verifyOtpPayload = Map.of(
                "challengeId", otpResponse.get("challengeId").toString(),
                "mobile", "+919888777666",
                "otp", otpResponse.get("otpPreview").toString()
        );

        MvcResult verifyResult = mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyOtpPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        Map<String, Object> authResponse = objectMapper.readValue(
                verifyResult.getResponse().getContentAsString(),
                new TypeReference<Map<String, Object>>() {
                }
        );
        String accessToken = authResponse.get("accessToken").toString();

        mockMvc.perform(get("/api/v1/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("BB user"));

        Map<String, Object> updateProfilePayload = Map.of(
                "fullName", "Kamesh",
                "email", "kamesh@example.com"
        );

        mockMvc.perform(patch("/api/v1/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfilePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Kamesh"))
                .andExpect(jsonPath("$.email").value("kamesh@example.com"));
    }
}



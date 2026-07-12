package com.brahmibhojan.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void otpRequestAndVerificationShouldSucceed() throws Exception {
        Map<String, Object> requestOtpPayload = Map.of(
                "mobile", "+919999888777"
        );

        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOtpPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.challengeId").isNotEmpty())
                .andExpect(jsonPath("$.otpPreview").isNotEmpty())
                .andReturn();

        Map<String, Object> otpResponse = objectMapper.readValue(
                otpRequestResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String challengeId = otpResponse.get("challengeId").toString();
        String otpPreview = otpResponse.get("otpPreview").toString();

        Map<String, Object> verifyOtpPayload = Map.of(
                "challengeId", challengeId,
                "mobile", "+919999888777",
                "otp", otpPreview
        );

        mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyOtpPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.mobile").value("+919999888777"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void refreshAndLogoutShouldRevokeToken() throws Exception {
        Map<String, Object> requestOtpPayload = Map.of("mobile", "+919111222333");

        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOtpPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> otpResponse = objectMapper.readValue(
                otpRequestResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        Map<String, Object> verifyOtpPayload = Map.of(
                "challengeId", otpResponse.get("challengeId").toString(),
                "mobile", "+919111222333",
                "otp", otpResponse.get("otpPreview").toString()
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyOtpPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        Map<String, Object> loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String refreshToken = loginResponse.get("refreshToken").toString();

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        Map<String, Object> refreshResponse = objectMapper.readValue(
                refreshResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String rotatedRefreshToken = refreshResponse.get("refreshToken").toString();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", rotatedRefreshToken))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", rotatedRefreshToken))))
                .andExpect(status().isUnauthorized());
    }
}


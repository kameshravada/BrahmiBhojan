package com.brahmibhojan.modules.notifications;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.repository.NotificationPreferenceRepository;
import com.brahmibhojan.modules.users.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldAllowAuthenticatedUserToUpdatePreferenceAndReadNotifications() throws Exception {
        String mobile = "+919555333777";
        String accessToken = loginAndGetAccessToken(mobile);

        mockMvc.perform(patch("/api/v1/me/notifications/preferences")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "channel", "EMAIL",
                                "marketingEnabled", true
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.marketingEnabled").value(true));

        var user = userRepository.findByMobile(mobile).orElseThrow();
        var preference = notificationPreferenceRepository
                .findByUserIdAndChannel(user.getId(), NotificationChannel.EMAIL)
                .orElseThrow();
        assertThat(preference.isMarketingEnabled()).isTrue();

        mockMvc.perform(get("/api/v1/me/notifications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private String loginAndGetAccessToken(String mobile) throws Exception {
        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "mobile", mobile,
                                "deviceId", "notification-test-device"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> otpResponse = objectMapper.readValue(
                otpRequestResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        MvcResult verifyResult = mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "challengeId", otpResponse.get("challengeId"),
                                "mobile", mobile,
                                "otp", otpResponse.get("otpPreview"),
                                "deviceId", "notification-test-device"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> verifyResponse = objectMapper.readValue(
                verifyResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        return verifyResponse.get("accessToken").toString();
    }
}

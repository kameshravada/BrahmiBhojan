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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private static final String TEST_MOBILE = "+919888777666";

    @Test
    void shouldCreateDefaultUserAndAllowProfileUpdate() throws Exception {
        String accessToken = loginAndGetAccessToken(TEST_MOBILE);

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

    @Test
    void shouldSupportAddressCrudAndDefaultSwitching() throws Exception {
        String accessToken = loginAndGetAccessToken("+919777666555");

        String firstAddressId = createAddress(accessToken, "Home", "+919777666555", true);
        String secondAddressId = createAddress(accessToken, "Office", "+919777666555", false);

        // Switch default to second address and verify only one default remains.
        mockMvc.perform(put("/api/v1/me/addresses/{addressId}", secondAddressId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "recipientName", "Office",
                                "phoneNumber", "+919777666555",
                                "line1", "Street 22",
                                "line2", "Floor 2",
                                "landmark", "Near Park",
                                "city", "Hyderabad",
                                "state", "Telangana",
                                "country", "India",
                                "postalCode", "500002",
                                "isDefault", true
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));

        MvcResult addressesResult = mockMvc.perform(get("/api/v1/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, Object>> addresses = objectMapper.readValue(
                addressesResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        long defaultCount = addresses.stream()
                .filter(address -> Boolean.TRUE.equals(address.get("isDefault")))
                .count();
        assertThat(defaultCount).isEqualTo(1);
        assertThat(addresses.stream().anyMatch(address -> secondAddressId.equals(address.get("addressId"))
                && Boolean.TRUE.equals(address.get("isDefault")))).isTrue();

        mockMvc.perform(delete("/api/v1/me/addresses/{addressId}", firstAddressId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        MvcResult afterDeleteResult = mockMvc.perform(get("/api/v1/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, Object>> addressesAfterDelete = objectMapper.readValue(
                afterDeleteResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(addressesAfterDelete).hasSize(1);
        assertThat(addressesAfterDelete.getFirst().get("addressId")).isEqualTo(secondAddressId);
    }

    private String loginAndGetAccessToken(String mobile) throws Exception {
        Map<String, Object> requestOtpPayload = Map.of("mobile", mobile);

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
                "mobile", mobile,
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
        return authResponse.get("accessToken").toString();
    }

    private String createAddress(String accessToken, String recipientName, String phoneNumber, boolean isDefault) throws Exception {
        MvcResult addressResult = mockMvc.perform(post("/api/v1/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "recipientName", recipientName,
                                "phoneNumber", phoneNumber,
                                "line1", "Street 1",
                                "line2", "Area",
                                "landmark", "Near Park",
                                "city", "Hyderabad",
                                "state", "Telangana",
                                "country", "India",
                                "postalCode", "500001",
                                "isDefault", isDefault
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> addressResponse = objectMapper.readValue(
                addressResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        return addressResponse.get("addressId").toString();
    }
}



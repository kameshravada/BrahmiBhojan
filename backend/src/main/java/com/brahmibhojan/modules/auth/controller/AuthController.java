package com.brahmibhojan.modules.auth.controller;

import com.brahmibhojan.modules.auth.dto.AuthResponse;
import com.brahmibhojan.modules.auth.dto.LogoutRequest;
import com.brahmibhojan.modules.auth.dto.OtpRequestDto;
import com.brahmibhojan.modules.auth.dto.OtpRequestResponse;
import com.brahmibhojan.modules.auth.dto.OtpVerifyDto;
import com.brahmibhojan.modules.auth.dto.RefreshTokenRequest;
import com.brahmibhojan.modules.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/v1/auth/otp/request")
    @ResponseStatus(HttpStatus.CREATED)
    public OtpRequestResponse requestOtp(@Valid @RequestBody OtpRequestDto request, HttpServletRequest httpRequest) {
        return authService.requestOtp(request, httpRequest);
    }

    @PostMapping("/api/v1/auth/otp/verify")
    public AuthResponse verifyOtp(@Valid @RequestBody OtpVerifyDto request) {
        return authService.verifyOtpAndLogin(request);
    }

    @PostMapping("/api/v1/auth/refresh-token")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshSession(request);
    }

    @PostMapping("/api/v1/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }

}


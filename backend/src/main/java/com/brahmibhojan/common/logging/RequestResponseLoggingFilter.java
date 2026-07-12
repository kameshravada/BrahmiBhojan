package com.brahmibhojan.common.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "otp", "otppreview", "accesstoken", "refreshtoken", "authorization", "password", "token"
    );

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (isMultipart(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String traceId = UUID.randomUUID().toString();
        Instant startedAt = Instant.now();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        log.info("trace={} incoming method={} path={}", traceId, request.getMethod(), request.getRequestURI());

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            String requestBody = extractRequestBody(wrappedRequest);
            String responseBody = extractResponseBody(wrappedResponse);

            log.info(
                    "trace={} outgoing status={} durationMs={} requestPayload={} responsePayload={}",
                    traceId,
                    wrappedResponse.getStatus(),
                    Instant.now().toEpochMilli() - startedAt.toEpochMilli(),
                    requestBody,
                    responseBody
            );
            wrappedResponse.copyBodyToResponse();
        }
    }

    private boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return StringUtils.hasText(contentType) && contentType.toLowerCase().contains("multipart/form-data");
    }

    private String extractRequestBody(ContentCachingRequestWrapper request) {
        if (isNonJsonContentType(request.getContentType())) {
            return "<non-json>";
        }
        String raw = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        return sanitize(raw);
    }

    private String extractResponseBody(ContentCachingResponseWrapper response) {
        if (isNonJsonContentType(response.getContentType())) {
            return "<non-json>";
        }
        String raw = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        return sanitize(raw);
    }

    private boolean isNonJsonContentType(String contentType) {
        return !StringUtils.hasText(contentType) || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String sanitize(String payload) {
        if (!StringUtils.hasText(payload)) {
            return "{}";
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            mask(root);
            String masked = objectMapper.writeValueAsString(root);
            return masked.length() > 2500 ? masked.substring(0, 2500) + "...<truncated>" : masked;
        } catch (Exception ignored) {
            return payload.length() > 2500 ? payload.substring(0, 2500) + "...<truncated>" : payload;
        }
    }

    private void mask(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            objectNode.fieldNames().forEachRemaining(field -> {
                JsonNode value = objectNode.get(field);
                if (SENSITIVE_FIELDS.contains(field.toLowerCase())) {
                    objectNode.put(field, "***");
                } else {
                    mask(value);
                }
            });
            return;
        }

        if (node instanceof ArrayNode arrayNode) {
            for (JsonNode item : arrayNode) {
                mask(item);
            }
        }
    }
}


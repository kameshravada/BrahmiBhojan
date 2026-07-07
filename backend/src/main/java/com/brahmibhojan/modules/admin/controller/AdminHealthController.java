package com.brahmibhojan.modules.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminHealthController {

    @GetMapping("/me")
    public Map<String, String> me(Principal principal) {
        return Map.of(
                "message", "Authenticated endpoint reached",
                "email", principal.getName()
        );
    }
}


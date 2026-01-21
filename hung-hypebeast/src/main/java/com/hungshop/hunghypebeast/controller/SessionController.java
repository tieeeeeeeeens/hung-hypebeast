package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.dto.response.SessionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping
    public SessionResponse createSession() {
        String sessionId = "sess-" + UUID.randomUUID();
        return new SessionResponse(sessionId);
    }
}

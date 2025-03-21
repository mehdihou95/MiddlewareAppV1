package com.xml.processor.controller;

import com.xml.processor.model.ProcessedFile;
import com.xml.processor.service.XmlProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class XmlProcessorController {
    private final XmlProcessorService service;

    public XmlProcessorController(XmlProcessorService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<ProcessedFile> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.processXmlFile(file));
    }

    @GetMapping("/files/processed")
    public ResponseEntity<List<ProcessedFile>> getProcessedFiles() {
        return ResponseEntity.ok(service.getProcessedFiles());
    }

    @GetMapping("/files/errors")
    public ResponseEntity<List<ProcessedFile>> getErrorFiles() {
        return ResponseEntity.ok(service.getErrorFiles());
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        return ResponseEntity.ok(Map.of(
            "username", authentication.getName(),
            "roles", authentication.getAuthorities(),
            "authenticated", authentication.isAuthenticated()
        ));
    }

    // Debug endpoint - remove in production
    @GetMapping("/debug/auth")
    public ResponseEntity<String> debugAuth(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("No authentication present");
        }
        return ResponseEntity.ok(String.format(
            "Auth: %s, Principal: %s, Credentials: %s, Details: %s",
            authentication.isAuthenticated(),
            authentication.getPrincipal(),
            "[PROTECTED]",
            authentication.getDetails()
        ));
    }
} 
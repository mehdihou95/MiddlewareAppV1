package com.xml.processor.controller;

import com.xml.processor.model.ProcessedFile;
import com.xml.processor.service.XmlProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

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
} 
package com.xml.processor.controller;

import com.xml.processor.config.ClientContextHolder;
import com.xml.processor.model.ProcessedFile;
import com.xml.processor.service.XmlProcessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final XmlProcessorService xmlProcessorService;

    public FileUploadController(XmlProcessorService xmlProcessorService) {
        this.xmlProcessorService = xmlProcessorService;
    }

    @PostMapping
    public ResponseEntity<ProcessedFile> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientId") Long clientId,
            @RequestParam("interfaceId") Long interfaceId) {
        
        // Set client context from parameters
        ClientContextHolder.setClientId(clientId);
        
        // Process file with interface ID
        ProcessedFile processedFile = xmlProcessorService.processXmlFile(file, interfaceId);
        return ResponseEntity.ok(processedFile);
    }
} 
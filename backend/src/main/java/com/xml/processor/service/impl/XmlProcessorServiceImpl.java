package com.xml.processor.service.impl;

import com.xml.processor.model.Interface;
import com.xml.processor.model.ProcessedFile;
import com.xml.processor.repository.InterfaceRepository;
import com.xml.processor.service.ProcessedFileService;
import com.xml.processor.service.XmlProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class XmlProcessorServiceImpl implements XmlProcessorService {

    @Autowired
    private ProcessedFileService processedFileService;

    @Autowired
    private InterfaceRepository interfaceRepository;

    @Override
    @Transactional
    public ProcessedFile processXmlFile(MultipartFile file, Long interfaceId) {
        try {
            // Get interface configuration
            Interface interfaceEntity = interfaceRepository.findById(interfaceId)
                .orElseThrow(() -> new RuntimeException("Interface not found with id: " + interfaceId));

            // Create processed file record
            ProcessedFile processedFile = new ProcessedFile();
            processedFile.setFileName(file.getOriginalFilename());
            processedFile.setInterfaceEntity(interfaceEntity);
            processedFile.setStatus("PROCESSING");
            processedFile.setProcessedAt(LocalDateTime.now());

            // Save initial record
            processedFile = processedFileService.createProcessedFile(processedFile);

            // TODO: Implement actual XML processing logic here
            // This would include:
            // 1. Validating XML against interface schema
            // 2. Applying mapping rules
            // 3. Transforming data
            // 4. Storing processed data

            // Update status to success
            processedFile.setStatus("SUCCESS");
            processedFile.setProcessedAt(LocalDateTime.now());
            return processedFileService.updateProcessedFile(processedFile.getId(), processedFile);

        } catch (Exception e) {
            // Create error record
            ProcessedFile errorFile = new ProcessedFile();
            errorFile.setFileName(file.getOriginalFilename());
            errorFile.setInterfaceEntity(interfaceRepository.findById(interfaceId).orElse(null));
            errorFile.setStatus("ERROR");
            errorFile.setErrorMessage(e.getMessage());
            errorFile.setProcessedAt(LocalDateTime.now());
            
            return processedFileService.createProcessedFile(errorFile);
        }
    }
} 
package com.xml.processor.service;

import com.xml.processor.config.ClientContextHolder;
import com.xml.processor.model.Interface;
import com.xml.processor.model.ProcessedFile;
import com.xml.processor.repository.ProcessedFileRepository;
import com.xml.processor.service.interfaces.InterfaceService;
import com.xml.processor.service.strategy.DocumentProcessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Service
public class XmlProcessorService {
    
    private static final Logger log = LoggerFactory.getLogger(XmlProcessorService.class);
    
    @Autowired
    private DocumentProcessingStrategyService strategyService;
    
    @Autowired
    private InterfaceService interfaceService;
    
    @Autowired
    private ProcessedFileRepository processedFileRepository;

    @Transactional
    public ProcessedFile processXmlFile(MultipartFile file) {
        try {
            // Parse XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.getInputStream());

            log.info("Processing XML file: {}", file.getOriginalFilename());

            // Get client ID from context
            Long clientId = ClientContextHolder.getClientId();
            if (clientId == null) {
                throw new RuntimeException("Client context not available");
            }
            
            // Detect interface
            String xmlContent = convertDocumentToString(document);
            Interface detectedInterface = interfaceService.detectInterface(xmlContent, clientId);
            
            if (detectedInterface == null) {
                throw new RuntimeException("Could not detect interface for XML document");
            }
            
            log.info("Detected interface: {}", detectedInterface.getName());
            
            // Get appropriate processing strategy
            DocumentProcessingStrategy strategy = strategyService.getStrategy(detectedInterface);
            
            if (strategy == null) {
                throw new RuntimeException("No processing strategy available for interface type: " 
                    + detectedInterface.getType());
            }
            
            // Process document using the selected strategy
            Map<String, Object> processedData = strategy.processDocument(document, detectedInterface, clientId);
            
            // Create processed file record
            ProcessedFile processedFile = new ProcessedFile();
            processedFile.setFileName(file.getOriginalFilename());
            processedFile.setStatus("SUCCESS");
            processedFile.setInterfaceEntity(detectedInterface);
            processedFile.setProcessedData(processedData);
            
            return processedFileRepository.save(processedFile);
            
        } catch (Exception e) {
            log.error("Error processing XML file: " + file.getOriginalFilename(), e);
            return saveProcessingStatus(file.getOriginalFilename(), false, e.getMessage());
        }
    }

    private String convertDocumentToString(Document document) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            log.error("Error converting document to string: {}", e.getMessage(), e);
            return null;
        }
    }

    private ProcessedFile saveProcessingStatus(String fileName, boolean success, String errorMessage) {
        ProcessedFile processedFile = new ProcessedFile();
        processedFile.setFileName(fileName);
        processedFile.setStatus(success ? "SUCCESS" : "ERROR");
        processedFile.setErrorMessage(errorMessage);
        return processedFileRepository.save(processedFile);
    }

    /**
     * Get all processed files
     * @return List of processed files
     */
    public List<ProcessedFile> getProcessedFiles() {
        return processedFileRepository.findByStatus("PROCESSED");
    }

    /**
     * Get all error files
     * @return List of error files
     */
    public List<ProcessedFile> getErrorFiles() {
        return processedFileRepository.findByStatus("ERROR");
    }
} 
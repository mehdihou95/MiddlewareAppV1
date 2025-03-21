package com.xml.processor.service.impl;

import com.xml.processor.config.ClientContextHolder;
import com.xml.processor.model.ProcessedFile;
import com.xml.processor.repository.ProcessedFileRepository;
import com.xml.processor.service.interfaces.ProcessedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProcessedFileServiceImpl implements ProcessedFileService {

    @Autowired
    private ProcessedFileRepository processedFileRepository;

    @Override
    @Transactional
    public ProcessedFile createProcessedFile(ProcessedFile file) {
        return processedFileRepository.save(file);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessedFile> getProcessedFileById(Long id) {
        Long clientId = ClientContextHolder.getClientId();
        if (clientId != null) {
            return processedFileRepository.findByIdAndClient_Id(id, clientId);
        }
        return processedFileRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> getAllProcessedFiles() {
        Long clientId = ClientContextHolder.getClientId();
        if (clientId != null) {
            return processedFileRepository.findByClient_Id(clientId);
        }
        return processedFileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> getProcessedFilesByClient_Id(Long clientId) {
        return processedFileRepository.findByClient_Id(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessedFile> findByFileNameAndClient_Id(String fileName, Long clientId) {
        // Since there's no specific repository method for this, we'll filter the results
        return processedFileRepository.findByClient_Id(clientId).stream()
            .filter(file -> file.getFileName().equals(fileName))
            .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> findByStatus(String status) {
        return processedFileRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> findByClient_IdAndStatus(Long clientId, String status) {
        // First get all files for the client, then filter by status
        return processedFileRepository.findByClient_Id(clientId).stream()
            .filter(file -> file.getStatus().equals(status))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> findByClient_IdAndProcessingStartTimeBetween(Long clientId, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        
        // Filter files by client and process time
        return processedFileRepository.findByClient_Id(clientId).stream()
            .filter(file -> {
                LocalDateTime processedAt = file.getProcessedAt();
                return processedAt != null && 
                       (processedAt.isEqual(start) || processedAt.isAfter(start)) && 
                       (processedAt.isEqual(end) || processedAt.isBefore(end));
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> findByClient_IdAndHasErrors(Long clientId) {
        // Filter by client and error status
        return processedFileRepository.findByClient_Id(clientId).stream()
            .filter(file -> "ERROR".equals(file.getStatus()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessedFile> findLatestProcessedFiles(Long clientId) {
        // Get files for client and sort by process time descending, limit to 10
        return processedFileRepository.findByClient_Id(clientId).stream()
            .sorted((f1, f2) -> {
                if (f1.getProcessedAt() == null) return 1;
                if (f2.getProcessedAt() == null) return -1;
                return f2.getProcessedAt().compareTo(f1.getProcessedAt());
            })
            .limit(10)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProcessedFile updateProcessedFile(Long id, ProcessedFile fileDetails) {
        Long clientId = ClientContextHolder.getClientId();
        ProcessedFile file;
        
        if (clientId != null) {
            file = processedFileRepository.findByIdAndClient_Id(id, clientId)
                .orElseThrow(() -> new RuntimeException("Processed file not found with id: " + id));
        } else {
            file = processedFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processed file not found with id: " + id));
        }
        
        // Update fields from fileDetails
        file.setFileName(fileDetails.getFileName());
        file.setStatus(fileDetails.getStatus());
        file.setErrorMessage(fileDetails.getErrorMessage());
        file.setInterfaceEntity(fileDetails.getInterfaceEntity());
        file.setProcessedData(fileDetails.getProcessedData());
        
        return processedFileRepository.save(file);
    }

    @Override
    @Transactional
    public void deleteProcessedFile(Long id) {
        Long clientId = ClientContextHolder.getClientId();
        if (clientId != null) {
            processedFileRepository.findByIdAndClient_Id(id, clientId)
                .ifPresent(file -> processedFileRepository.deleteById(id));
        } else {
            processedFileRepository.deleteById(id);
        }
    }
} 
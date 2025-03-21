package com.xml.processor.repository;

import com.xml.processor.model.ProcessedFile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessedFileRepository extends BaseRepository<ProcessedFile> {
    List<ProcessedFile> findByInterfaceEntity_Id(Long interfaceId);
    List<ProcessedFile> findByClient_IdAndInterfaceEntity_Id(Long clientId, Long interfaceId);
    List<ProcessedFile> findByStatus(String status);
} 
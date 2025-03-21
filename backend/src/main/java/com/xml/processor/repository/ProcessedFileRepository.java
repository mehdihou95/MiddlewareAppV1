package com.xml.processor.repository;

import com.xml.processor.model.ProcessedFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessedFileRepository extends BaseRepository<ProcessedFile> {
    /**
     * Find all processed files for a specific client
     *
     * @param clientId The ID of the client
     * @return List of processed files belonging to the client
     */
    List<ProcessedFile> findByClient_Id(Long clientId);

    /**
     * Find processed files by client ID and interface ID
     */
    @Query("SELECT p FROM ProcessedFile p WHERE p.client.id = ?1 AND p.interfaceEntity.id = ?2")
    List<ProcessedFile> findByClient_IdAndInterfaceEntity_Id(Long clientId, Long interfaceId);

    /**
     * Find processed files by client ID and status
     */
    @Query("SELECT p FROM ProcessedFile p WHERE p.client.id = ?1 AND p.status = ?2")
    List<ProcessedFile> findByClient_IdAndStatus(Long clientId, String status);

    /**
     * Find processed files by client ID and filename
     */
    List<ProcessedFile> findByClient_IdAndFileName(Long clientId, String fileName);

    List<ProcessedFile> findByStatus(String status);
} 
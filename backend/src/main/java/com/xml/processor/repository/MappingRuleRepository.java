package com.xml.processor.repository;

import com.xml.processor.model.MappingRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingRuleRepository extends BaseRepository<MappingRule> {
    
    @Query("SELECT m FROM MappingRule m WHERE m.client.id = ?1 AND m.name = ?2")
    Optional<MappingRule> findByClient_IdAndName(Long clientId, String name);
    
    List<MappingRule> findByClient_IdAndIsActive(Long clientId, Boolean isActive);
    
    @Query("SELECT m FROM MappingRule m WHERE m.client.id = ?1 AND m.sourceField = ?2")
    List<MappingRule> findByClient_IdAndSourceField(Long clientId, String sourceField);
    
    @Query("SELECT m FROM MappingRule m WHERE m.client.id = ?1 AND m.targetField = ?2")
    List<MappingRule> findByClient_IdAndTargetField(Long clientId, String targetField);
    
    @Query("SELECT m FROM MappingRule m WHERE m.client.id = ?1 ORDER BY m.priority ASC")
    List<MappingRule> findByClient_IdOrderByPriority(Long clientId);
    
    @Query("SELECT m FROM MappingRule m WHERE m.client.id = ?1 AND m.required = true")
    List<MappingRule> findRequiredRulesByClient_Id(Long clientId);
    
    @Query("SELECT m FROM MappingRule m WHERE m.id = ?1 AND m.client.id = ?2")
    Optional<MappingRule> findByIdAndClient_Id(Long id, Long clientId);
    
    @Query("DELETE FROM MappingRule m WHERE m.client.id = ?1")
    void deleteByClient_Id(Long clientId);

    /**
     * Find all mapping rules for a specific client
     *
     * @param clientId The ID of the client
     * @return List of mapping rules belonging to the client
     */
    List<MappingRule> findByClient_Id(Long clientId);

    /**
     * Find mapping rules by client ID and interface ID
     */
    List<MappingRule> findByClient_IdAndInterfaceEntity_Id(Long clientId, Long interfaceId);
} 
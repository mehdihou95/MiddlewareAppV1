package com.xml.processor.service.interfaces;

import com.xml.processor.model.MappingRule;
import java.util.List;
import java.util.Optional;

public interface MappingRuleService {
    MappingRule createMappingRule(MappingRule rule);
    Optional<MappingRule> getMappingRuleById(Long id);
    List<MappingRule> getAllMappingRules();
    List<MappingRule> getMappingRulesByClientId(Long clientId);
    MappingRule updateMappingRule(Long id, MappingRule rule);
    void deleteMappingRule(Long id);
    void saveMappingConfiguration(List<MappingRule> rules);
    List<MappingRule> findByTableNameAndClientId(String tableName, Long clientId);
    void deleteByClientIdAndTableName(Long clientId, String tableName);
    List<MappingRule> getActiveMappingRules(Long interfaceId);
} 
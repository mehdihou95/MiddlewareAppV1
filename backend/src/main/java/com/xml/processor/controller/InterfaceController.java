package com.xml.processor.controller;

import com.xml.processor.config.ClientContextHolder;
import com.xml.processor.model.Interface;
import com.xml.processor.model.MappingRule;
import com.xml.processor.service.interfaces.InterfaceService;
import com.xml.processor.service.interfaces.MappingRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@RestController
@RequestMapping("/api/interfaces")
public class InterfaceController {
    
    private static final Logger logger = LoggerFactory.getLogger(InterfaceController.class);
    
    @Autowired
    private InterfaceService interfaceService;
    
    @Autowired
    private MappingRuleService mappingRuleService;
    
    @GetMapping
    public ResponseEntity<Page<Interface>> getInterfaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) String typeFilter,
            @RequestParam(required = false) Boolean isActiveFilter) {
        
        Page<Interface> interfaces = interfaceService.getInterfaces(page, size, sortBy, direction, nameFilter, typeFilter, isActiveFilter);
        return ResponseEntity.ok(interfaces);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Interface> getInterface(@PathVariable Long id) {
        logger.debug("Fetching interface with id: {}", id);
        return interfaceService.getInterfaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Interface> createInterface(@RequestBody Interface interfaceEntity) {
        logger.info("Creating new interface: {}", interfaceEntity.getName());
        Interface created = interfaceService.createInterface(interfaceEntity);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Interface> updateInterface(@PathVariable Long id, @RequestBody Interface interfaceEntity) {
        logger.info("Updating interface with id: {}", id);
        Interface updated = interfaceService.updateInterface(id, interfaceEntity);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterface(@PathVariable Long id) {
        logger.info("Deleting interface with id: {}", id);
        interfaceService.deleteInterface(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/mappings")
    public ResponseEntity<List<MappingRule>> getInterfaceMappings(@PathVariable Long id) {
        return ResponseEntity.ok(mappingRuleService.getActiveMappingRules(id));
    }
    
    @PutMapping("/{id}/mappings")
    public ResponseEntity<List<MappingRule>> updateInterfaceMappings(
            @PathVariable Long id,
            @Valid @RequestBody List<MappingRule> mappings) {
        // Update existing mappings and create new ones
        mappings.forEach(mapping -> {
            mapping.setInterfaceId(id);
            if (mapping.getId() == null) {
                mappingRuleService.createMappingRule(mapping);
            } else {
                mappingRuleService.updateMappingRule(mapping.getId(), mapping);
            }
        });
        return ResponseEntity.ok(mappingRuleService.getActiveMappingRules(id));
    }
    
    @PostMapping("/{id}/validate")
    public ResponseEntity<Map<String, Object>> validateInterface(@PathVariable Long id) {
        Interface interfaceEntity = interfaceService.getInterfaceById(id)
                .orElseThrow(() -> new RuntimeException("Interface not found"));
            
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("valid", true);
        validationResult.put("message", "Interface validation successful");
        
        // Validate schema path if provided
        if (interfaceEntity.getSchemaPath() != null && !interfaceEntity.getSchemaPath().isEmpty()) {
            try {
                // Validate schema file exists and is valid XML
                File schemaFile = new File(interfaceEntity.getSchemaPath());
                if (!schemaFile.exists()) {
                    throw new RuntimeException("Schema file not found: " + interfaceEntity.getSchemaPath());
                }
                
                // Validate schema is well-formed XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.parse(schemaFile);
                
                validationResult.put("schemaValid", true);
                validationResult.put("schemaMessage", "Schema validation successful");
            } catch (Exception e) {
                validationResult.put("schemaValid", false);
                validationResult.put("schemaError", e.getMessage());
                validationResult.put("valid", false);
                validationResult.put("message", "Schema validation failed");
            }
        }
        
        // Validate mapping rules
        List<MappingRule> rules = mappingRuleService.getActiveMappingRules(id);
        if (rules.isEmpty()) {
            validationResult.put("hasMappings", false);
            validationResult.put("message", "Interface has no mapping rules");
        } else {
            validationResult.put("hasMappings", true);
            validationResult.put("mappingCount", rules.size());
            
            // Validate each mapping rule
            List<Map<String, Object>> mappingValidationResults = new ArrayList<>();
            for (MappingRule rule : rules) {
package com.xml.processor.controller;

import com.xml.processor.config.ClientContextHolder;
import com.xml.processor.model.Interface;
import com.xml.processor.model.MappingRule;
import com.xml.processor.service.interfaces.InterfaceService;
import com.xml.processor.service.interfaces.MappingRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@RestController
@RequestMapping("/api/interfaces")
public class InterfaceController {
    
    private static final Logger logger = LoggerFactory.getLogger(InterfaceController.class);
    
    @Autowired
    private InterfaceService interfaceService;
    
    @Autowired
    private MappingRuleService mappingRuleService;
    
    @GetMapping
    public ResponseEntity<List<Interface>> getAllInterfaces() {
        logger.debug("Fetching all interfaces");
        List<Interface> interfaces = interfaceService.getAllInterfaces();
        return ResponseEntity.ok(interfaces);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Interface> getInterface(@PathVariable Long id) {
        logger.debug("Fetching interface with id: {}", id);
        return interfaceService.getInterfaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Interface> createInterface(@RequestBody Interface interfaceEntity) {
        logger.info("Creating new interface: {}", interfaceEntity.getName());
        Interface created = interfaceService.createInterface(interfaceEntity);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Interface> updateInterface(@PathVariable Long id, @RequestBody Interface interfaceEntity) {
        logger.info("Updating interface with id: {}", id);
        Interface updated = interfaceService.updateInterface(id, interfaceEntity);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterface(@PathVariable Long id) {
        logger.info("Deleting interface with id: {}", id);
        interfaceService.deleteInterface(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/mappings")
    public ResponseEntity<List<MappingRule>> getInterfaceMappings(@PathVariable Long id) {
        return ResponseEntity.ok(mappingRuleService.getActiveMappingRules(id));
    }
    
    @PutMapping("/{id}/mappings")
    public ResponseEntity<List<MappingRule>> updateInterfaceMappings(
            @PathVariable Long id,
            @Valid @RequestBody List<MappingRule> mappings) {
        // Update existing mappings and create new ones
        mappings.forEach(mapping -> {
            mapping.setInterfaceId(id);
            if (mapping.getId() == null) {
                mappingRuleService.createMappingRule(mapping);
            } else {
                mappingRuleService.updateMappingRule(mapping.getId(), mapping);
            }
        });
        return ResponseEntity.ok(mappingRuleService.getActiveMappingRules(id));
    }
    
    @PostMapping("/{id}/validate")
    public ResponseEntity<Map<String, Object>> validateInterface(@PathVariable Long id) {
        Interface interfaceEntity = interfaceService.getInterfaceById(id)
                .orElseThrow(() -> new RuntimeException("Interface not found"));
            
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("valid", true);
        validationResult.put("message", "Interface validation successful");
        
        // Validate schema path if provided
        if (interfaceEntity.getSchemaPath() != null && !interfaceEntity.getSchemaPath().isEmpty()) {
            try {
                // Validate schema file exists and is valid XML
                File schemaFile = new File(interfaceEntity.getSchemaPath());
                if (!schemaFile.exists()) {
                    throw new RuntimeException("Schema file not found: " + interfaceEntity.getSchemaPath());
                }
                
                // Validate schema is well-formed XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.parse(schemaFile);
                
                validationResult.put("schemaValid", true);
                validationResult.put("schemaMessage", "Schema validation successful");
            } catch (Exception e) {
                validationResult.put("schemaValid", false);
                validationResult.put("schemaError", e.getMessage());
                validationResult.put("valid", false);
                validationResult.put("message", "Schema validation failed");
            }
        }
        
        // Validate mapping rules
        List<MappingRule> rules = mappingRuleService.getActiveMappingRules(id);
        if (rules.isEmpty()) {
            validationResult.put("hasMappings", false);
            validationResult.put("message", "Interface has no mapping rules");
        } else {
            validationResult.put("hasMappings", true);
            validationResult.put("mappingCount", rules.size());
            
            // Validate each mapping rule
            List<Map<String, Object>> mappingValidationResults = new ArrayList<>();
            for (MappingRule rule : rules) {
                Map<String, Object> ruleValidation = new HashMap<>();
                ruleValidation.put("id", rule.getId());
                ruleValidation.put("name", rule.getName());
                
                // Validate XML path
                if (rule.getXmlPath() == null || rule.getXmlPath().trim().isEmpty()) {
                    ruleValidation.put("valid", false);
                    ruleValidation.put("error", "XML path is required");
                } else {
                    ruleValidation.put("valid", true);
                }
                
                // Validate database field
                if (rule.getDatabaseField() == null || rule.getDatabaseField().trim().isEmpty()) {
                    ruleValidation.put("valid", false);
                    ruleValidation.put("error", "Database field is required");
                } else {
                    ruleValidation.put("valid", true);
                }
                
                mappingValidationResults.add(ruleValidation);
            }
            
            validationResult.put("mappingValidations", mappingValidationResults);
            
            // Check if any mapping validations failed
            boolean allMappingsValid = mappingValidationResults.stream()
                .allMatch(v -> (Boolean) v.get("valid"));
            
            if (!allMappingsValid) {
                validationResult.put("valid", false);
                validationResult.put("message", "Some mapping rules are invalid");
            }
        }
        
        return ResponseEntity.ok(validationResult);
    }
} 
package com.xml.processor.service;

import com.xml.processor.model.Client;
import com.xml.processor.model.MappingRule;
import com.xml.processor.repository.ClientRepository;
import com.xml.processor.repository.MappingRuleRepository;
import com.xml.processor.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientOnboardingService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MappingRuleRepository mappingRuleRepository;

    @Transactional
    public Client onboardNewClient(Client client, List<MappingRule> defaultMappingRules) {
        // Validate client data
        validateClientData(client);

        // Create the client
        Client newClient = clientService.createClient(client);

        // Apply default mapping rules
        if (defaultMappingRules != null && !defaultMappingRules.isEmpty()) {
            for (MappingRule rule : defaultMappingRules) {
                rule.setClient(newClient);
                mappingRuleRepository.save(rule);
            }
        }

        return newClient;
    }

    @Transactional
    public Client cloneClientConfiguration(Long sourceClientId, Client newClient) {
        // Get source client
        Client sourceClient = clientService.getClientById(sourceClientId)
                .orElseThrow(() -> new IllegalArgumentException("Source client not found"));

        // Create new client
        Client createdClient = clientService.createClient(newClient);

        // Clone mapping rules
        List<MappingRule> sourceRules = mappingRuleRepository.findByClient_Id(sourceClientId);
        for (MappingRule sourceRule : sourceRules) {
            MappingRule newRule = new MappingRule();
            newRule.setClient(createdClient);
            newRule.setName(sourceRule.getName());
            newRule.setDescription(sourceRule.getDescription());
            newRule.setSourceField(sourceRule.getSourceField());
            newRule.setTargetField(sourceRule.getTargetField());
            newRule.setTransformation(sourceRule.getTransformation());
            mappingRuleRepository.save(newRule);
        }

        return createdClient;
    }

    private void validateClientData(Client client) {
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required");
        }

        if (client.getName().length() > 255) {
            throw new IllegalArgumentException("Client name must not exceed 255 characters");
        }

        if (client.getDescription() != null && client.getDescription().length() > 500) {
            throw new IllegalArgumentException("Client description must not exceed 500 characters");
        }

        if (client.getStatus() == null) {
            throw new IllegalArgumentException("Client status is required");
        }
    }

    public List<MappingRule> getDefaultMappingRules() {
        // Return a list of default mapping rules that can be used as a template
        List<MappingRule> defaultRules = new ArrayList<>();
        
        MappingRule rule1 = new MappingRule();
        rule1.setName("Default Header Rule");
        rule1.setDescription("Default mapping rule for ASN headers");
        rule1.setSourceField("documentNumber");
        rule1.setTargetField("docNumber");
        defaultRules.add(rule1);

        MappingRule rule2 = new MappingRule();
        rule2.setName("Default Line Rule");
        rule2.setDescription("Default mapping rule for ASN lines");
        rule2.setSourceField("lineNumber");
        rule2.setTargetField("lineNum");
        defaultRules.add(rule2);

        return defaultRules;
    }
} 
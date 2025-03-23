package com.xml.processor.service.impl;
    
import com.xml.processor.config.ClientContextHolder;
import com.xml.processor.model.Client;
import com.xml.processor.model.Interface;
import com.xml.processor.repository.InterfaceRepository;
import com.xml.processor.service.interfaces.ClientService;
import com.xml.processor.service.interfaces.InterfaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
    
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
    
@Service
public class InterfaceServiceImpl implements InterfaceService {
    
    private static final Logger logger = LoggerFactory.getLogger(InterfaceServiceImpl.class);
    
    @Autowired
    private InterfaceRepository interfaceRepository;
    
    @Autowired
    private ClientService clientService;
    
    @Override
    public List<Interface> getAllInterfaces() {
        Long clientId = ClientContextHolder.getClientId();
        if (clientId != null) {
            return interfaceRepository.findByClient_Id(clientId);
        }
        return interfaceRepository.findAll();
    }
    
    @Override
    public List<Interface> getClientInterfaces(Long clientId) {
        return interfaceRepository.findByClient_Id(clientId);
    }
    
    @Override
    public Optional<Interface> getInterfaceById(Long id) {
        Long clientId = ClientContextHolder.getClientId();
        if (clientId != null) {
            return interfaceRepository.findByIdAndClient_Id(id, clientId);
        }
        return interfaceRepository.findById(id);
    }
    
    @Override
    public Optional<Interface> getInterfaceByName(String name, Long clientId) {
        return interfaceRepository.findByClient_IdAndName(clientId, name);
    }
    
    @Override
    public Interface createInterface(Interface interfaceEntity) {
        // Set client from context if not explicitly set
        if (interfaceEntity.getClient() == null && ClientContextHolder.getClientId() != null) {
            Client client = clientService.getClientById(ClientContextHolder.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
            interfaceEntity.setClient(client);
        }
        return interfaceRepository.save(interfaceEntity);
    }
    
    @Override
    public Interface updateInterface(Long id, Interface interfaceEntity) {
        Long clientId = ClientContextHolder.getClientId();
        Interface existingInterface;
        
        if (clientId != null) {
            existingInterface = interfaceRepository.findByIdAndClient_Id(id, clientId)
                .orElseThrow(() -> new RuntimeException("Interface not found"));
        } else {
            existingInterface = interfaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interface not found"));
        }
        
        // Update fields
        existingInterface.setName(interfaceEntity.getName());
        existingInterface.setType(interfaceEntity.getType());
        existingInterface.setDescription(interfaceEntity.getDescription());
        existingInterface.setSchemaPath(interfaceEntity.getSchemaPath());
        existingInterface.setRootElement(interfaceEntity.getRootElement());
        existingInterface.setNamespace(interfaceEntity.getNamespace());
        existingInterface.setIsActive(interfaceEntity.getIsActive());
        existingInterface.setPriority(interfaceEntity.getPriority());
        
        return interfaceRepository.save(existingInterface);
    }
    
    @Override
    public void deleteInterface(Long id) {
        Long clientId = ClientContextHolder.getClientId();
        if (clientId != null) {
            interfaceRepository.findByIdAndClient_Id(id, clientId)
                .ifPresent(interfaceEntity -> interfaceRepository.deleteById(id));
        } else {
            interfaceRepository.deleteById(id);
        }
    }
    
    @Override
    public List<Interface> getInterfacesByClient(Client client) {
        return interfaceRepository.findByClient(client);
    }
    
    @Override
    public Interface detectInterface(String xmlContent, Long clientId) {
        try {
            // Parse XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
            
            // Get root element name and namespace
            String rootElement = document.getDocumentElement().getLocalName();
            String namespace = document.getDocumentElement().getNamespaceURI();
            
            logger.info("Detecting interface for root element: {}, namespace: {}", rootElement, namespace);
            
            // Get all active interfaces for the client
            List<Interface> interfaces = interfaceRepository.findByClient_IdAndIsActiveTrue(clientId);
            
            // First try exact match on root element and namespace
            for (Interface interfaceEntity : interfaces) {
                if (rootElement.equals(interfaceEntity.getRootElement()) && 
                    (namespace == null || namespace.equals(interfaceEntity.getNamespace()))) {
                    logger.info("Found exact match for interface: {}", interfaceEntity.getName());
                    return interfaceEntity;
                }
            }
            
            // Try match on root element only
            for (Interface interfaceEntity : interfaces) {
                if (rootElement.equals(interfaceEntity.getRootElement())) {
                    logger.info("Found root element match for interface: {}", interfaceEntity.getName());
                    return interfaceEntity;
                }
            }
            
            // Try partial match
            for (Interface interfaceEntity : interfaces) {
                if (interfaceEntity.getRootElement() != null && 
                    rootElement.contains(interfaceEntity.getRootElement())) {
                    logger.info("Found partial match for interface: {}", interfaceEntity.getName());
                    return interfaceEntity;
                }
            }
            
            logger.warn("No matching interface found for root element: {}", rootElement);
            return null;
        } catch (Exception e) {
            logger.error("Error detecting interface: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Interface> getInterfaces(int page, int size, String sortBy, String direction, 
            String nameFilter, String typeFilter, Boolean isActiveFilter) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        // Apply filters if provided
        if (nameFilter != null && !nameFilter.isEmpty()) {
            return interfaceRepository.findByNameContainingIgnoreCase(nameFilter, pageRequest);
        } else if (typeFilter != null && !typeFilter.isEmpty()) {
            return interfaceRepository.findByType(typeFilter, pageRequest);
        } else if (isActiveFilter != null) {
            return interfaceRepository.findByIsActive(isActiveFilter, pageRequest);
        }
        
        // No filters, return all with pagination
        return interfaceRepository.findAll(pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Interface getInterfaceById(Long id) {
        return interfaceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Interface not found with id: " + id));
    }

    @Override
    @Transactional
    public Interface createInterface(Interface interfaceEntity) {
        return interfaceRepository.save(interfaceEntity);
    }

    @Override
    @Transactional
    public Interface updateInterface(Long id, Interface interfaceEntity) {
        Interface existingInterface = getInterfaceById(id);
        // Update fields
        existingInterface.setName(interfaceEntity.getName());
        existingInterface.setType(interfaceEntity.getType());
        existingInterface.setDescription(interfaceEntity.getDescription());
        existingInterface.setIsActive(interfaceEntity.getIsActive());
        existingInterface.setPriority(interfaceEntity.getPriority());
        existingInterface.setRootElement(interfaceEntity.getRootElement());
        existingInterface.setNamespace(interfaceEntity.getNamespace());
        existingInterface.setSchemaPath(interfaceEntity.getSchemaPath());
        return interfaceRepository.save(existingInterface);
    }

    @Override
    @Transactional
    public void deleteInterface(Long id) {
        interfaceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Interface> getInterfacesByClient(Long clientId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return interfaceRepository.findByClientId(clientId, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Interface> searchInterfaces(String name, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return interfaceRepository.findByNameContainingIgnoreCase(name, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Interface> getInterfacesByType(String type, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return interfaceRepository.findByType(type, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Interface> getInterfacesByStatus(boolean isActive, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return interfaceRepository.findByIsActive(isActive, pageRequest);
    }
} 
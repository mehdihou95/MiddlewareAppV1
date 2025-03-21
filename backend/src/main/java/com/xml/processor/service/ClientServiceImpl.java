package com.xml.processor.service;

import com.xml.processor.model.Client;
import com.xml.processor.repository.ClientRepository;
import com.xml.processor.service.interfaces.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Override
    @Transactional
    public Client saveClient(Client client) {
        if (clientRepository.existsByName(client.getName())) {
            throw new IllegalArgumentException("Client with name " + client.getName() + " already exists");
        }
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientByName(String name) {
        return clientRepository.findByName(name)
            .orElseThrow(() -> new EntityNotFoundException("Client not found with name: " + name));
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByName(String name) {
        return clientRepository.existsByName(name);
    }

    // Non-interface methods without @Override
    @Transactional
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + id));
        
        if (!client.getName().equals(clientDetails.getName()) && 
            clientRepository.existsByNameAndIdNot(clientDetails.getName(), id)) {
            throw new IllegalArgumentException("Client with name " + clientDetails.getName() + " already exists");
        }
        
        client.setName(clientDetails.getName());
        client.setDescription(clientDetails.getDescription());
        client.setStatus(clientDetails.getStatus());
        
        return clientRepository.save(client);
    }
    
    public boolean existsById(Long id) {
        return clientRepository.existsById(id);
    }
    
    public Optional<Client> findByClientCode(String clientCode) {
        return clientRepository.findByCode(clientCode);
    }
} 
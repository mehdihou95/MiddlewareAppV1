package com.xml.processor.service;

import com.xml.processor.model.Client;
import com.xml.processor.repository.ClientRepository;
import com.xml.processor.service.interfaces.ClientService;
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
    public Client createClient(Client client) {
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
    public Optional<Client> getClientByName(String name) {
        return clientRepository.findByName(name);
    }

    @Override
    @Transactional
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));
        
        if (!client.getName().equals(clientDetails.getName()) && 
            clientRepository.existsByNameAndIdNot(clientDetails.getName(), id)) {
            throw new IllegalArgumentException("Client with name " + clientDetails.getName() + " already exists");
        }
        
        client.setName(clientDetails.getName());
        client.setDescription(clientDetails.getDescription());
        client.setStatus(clientDetails.getStatus());
        
        return clientRepository.save(client);
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
    public boolean existsById(Long id) {
        return clientRepository.existsById(id);
    }
    
    @Override
    public Optional<Client> findByClientCode(String clientCode) {
        // Assuming the code field in Client is meant for clientCode
        return clientRepository.findByCode(clientCode);
    }
} 
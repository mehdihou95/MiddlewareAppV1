package com.xml.processor.service.impl;

import com.xml.processor.model.Client;
import com.xml.processor.repository.ClientRepository;
import com.xml.processor.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Client> getClients(int page, int size, String sortBy, String direction, String nameFilter, String statusFilter) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        // Apply filters if provided
        if (nameFilter != null && !nameFilter.isEmpty()) {
            return clientRepository.findByNameContainingIgnoreCase(nameFilter, pageRequest);
        } else if (statusFilter != null && !statusFilter.isEmpty()) {
            return clientRepository.findByStatus(statusFilter, pageRequest);
        }
        
        // No filters, return all with pagination
        return clientRepository.findAll(pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    @Override
    @Transactional
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client updateClient(Long id, Client client) {
        Client existingClient = getClientById(id);
        // Update fields
        existingClient.setName(client.getName());
        existingClient.setCode(client.getCode());
        existingClient.setDescription(client.getDescription());
        existingClient.setStatus(client.getStatus());
        return clientRepository.save(existingClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Client> getClientsByStatus(String status, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return clientRepository.findByStatus(status, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Client> searchClients(String name, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return clientRepository.findByNameContainingIgnoreCase(name, pageRequest);
    }
} 
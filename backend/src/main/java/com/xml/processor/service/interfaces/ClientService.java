package com.xml.processor.service.interfaces;

import com.xml.processor.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client createClient(Client client);
    List<Client> getAllClients();
    Optional<Client> getClientById(Long id);
    Optional<Client> getClientByName(String name);
    Client updateClient(Long id, Client clientDetails);
    void deleteClient(Long id);
    boolean existsById(Long id);
    Optional<Client> findByClientCode(String clientCode);
} 
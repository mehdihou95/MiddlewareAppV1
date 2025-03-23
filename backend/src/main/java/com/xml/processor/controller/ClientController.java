package com.xml.processor.controller;

import com.xml.processor.model.Client;
import com.xml.processor.model.Interface;
import com.xml.processor.service.interfaces.ClientService;
import com.xml.processor.service.interfaces.InterfaceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    private final InterfaceService interfaceService;

    public ClientController(ClientService clientService, InterfaceService interfaceService) {
        this.clientService = clientService;
        this.interfaceService = interfaceService;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        client.setId(id);
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientService.getClientById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<Client>> getClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) String statusFilter) {
        
        Page<Client> clients = clientService.getClients(page, size, sortBy, direction, nameFilter, statusFilter);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Client> getClientByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(clientService.getClientByName(name));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{clientId}/interfaces")
    public ResponseEntity<Interface> createClientInterface(
            @PathVariable Long clientId,
            @RequestBody Interface interfaceEntity) {
        return clientService.getClientById(clientId)
                .map(client -> {
                    interfaceEntity.setClient(client);
                    Interface created = interfaceService.createInterface(interfaceEntity);
                    return ResponseEntity.ok(created);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{clientId}/interfaces")
    public ResponseEntity<List<Interface>> getClientInterfaces(@PathVariable Long clientId) {
        return clientService.getClientById(clientId)
                .map(client -> ResponseEntity.ok(interfaceService.getInterfacesByClient(client)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Client>> searchClients(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Page<Client> clients = clientService.searchClients(name, page, size, sortBy, direction);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Client>> getClientsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Page<Client> clients = clientService.getClientsByStatus(status, page, size, sortBy, direction);
        return ResponseEntity.ok(clients);
    }
} 
package com.xml.processor.controller;

import com.xml.processor.model.Client;
import com.xml.processor.model.Interface;
import com.xml.processor.service.interfaces.ClientService;
import com.xml.processor.service.interfaces.InterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private InterfaceService interfaceService;

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
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
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
} 
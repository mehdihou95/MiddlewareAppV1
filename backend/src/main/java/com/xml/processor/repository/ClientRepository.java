package com.xml.processor.repository;

import com.xml.processor.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByName(String name);
    Optional<Client> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
} 
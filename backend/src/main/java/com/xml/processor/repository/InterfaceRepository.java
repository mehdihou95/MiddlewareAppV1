package com.xml.processor.repository;
    
import com.xml.processor.model.Interface;
import com.xml.processor.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
    
import java.util.List;
import java.util.Optional;
    
@Repository
public interface InterfaceRepository extends JpaRepository<Interface, Long> {
    List<Interface> findByClient_IdAndType(Long clientId, String type);
    Optional<Interface> findByClient_IdAndName(Long clientId, String name);
    List<Interface> findByClient_IdAndIsActiveTrue(Long clientId);
    List<Interface> findByClient(Client client);
    List<Interface> findByClientId(Long clientId);
    Page<Interface> findByClientId(Long clientId, Pageable pageable);
    Page<Interface> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Interface> findByType(String type, Pageable pageable);
    Page<Interface> findByIsActive(boolean isActive, Pageable pageable);
    boolean existsByNameAndClientId(String name, Long clientId);
    boolean existsByNameAndClientIdAndIdNot(String name, Long clientId, Long id);
} 
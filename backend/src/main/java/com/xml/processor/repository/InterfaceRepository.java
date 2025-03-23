package com.xml.processor.repository;
    
import com.xml.processor.model.Interface;
import com.xml.processor.model.Client;
import org.springframework.stereotype.Repository;
    
import java.util.List;
import java.util.Optional;
    
@Repository
public interface InterfaceRepository extends BaseRepository<Interface> {
    List<Interface> findByClient_IdAndType(Long clientId, String type);
    Optional<Interface> findByClient_IdAndName(Long clientId, String name);
    List<Interface> findByClient_IdAndIsActiveTrue(Long clientId);
    List<Interface> findByClient(Client client);
} 
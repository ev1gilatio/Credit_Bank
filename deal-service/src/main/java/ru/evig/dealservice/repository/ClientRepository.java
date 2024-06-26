package ru.evig.dealservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.evig.dealservice.entity.Client;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}

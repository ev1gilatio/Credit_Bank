package ru.evig.dealservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.evig.dealservice.entity.Credit;

import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
}

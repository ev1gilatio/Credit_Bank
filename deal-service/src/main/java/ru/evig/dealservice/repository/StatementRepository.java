package ru.evig.dealservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.evig.dealservice.entity.Statement;

import java.util.UUID;

public interface StatementRepository extends JpaRepository<Statement, UUID> {
}

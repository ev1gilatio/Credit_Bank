package ru.evig.dealservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "statement")
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

    @Id
    private UUID id;

    @JoinColumn(name = "client_id")
    private UUID clientId;
}

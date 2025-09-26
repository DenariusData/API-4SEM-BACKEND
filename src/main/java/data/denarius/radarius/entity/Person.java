package data.denarius.radarius.entity;

import data.denarius.radarius.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String name;

    private String whatsapp;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}

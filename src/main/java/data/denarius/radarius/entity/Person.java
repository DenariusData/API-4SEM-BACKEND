package data.denarius.radarius.entity;

import data.denarius.radarius.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JoinColumn(name = "per_id")
    private Integer id;

    @JoinColumn(name = "per_name")
    private String name;

    @JoinColumn(name = "per_whatsapp")
    private String whatsapp;

    @JoinColumn(name = "per_email")
    private String email;

    @JoinColumn(name = "per_password")
    private String password;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "per_role")
    private RoleEnum role;

    @Column(name = "per_created_at")
    private LocalDateTime createdAt;
}

package data.denarius.radarius.entity;

import data.denarius.radarius.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "per_id")
    private Integer id;

    @Column(name = "per_name")
    private String name;

    @Column(name = "per_whatsapp")
    private String whatsapp;

    @Column(name = "per_email")
    private String email;

    @Column(name = "per_password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "per_role")
    private RoleEnum role;

    @Column(name = "per_created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "person_region",
            joinColumns = @JoinColumn(name = "per_id"),
            inverseJoinColumns = @JoinColumn(name = "reg_id")
    )
    private List<Region> regions = new ArrayList<>();
}

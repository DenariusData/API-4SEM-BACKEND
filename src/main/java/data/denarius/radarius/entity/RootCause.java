package data.denarius.radarius.entity;

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
@Table(name = "root_cause")
public class RootCause {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "rc_id")
    private Integer id;

    @Column(name = "rc_name")
    private String name;

    @Column(name = "rc_description")
    private String description;

    @Column(name = "rc_created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "rc_created_by")
    private Person person;
}

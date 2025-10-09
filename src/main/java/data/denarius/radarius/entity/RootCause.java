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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "rc_id")
    private Integer id;

    @JoinColumn(name = "rc_name")
    private String name;

    @JoinColumn(name = "rc_description")
    private String description;

    @JoinColumn(name = "rc_created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "created_by")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "pro_id")
    private Protocol protocol;

}
package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol")
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JoinColumn(name = "pro_id")
    private Integer id;

    @Column(name = "pro_name")
    private String name;

    @Column(name = "pro_created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "pro_created_by")
    private Person createdBy;

    @OneToMany(mappedBy = "protocol")
    private List<RootCause> rootCauses;
}


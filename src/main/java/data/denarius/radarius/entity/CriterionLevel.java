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
@Table(name = "criterion_level")
public class CriterionLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "cl_id")
    private Integer id;

    @Column(name = "cl_level")
    private Short level;

    @Column(name = "cl_created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "cl_created_by")
    private Person createdBy;

    @ManyToOne
    @JoinColumn(name = "cri_id")
    private Criterion criterion;
}

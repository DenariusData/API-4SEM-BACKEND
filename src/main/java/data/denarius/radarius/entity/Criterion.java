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
@Table(name = "criterion")
public class Criterion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JoinColumn(name = "cri_id")
    private Integer id;

    @Column(name = "cri_name")
    private String name;

    @Column(name = "cri_created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "cri_created_by")
    private Person createdBy;

    @OneToMany(mappedBy = "criterion")
    private List<CriterionLevel> criterionLevels;

    @OneToMany(mappedBy = "criterion")
    private List<Alert> alerts;
}

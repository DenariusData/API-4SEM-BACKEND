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
@Table(name = "detected_incident")
public class DetectedIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "di_id")
    private Integer id;

    @Column(name = "di_incident_type")
    private String incidentType;

    @Column(name = "di_created_at")
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "di_created_by")
    private Person createdBy;

    @ManyToOne
    @JoinColumn(name = "ale_id")
    private Alert alert;
}

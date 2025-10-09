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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "di_id")
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "ale_id")
    private Alert alert;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Person createdBy;

    @JoinColumn(name = "di_incident_type")
    private String incidentType;

    @Column(name = "di_created_at")
    private LocalDateTime createdAt;
}

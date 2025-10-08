package data.denarius.radarius.entity;


import data.denarius.radarius.enums.SourceTypeEnum;
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
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "ale_id")
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "ale_criterion_id")
    private Criterion criterion;

    @ManyToOne
    @JoinColumn(name = "ale_protocol_id")
    private Protocol protocol;

    @Column(name = "ale_level")
    private Short level;

    @Column(name = "ale_closed_at")
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "ale_assigned")
    private Person assigned;

    @JoinColumn(name = "ale_message")
    private String message;

    @JoinColumn(name = "ale_conclusion")
    private String conclusion;

    @ManyToOne
    @JoinColumn(name = "ale_camera_id")
    private Camera camera;

    @Column(name = "ale_created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "ale_source_type")
    private SourceTypeEnum sourceType;

    @OneToMany(mappedBy = "ale_alert")
    private List<DetectedIncident> incidents;

    @ManyToOne
    @JoinColumn(name = "ale_created_by")
    private Person createdBy;

    @OneToOne
    @JoinColumn(name = "ale_root_cause")
    private RootCause rootCause;

    @OneToMany(mappedBy = "ale_alert")
    private List<AlertLog> logs;


}
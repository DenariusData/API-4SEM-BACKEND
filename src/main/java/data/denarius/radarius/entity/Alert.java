package data.denarius.radarius.entity;

import data.denarius.radarius.enums.SourceTypeEnum;
import data.denarius.radarius.listeners.AlertEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert")
@EntityListeners(AlertEntityListener.class)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ale_id")
    private Integer id;

    @Column(name = "ale_level")
    private Short level;

    @Column(name = "ale_message")
    private String message;

    @Column(name = "ale_conclusion")
    private String conclusion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ale_source_type")
    private SourceTypeEnum sourceType;

    @Column(name = "ale_created_at")
    private LocalDateTime createdAt;

    @Column(name = "ale_closed_at")
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "ale_created_by")
    private Person createdBy;

    @ManyToOne
    @JoinColumn(name = "ale_assigned_to")
    private Person assignedTo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cri_id", nullable = false)
    private Criterion criterion;

    @ManyToOne
    @JoinColumn(name = "rc_id")
    private RootCause rootCause;

    @ManyToOne
    @JoinColumn(name = "pro_id")
    private Protocol protocol;

    @OneToMany(mappedBy = "alert")
    private List<DetectedIncident> incidents;

    @OneToMany(mappedBy = "alert")
    private List<AlertLog> logs;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reg_id", nullable = false)
    private Region region;
}

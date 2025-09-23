package data.denarius.radarius.entity;


import data.denarius.radarius.enums.SourceTypeEnum;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "alerta")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertId;

    @ManyToOne
    @JoinColumn(name = "criterio_id")
    private Criterion criterion;

    @ManyToOne
    @JoinColumn(name = "protocolo_id")
    private Protocol protocol;

    private Short level;

    private String status;

    @ManyToOne
    @JoinColumn(name = "atribuido_para")
    private User assignedTo;

    private String message;

    private String conclusion;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private SourceTypeEnum sourceType;

    @OneToMany(mappedBy = "alert")
    private List<DetectedIncident> incidents;

    @OneToMany(mappedBy = "alert")
    private List<AlertLog> logs;

    // Getters and setters
}

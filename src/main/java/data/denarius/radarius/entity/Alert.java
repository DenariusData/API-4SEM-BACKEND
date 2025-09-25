package data.denarius.radarius.entity;


import data.denarius.radarius.enums.SourceTypeEnum;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertId;

    @ManyToOne
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    @ManyToOne
    @JoinColumn(name = "protocol_id")
    private Protocol protocol;

    private Short level;

    private String status;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
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

    public Integer getAlertId() {
        return alertId;
    }

    public void setAlertId(Integer alertId) {
        this.alertId = alertId;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SourceTypeEnum getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceTypeEnum sourceType) {
        this.sourceType = sourceType;
    }

    public List<DetectedIncident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<DetectedIncident> incidents) {
        this.incidents = incidents;
    }

    public List<AlertLog> getLogs() {
        return logs;
    }

    public void setLogs(List<AlertLog> logs) {
        this.logs = logs;
    }
}

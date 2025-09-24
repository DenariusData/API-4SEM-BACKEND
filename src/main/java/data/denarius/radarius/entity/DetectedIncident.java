package data.denarius.radarius.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "incidente_detectado")
public class DetectedIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer incidentId;

    @ManyToOne
    @JoinColumn(name = "alerta_id")
    private Alert alert;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User user;

    private String incidentType;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Integer getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Integer incidentId) {
        this.incidentId = incidentId;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

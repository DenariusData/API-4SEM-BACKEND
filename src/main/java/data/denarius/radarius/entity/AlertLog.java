package data.denarius.radarius.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "alert_log")
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Person person;

    private String channel;

    private String event;

    @Column(name = "event_ts")
    private OffsetDateTime eventTimestamp;

    private String status;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Person getUser() {
        return person;
    }

    public void setUser(Person person) {
        this.person = person;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public OffsetDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(OffsetDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

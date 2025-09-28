package data.denarius.radarius.dtos.alertlog;

import java.time.OffsetDateTime;

public class AlertLogResponseDTO {

    private Integer logId;
    private Integer alertId;
    private Integer userId;
    private String channel;
    private String event;
    private OffsetDateTime eventTimestamp;
    private String status;

    // Getters e Setters
    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }

    public Integer getAlertId() { return alertId; }
    public void setAlertId(Integer alertId) { this.alertId = alertId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public OffsetDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(OffsetDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

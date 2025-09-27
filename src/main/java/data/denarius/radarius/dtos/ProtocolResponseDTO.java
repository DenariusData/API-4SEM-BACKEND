package data.denarius.radarius.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ProtocolResponseDTO {

    private Integer protocolId;
    private String name;
    private OffsetDateTime createdAt;
    private Integer createdById;
    private List<Integer> alertIds;

    // Getters e Setters
    public Integer getProtocolId() { return protocolId; }
    public void setProtocolId(Integer protocolId) { this.protocolId = protocolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedById() { return createdById; }
    public void setCreatedById(Integer createdById) { this.createdById = createdById; }

    public List<Integer> getAlertIds() { return alertIds; }
    public void setAlertIds(List<Integer> alertIds) { this.alertIds = alertIds; }
}

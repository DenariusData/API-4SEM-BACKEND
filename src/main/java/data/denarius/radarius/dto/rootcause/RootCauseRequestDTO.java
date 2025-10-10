package data.denarius.radarius.dto.rootcause;

import java.time.LocalDateTime;

public class RootCauseRequestDTO {
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private Integer personId;
    private Integer protocolId;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getPersonId() { return personId; }
    public void setPersonId(Integer personId) { this.personId = personId; }

    public Integer getProtocolId() { return protocolId; }
    public void setProtocolId(Integer protocolId) { this.protocolId = protocolId; }
}

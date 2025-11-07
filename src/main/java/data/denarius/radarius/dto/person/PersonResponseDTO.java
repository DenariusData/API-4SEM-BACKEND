package data.denarius.radarius.dto.person;

import data.denarius.radarius.dto.region.RegionResponseDTO;
import data.denarius.radarius.enums.RoleEnum;
import java.time.LocalDateTime;
import java.util.List;

public class PersonResponseDTO {
    private Integer id;
    private String name;
    private String whatsapp;
    private String email;
    private RoleEnum role;
    private LocalDateTime createdAt;
    private List<RegionResponseDTO> regions;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public RoleEnum getRole() { return role; }
    public void setRole(RoleEnum role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RegionResponseDTO> getRegions() { return regions; }
    public void setRegions(List<RegionResponseDTO> regions) { this.regions = regions; }
}

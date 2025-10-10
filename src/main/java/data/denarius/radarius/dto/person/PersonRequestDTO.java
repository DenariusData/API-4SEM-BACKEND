package data.denarius.radarius.dto.person;

import data.denarius.radarius.enums.RoleEnum;
import java.time.LocalDateTime;

public class PersonRequestDTO {
    private String name;
    private String whatsapp;
    private String email;
    private String password;
    private RoleEnum role;
    private LocalDateTime createdAt;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleEnum getRole() { return role; }
    public void setRole(RoleEnum role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

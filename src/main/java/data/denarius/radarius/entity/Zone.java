package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "zone")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "zon_id")
    private Integer id;

    @JoinColumn(name = "zon_name")
    private String name;

    @JoinColumn(name = "zon_created_at")
    private LocalDateTime createdAt;

    @JoinColumn(name = "zon_updated_at")
    private LocalDateTime updatedAt;
}
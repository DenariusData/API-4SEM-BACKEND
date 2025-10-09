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
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "reg_id")
    private Integer id;

    @JoinColumn(name = "reg_name")
    private String name;

    @JoinColumn(name = "reg_created_at")
    private LocalDateTime createdAt;

    @JoinColumn(name = "reg_updated_at")
    private LocalDateTime updatedAt;
}

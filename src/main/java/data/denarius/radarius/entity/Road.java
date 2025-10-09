package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "road")
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roa_id")
    private Integer id;

    @Column(name = "roa_address")
    private String address;

    @Column(name = "roa_speed_limit")
    private BigDecimal speedLimit;

    @Column(name = "roa_created_at")
    private LocalDateTime createdAt;

    @Column(name = "roa_updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "road")
    private List<Camera> cameras;

}

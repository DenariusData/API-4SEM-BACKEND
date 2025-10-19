package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "reg_id")
    private Integer id;

    @Column(name = "reg_name")
    private String name;

    @Column(name = "reg_created_at")
    private LocalDateTime createdAt;

    @Column(name = "reg_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reg_center_latitude", precision = 9, scale = 6)
    private BigDecimal centerLatitude;

    @Column(name = "reg_center_longitude", precision = 9, scale = 6)
    private BigDecimal centerLongitude;

    @Column(name = "reg_radius_km", precision = 5, scale = 2)
    private BigDecimal radiusKm;
}

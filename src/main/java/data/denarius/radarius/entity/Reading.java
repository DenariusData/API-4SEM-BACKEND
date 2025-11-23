package data.denarius.radarius.entity;

import data.denarius.radarius.enums.VehicleTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reading")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "rea_id")
    private Integer id;

    @Column(name = "rea_created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rea_vehicle_type")
    private VehicleTypeEnum vehicleType;

    @Column(name = "rea_speed")
    private BigDecimal speed;

    @ManyToOne
    @JoinColumn(name = "cam_id")
    private Camera camera;
}

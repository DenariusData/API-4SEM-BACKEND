package data.denarius.radarius.entity;

import data.denarius.radarius.enums.VehicleTypeEnum;
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
@Table(name = "reading")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "rea_camera_id")
    private Camera camera;

    @Column(name = "rea_created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleTypeEnum vehicleType;

    @Column(name = "rea_speed")
    private BigDecimal speed;

    @Column(name = "rea_plate")
    private String plate;


}

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
@Table(name = "camera")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "cam_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reg_id")
    private Region region;

    @ManyToOne
    @JoinColumn(name = "roa_id")
    private Road road;

    @Column(name = "cam_latitude")
    private BigDecimal latitude;

    @Column(name = "cam_longitude")
    private BigDecimal longitude;

    @Column(name = "cam_active")
    private Boolean active;

    @Column(name = "cam_created_at")
    private LocalDateTime createdAt;

    @Column(name = "cam_updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "camera")
    private List<Reading> readings;

    @OneToMany(mappedBy = "camera")
    private List<Alert> alerts;


}

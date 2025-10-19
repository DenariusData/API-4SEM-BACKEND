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
@Table(name = "dados_base_radares")
public class RadarBaseData {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CAMERA_LAT", precision = 10, scale = 6)
    private BigDecimal cameraLatitude;

    @Column(name = "CAMERA_LONG", precision = 10, scale = 6)
    private BigDecimal cameraLongitude;

    @Column(name = "CAMERA_ID", length = 50)
    private String cameraId;

    @Column(name = "FAIXA_DA_CAMERA", precision = 2)
    private Integer cameraLane;

    @Column(name = "QUANTIDADE_DE_FAIXAS", precision = 2)
    private Integer totalLanes;

    @Column(name = "DATA_HORA")
    private LocalDateTime dateTime;

    @Column(name = "TIPO_VEICULO", length = 20)
    private String vehicleType;

    @Column(name = "VELOCIDADE_VEICULO", precision = 5, scale = 2)
    private BigDecimal vehicleSpeed;

    @Column(name = "VELOCIDADE_REGULAMENTADA", precision = 3)
    private Integer speedLimit;

    @Column(name = "ENDERECO", length = 200)
    private String address;

    @Column(name = "NUMERO", length = 10)
    private String number;

    @Column(name = "CIDADE", length = 100)
    private String city;

    @Column(name = "SENTIDO", length = 50)
    private String direction;

    @Builder.Default
    @Column(name = "PROCESSADO", nullable = false)
    private Boolean processed = false;
}
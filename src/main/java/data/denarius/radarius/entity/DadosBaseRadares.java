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
public class DadosBaseRadares {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CAMERA_LAT", precision = 10, scale = 6)
    private BigDecimal cameraLat;

    @Column(name = "CAMERA_LONG", precision = 10, scale = 6)
    private BigDecimal cameraLong;

    @Column(name = "CAMERA_ID", length = 50)
    private String cameraId;

    @Column(name = "FAIXA_DA_CAMERA", precision = 2)
    private Integer faixaDaCamera;

    @Column(name = "QUANTIDADE_DE_FAIXAS", precision = 2)
    private Integer quantidadeDeFaixas;

    @Column(name = "DATA_HORA")
    private LocalDateTime dataHora;

    @Column(name = "TIPO_VEICULO", length = 20)
    private String tipoVeiculo;

    @Column(name = "VELOCIDADE_VEICULO", precision = 5, scale = 2)
    private BigDecimal velocidadeVeiculo;

    @Column(name = "VELOCIDADE_REGULAMENTADA", precision = 3)
    private Integer velocidadeRegulamentada;

    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    @Column(name = "CIDADE", length = 100)
    private String cidade;

    @Column(name = "SENTIDO", length = 50)
    private String sentido;

    // Campo para controle de processamento - indica se este registro já foi processado
    @Builder.Default
    @Column(name = "PROCESSADO", nullable = false)
    private Boolean processado = false;
}
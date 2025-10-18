package data.denarius.radarius.dto.dadosbaseradares;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DadosBaseRadaresResponseDTO {
    private Long id;
    private BigDecimal cameraLat;
    private BigDecimal cameraLong;
    private String cameraId;
    private Integer faixaDaCamera;
    private Integer quantidadeDeFaixas;
    private LocalDateTime dataHora;
    private String tipoVeiculo;
    private BigDecimal velocidadeVeiculo;
    private Integer velocidadeRegulamentada;
    private String endereco;
    private String numero;
    private String cidade;
    private String sentido;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getCameraLat() { return cameraLat; }
    public void setCameraLat(BigDecimal cameraLat) { this.cameraLat = cameraLat; }

    public BigDecimal getCameraLong() { return cameraLong; }
    public void setCameraLong(BigDecimal cameraLong) { this.cameraLong = cameraLong; }

    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }

    public Integer getFaixaDaCamera() { return faixaDaCamera; }
    public void setFaixaDaCamera(Integer faixaDaCamera) { this.faixaDaCamera = faixaDaCamera; }

    public Integer getQuantidadeDeFaixas() { return quantidadeDeFaixas; }
    public void setQuantidadeDeFaixas(Integer quantidadeDeFaixas) { this.quantidadeDeFaixas = quantidadeDeFaixas; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(String tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }

    public BigDecimal getVelocidadeVeiculo() { return velocidadeVeiculo; }
    public void setVelocidadeVeiculo(BigDecimal velocidadeVeiculo) { this.velocidadeVeiculo = velocidadeVeiculo; }

    public Integer getVelocidadeRegulamentada() { return velocidadeRegulamentada; }
    public void setVelocidadeRegulamentada(Integer velocidadeRegulamentada) { this.velocidadeRegulamentada = velocidadeRegulamentada; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getSentido() { return sentido; }
    public void setSentido(String sentido) { this.sentido = sentido; }
}
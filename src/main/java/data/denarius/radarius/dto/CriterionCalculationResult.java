package data.denarius.radarius.dto;

import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Criterion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CriterionCalculationResult {
    
    private Criterion criterion;
    private Camera camera;
    private BigDecimal calculatedValue;
    private Integer calculatedLevel;
    private LocalDateTime calculationTime;
    private Integer sampleSize;
    private String description;

    public CriterionCalculationResult() {}

    public CriterionCalculationResult(Criterion criterion, Camera camera, 
                                    BigDecimal calculatedValue, Integer calculatedLevel, 
                                    Integer sampleSize, String description) {
        this.criterion = criterion;
        this.camera = camera;
        this.calculatedValue = calculatedValue;
        this.calculatedLevel = calculatedLevel;
        this.calculationTime = LocalDateTime.now();
        this.sampleSize = sampleSize;
        this.description = description;
    }

    // Getters and Setters
    public Criterion getCriterion() { return criterion; }
    public void setCriterion(Criterion criterion) { this.criterion = criterion; }

    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public BigDecimal getCalculatedValue() { return calculatedValue; }
    public void setCalculatedValue(BigDecimal calculatedValue) { this.calculatedValue = calculatedValue; }

    public Integer getCalculatedLevel() { return calculatedLevel; }
    public void setCalculatedLevel(Integer calculatedLevel) { this.calculatedLevel = calculatedLevel; }

    public LocalDateTime getCalculationTime() { return calculationTime; }
    public void setCalculationTime(LocalDateTime calculationTime) { this.calculationTime = calculationTime; }

    public Integer getSampleSize() { return sampleSize; }
    public void setSampleSize(Integer sampleSize) { this.sampleSize = sampleSize; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

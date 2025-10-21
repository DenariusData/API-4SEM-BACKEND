package data.denarius.radarius.service.criterion;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.repository.CriterionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class CriterionResultFactory {

    @Autowired
    private CriterionRepository criterionRepository;
    
    @Autowired
    private AlertLevelCalculator alertLevelCalculator;

    public CriterionCalculationResult createResult(String criterionName, Camera camera, 
            BigDecimal value, int sampleSize, String description) {
        
        Criterion criterion = criterionRepository.findByName(criterionName)
            .orElseThrow(() -> new RuntimeException("Criterion not found: " + criterionName + 
                ". Please ensure all required criteria are created in the database via init.sql"));
        
        Integer alertLevel = alertLevelCalculator.calculateAlertLevel(criterionName, value.doubleValue());
        
        return new CriterionCalculationResult(criterion, camera, camera.getRegion(), value, alertLevel, 
            sampleSize, description);
    }
}
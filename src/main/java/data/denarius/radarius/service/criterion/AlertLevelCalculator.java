package data.denarius.radarius.service.criterion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertLevelCalculator {
    
    public Integer calculateAlertLevel(String criterionName, Double calculatedValue) {
        if (calculatedValue == null || calculatedValue < 0) {
            log.warn("Invalid value for criterion {}: {}", criterionName, calculatedValue);
            return 1;
        }
        
        if (calculatedValue >= 80.0 && calculatedValue <= 100.0) return 5;
        if (calculatedValue >= 60.0 && calculatedValue < 80.0) return 4;
        if (calculatedValue >= 40.0 && calculatedValue < 60.0) return 3;
        if (calculatedValue >= 20.0 && calculatedValue < 40.0) return 2;
        if (calculatedValue >= 0.0 && calculatedValue < 20.0) return 1;
        
        log.warn("Value out of expected range (0-100%) for criterion {}: {}", criterionName, calculatedValue);
        return 1;
    }
}
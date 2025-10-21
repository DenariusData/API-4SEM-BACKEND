package data.denarius.radarius.service.criterion;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Camera;

public interface CriterionCalculator {
    CriterionCalculationResult calculate(Camera camera);
    String getCriterionName();
}
package data.denarius.radarius.service;

import data.denarius.radarius.dto.CriterionCalculationResult;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Criterion;

import java.util.List;
import java.util.Map;

public interface CriterionCalculationService {
    Map<Camera, List<CriterionCalculationResult>> calculateAndDetectLevelChanges();
    List<CriterionCalculationResult> calculateAllCriteriaForCamera(Camera camera);
    CriterionCalculationResult calculateCriterionForCamera(Criterion criterion, Camera camera);
    CriterionCalculationResult calculateCongestion(Camera camera);
    CriterionCalculationResult calculateVehicleDensity(Camera camera);
    CriterionCalculationResult calculateLargeVehicleCirculation(Camera camera);
    CriterionCalculationResult calculateSpeedViolations(Camera camera);
    Integer calculateAlertLevel(String criterionName, Double calculatedValue);
}

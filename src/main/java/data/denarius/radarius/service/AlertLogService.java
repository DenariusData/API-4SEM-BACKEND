package data.denarius.radarius.service;

import data.denarius.radarius.dto.alertlog.AlertLogResponseDTO;
import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;

import java.util.List;

public interface AlertLogService {

    AlertLog create(Short newLevel, Criterion criterion, Region region);
    void delete(Integer id);
    AlertLog findById(Integer id);
    List<AlertLog> findByAlert(Alert alert);
}

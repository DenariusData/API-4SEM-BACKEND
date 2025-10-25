package data.denarius.radarius.service;

import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;


public interface AlertLogService {
    AlertLog create(Short newLevel, Criterion criterion, Region region);
    void delete(Integer id);
}

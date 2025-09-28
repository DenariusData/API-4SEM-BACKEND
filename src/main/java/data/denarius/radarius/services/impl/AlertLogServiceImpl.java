package data.denarius.radarius.services.impl;

import data.denarius.radarius.entity.AlertLog;
import data.denarius.radarius.repositories.AlertLogRepository;
import data.denarius.radarius.services.AlertLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertLogServiceImpl implements AlertLogService {

    private final AlertLogRepository alertLogRepository;

    public AlertLogServiceImpl(AlertLogRepository alertLogRepository) {
        this.alertLogRepository = alertLogRepository;
    }

    @Override
    public List<AlertLog> findAll() {
        return alertLogRepository.findAll();
    }

    @Override
    public Optional<AlertLog> findById(Integer id) {
        return alertLogRepository.findById(id);
    }

    @Override
    public AlertLog save(AlertLog alertLog) {
        return alertLogRepository.save(alertLog);
    }

    @Override
    public AlertLog update(Integer id, AlertLog alertLog) {
        return alertLogRepository.findById(id)
                .map(existing -> {
                    existing.setAlert(alertLog.getAlert());
                    existing.setUser(alertLog.getUser());
                    existing.setChannel(alertLog.getChannel());
                    existing.setEvent(alertLog.getEvent());
                    existing.setEventTimestamp(alertLog.getEventTimestamp());
                    existing.setStatus(alertLog.getStatus());
                    return alertLogRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("AlertLog not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!alertLogRepository.existsById(id)) {
            throw new RuntimeException("AlertLog not found with id " + id);
        }
        alertLogRepository.deleteById(id);
    }
}

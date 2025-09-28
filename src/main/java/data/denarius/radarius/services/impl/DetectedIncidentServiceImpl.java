package data.denarius.radarius.services.impl;

import data.denarius.radarius.dto.DetectedIncidentRequestDTO;
import data.denarius.radarius.dto.DetectedIncidentResponseDTO;
import data.denarius.radarius.entity.Alert;
import data.denarius.radarius.entity.DetectedIncident;
import data.denarius.radarius.entity.User;
import data.denarius.radarius.repository.AlertRepository;
import data.denarius.radarius.repository.DetectedIncidentRepository;
import data.denarius.radarius.repository.UserRepository;
import data.denarius.radarius.service.DetectedIncidentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetectedIncidentServiceImpl implements DetectedIncidentService {

    private final DetectedIncidentRepository repository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public DetectedIncidentServiceImpl(DetectedIncidentRepository repository,
                                       AlertRepository alertRepository,
                                       UserRepository userRepository) {
        this.repository = repository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
    }

    private DetectedIncidentResponseDTO toDTO(DetectedIncident incident) {
        DetectedIncidentResponseDTO dto = new DetectedIncidentResponseDTO();
        dto.setIncidentId(incident.getIncidentId());
        dto.setAlertId(incident.getAlert() != null ? incident.getAlert().getAlertId() : null);
        dto.setUserId(incident.getUser() != null ? incident.getUser().getUserId() : null);
        dto.setIncidentType(incident.getIncidentType());
        dto.setCreatedAt(incident.getCreatedAt());
        return dto;
    }

    private void mapDTOToEntity(DetectedIncidentRequestDTO dto, DetectedIncident entity) {
        entity.setIncidentType(dto.getIncidentType());
        entity.setCreatedAt(dto.getCreatedAt());

        if (dto.getAlertId() != null) {
            Alert alert = alertRepository.findById(dto.getAlertId())
                    .orElseThrow(() -> new RuntimeException("Alert not found with id " + dto.getAlertId()));
            entity.setAlert(alert);
        }

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getUserId()));
            entity.setUser(user);
        }
    }

    @Override
    public List<DetectedIncidentResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DetectedIncidentResponseDTO findById(Integer id) {
        return repository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("DetectedIncident not found with id " + id));
    }

    @Override
    public DetectedIncidentResponseDTO save(DetectedIncidentRequestDTO dto) {
        DetectedIncident incident = new DetectedIncident();
        mapDTOToEntity(dto, incident);
        return toDTO(repository.save(incident));
    }

    @Override
    public DetectedIncidentResponseDTO update(Integer id, DetectedIncidentRequestDTO dto) {
        DetectedIncident incident = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetectedIncident not found with id " + id));
        mapDTOToEntity(dto, incident);
        return toDTO(repository.save(incident));
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("DetectedIncident not found with id " + id);
        }
        repository.deleteById(id);
    }
}

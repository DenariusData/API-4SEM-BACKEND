package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.reading.ReadingRequestDTO;
import data.denarius.radarius.dto.reading.ReadingResponseDTO;
import data.denarius.radarius.entity.Reading;
import data.denarius.radarius.repository.CameraRepository;
import data.denarius.radarius.repository.ReadingRepository;
import data.denarius.radarius.service.ReadingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadingServiceImpl implements ReadingService {

    @Autowired
    private ReadingRepository readingRepository;
    @Autowired
    private CameraRepository cameraRepository;

    @Override
    public ReadingResponseDTO create(ReadingRequestDTO dto) {
        Reading reading = mapToEntity(dto);
        return mapToDTO(readingRepository.save(reading));
    }

    @Override
    public ReadingResponseDTO update(Integer id, ReadingRequestDTO dto) {
        Reading reading = readingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reading não encontrado"));
        updateEntity(reading, dto);
        return mapToDTO(readingRepository.save(reading));
    }

    @Override
    public void delete(Integer id) {
        readingRepository.deleteById(id);
    }

    @Override
    public ReadingResponseDTO findById(Integer id) {
        return readingRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Reading não encontrado"));
    }

    @Override
    public List<ReadingResponseDTO> findAll() {
        return readingRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Reading mapToEntity(ReadingRequestDTO dto) {
        Reading reading = new Reading();
        updateEntity(reading, dto);
        return reading;
    }

    private void updateEntity(Reading reading, ReadingRequestDTO dto) {
        reading.setCreatedAt(dto.getCreatedAt());
        reading.setVehicleType(dto.getVehicleType());
        reading.setSpeed(dto.getSpeed());
        reading.setPlate(dto.getPlate());

        if (dto.getCameraId() != null)
            reading.setCamera(cameraRepository.findById(dto.getCameraId()).orElse(null));
    }

    private ReadingResponseDTO mapToDTO(Reading reading) {
        ReadingResponseDTO dto = new ReadingResponseDTO();
        dto.setId(reading.getId());
        dto.setCreatedAt(reading.getCreatedAt());
        dto.setVehicleType(reading.getVehicleType());
        dto.setSpeed(reading.getSpeed());
        dto.setPlate(reading.getPlate());
        dto.setCameraRegion(reading.getCamera() != null && reading.getCamera().getRegion() != null
                ? reading.getCamera().getRegion().getName() : null);
        dto.setCameraRoad(reading.getCamera() != null && reading.getCamera().getRoad() != null
                ? reading.getCamera().getRoad().getName() : null);
        return dto;
    }
}

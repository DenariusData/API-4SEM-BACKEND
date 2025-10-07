package data.denarius.radarius.services.impl;

import data.denarius.radarius.dtos.reading.ReadingRequestDTO;
import data.denarius.radarius.dtos.reading.ReadingResponseDTO;
import data.denarius.radarius.entity.Camera;
import data.denarius.radarius.entity.Reading;
import data.denarius.radarius.repositories.CameraRepository;
import data.denarius.radarius.repositories.ReadingRepository;
import data.denarius.radarius.services.ReadingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;
    private final CameraRepository cameraRepository;

    public ReadingServiceImpl(ReadingRepository readingRepository, CameraRepository cameraRepository) {
        this.readingRepository = readingRepository;
        this.cameraRepository = cameraRepository;
    }

    private ReadingResponseDTO toDTO(Reading reading) {
        ReadingResponseDTO dto = new ReadingResponseDTO();
        dto.setReadingId(reading.getReadingId());
        dto.setCameraId(reading.getCamera() != null ? reading.getCamera().getCameraId() : null);
        dto.setTimestamp(reading.getTimestamp());
        dto.setVehicleType(reading.getVehicleType());
        dto.setSpeed(reading.getSpeed());
        dto.setPlate(reading.getPlate());
        return dto;
    }

    private void mapDTOToEntity(ReadingRequestDTO dto, Reading entity) {
        if (dto.getCameraId() != null) {
            Camera camera = cameraRepository.findById(dto.getCameraId())
                    .orElseThrow(() -> new RuntimeException("Camera not found with id " + dto.getCameraId()));
            entity.setCamera(camera);
        }
        entity.setTimestamp(dto.getTimestamp());
        entity.setVehicleType(dto.getVehicleType());
        entity.setSpeed(dto.getSpeed());
        entity.setPlate(dto.getPlate());
    }

    @Override
    public List<ReadingResponseDTO> findAll() {
        return readingRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReadingResponseDTO findById(Integer id) {
        Reading reading = readingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reading not found with id " + id));
        return toDTO(reading);
    }

    @Override
    public ReadingResponseDTO save(ReadingRequestDTO dto) {
        Reading reading = new Reading();
        mapDTOToEntity(dto, reading);
        return toDTO(readingRepository.save(reading));
    }

    @Override
    public ReadingResponseDTO update(Integer id, ReadingRequestDTO dto) {
        Reading reading = readingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reading not found with id " + id));
        mapDTOToEntity(dto, reading);
        return toDTO(readingRepository.save(reading));
    }

    @Override
    public void delete(Integer id) {
        if (!readingRepository.existsById(id)) {
            throw new RuntimeException("Reading not found with id " + id);
        }
        readingRepository.deleteById(id);
    }
}

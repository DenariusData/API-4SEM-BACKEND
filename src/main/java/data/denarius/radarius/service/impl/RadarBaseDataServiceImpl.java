package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.radarbasedata.RadarBaseDataRequestDTO;
import data.denarius.radarius.dto.radarbasedata.RadarBaseDataResponseDTO;
import data.denarius.radarius.entity.RadarBaseData;
import data.denarius.radarius.repository.RadarBaseDataRepository;
import data.denarius.radarius.service.RadarBaseDataService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RadarBaseDataServiceImpl implements RadarBaseDataService {

    @Autowired
    private RadarBaseDataRepository radarBaseDataRepository;

    @Override
    public RadarBaseDataResponseDTO create(RadarBaseDataRequestDTO dto) {
        RadarBaseData radarBaseData = mapToEntity(dto);
        return mapToDTO(radarBaseDataRepository.save(radarBaseData));
    }

    @Override
    public RadarBaseDataResponseDTO update(Long id, RadarBaseDataRequestDTO dto) {
        RadarBaseData radarBaseData = radarBaseDataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Radar base data not found"));
        updateEntity(radarBaseData, dto);
        return mapToDTO(radarBaseDataRepository.save(radarBaseData));
    }

    @Override
    public void delete(Long id) {
        radarBaseDataRepository.deleteById(id);
    }

    @Override
    public RadarBaseDataResponseDTO findById(Long id) {
        return radarBaseDataRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Radar base data not found"));
    }

    @Override
    public List<RadarBaseDataResponseDTO> findAll() {
        return radarBaseDataRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findByCity(String city) {
        return radarBaseDataRepository.findByCityIgnoreCase(city).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findByVehicleType(String vehicleType) {
        return radarBaseDataRepository.findByVehicleTypeIgnoreCase(vehicleType).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findByCameraId(String cameraId) {
        return radarBaseDataRepository.findByCameraId(cameraId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findByDirection(String direction) {
        return radarBaseDataRepository.findByDirectionIgnoreCase(direction).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findByDateTimeBetween(LocalDateTime start, LocalDateTime end) {
        return radarBaseDataRepository.findByDateTimeBetween(start, end).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findAllOrderByDateTimeDesc() {
        return radarBaseDataRepository.findAllOrderByDateTimeDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findVehiclesAboveSpeedLimit() {
        return radarBaseDataRepository.findVehiclesAboveSpeedLimit().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findByCityAndVehicleType(String city, String vehicleType) {
        return radarBaseDataRepository.findByCityIgnoreCaseAndVehicleTypeIgnoreCase(city, vehicleType).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarBaseDataResponseDTO> findRecentRecords() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return radarBaseDataRepository.findRecentRecords(twentyFourHoursAgo).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RadarBaseData mapToEntity(RadarBaseDataRequestDTO dto) {
        return RadarBaseData.builder().build();
    }

    private void updateEntity(RadarBaseData radarBaseData, RadarBaseDataRequestDTO dto) {
        radarBaseData.setCameraLatitude(dto.getCameraLatitude());
        radarBaseData.setCameraLongitude(dto.getCameraLongitude());
        radarBaseData.setCameraId(dto.getCameraId());
        radarBaseData.setCameraLane(dto.getCameraLane());
        radarBaseData.setTotalLanes(dto.getTotalLanes());
        radarBaseData.setDateTime(dto.getDateTime());
        radarBaseData.setVehicleType(dto.getVehicleType());
        radarBaseData.setVehicleSpeed(dto.getVehicleSpeed());
        radarBaseData.setSpeedLimit(dto.getSpeedLimit());
        radarBaseData.setAddress(dto.getAddress());
        radarBaseData.setNumber(dto.getNumber());
        radarBaseData.setCity(dto.getCity());
        radarBaseData.setDirection(dto.getDirection());
    }

    private RadarBaseDataResponseDTO mapToDTO(RadarBaseData radarBaseData) {
        RadarBaseDataResponseDTO dto = new RadarBaseDataResponseDTO();
        dto.setId(radarBaseData.getId());
        dto.setCameraLatitude(radarBaseData.getCameraLatitude());
        dto.setCameraLongitude(radarBaseData.getCameraLongitude());
        dto.setCameraId(radarBaseData.getCameraId());
        dto.setCameraLane(radarBaseData.getCameraLane());
        dto.setTotalLanes(radarBaseData.getTotalLanes());
        dto.setDateTime(radarBaseData.getDateTime());
        dto.setVehicleType(radarBaseData.getVehicleType());
        dto.setVehicleSpeed(radarBaseData.getVehicleSpeed());
        dto.setSpeedLimit(radarBaseData.getSpeedLimit());
        dto.setAddress(radarBaseData.getAddress());
        dto.setNumber(radarBaseData.getNumber());
        dto.setCity(radarBaseData.getCity());
        dto.setDirection(radarBaseData.getDirection());
        return dto;
    }
}
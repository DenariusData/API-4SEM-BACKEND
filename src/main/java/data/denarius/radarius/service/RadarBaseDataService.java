package data.denarius.radarius.service;

import data.denarius.radarius.dto.radarbasedata.RadarBaseDataRequestDTO;
import data.denarius.radarius.dto.radarbasedata.RadarBaseDataResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface RadarBaseDataService {
    RadarBaseDataResponseDTO create(RadarBaseDataRequestDTO dto);
    RadarBaseDataResponseDTO update(Long id, RadarBaseDataRequestDTO dto);
    void delete(Long id);
    RadarBaseDataResponseDTO findById(Long id);
    List<RadarBaseDataResponseDTO> findAll();
    List<RadarBaseDataResponseDTO> findByCity(String city);
    List<RadarBaseDataResponseDTO> findByVehicleType(String vehicleType);
    List<RadarBaseDataResponseDTO> findByCameraId(String cameraId);
    List<RadarBaseDataResponseDTO> findByDirection(String direction);
    List<RadarBaseDataResponseDTO> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<RadarBaseDataResponseDTO> findAllOrderByDateTimeDesc();
    List<RadarBaseDataResponseDTO> findVehiclesAboveSpeedLimit();
    List<RadarBaseDataResponseDTO> findByCityAndVehicleType(String city, String vehicleType);
    List<RadarBaseDataResponseDTO> findRecentRecords();
}
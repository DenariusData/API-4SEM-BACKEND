package data.denarius.radarius.service;

import data.denarius.radarius.dto.reading.ReadingRequestDTO;
import data.denarius.radarius.dto.reading.ReadingResponseDTO;

import java.util.List;

public interface ReadingService {
    ReadingResponseDTO create(ReadingRequestDTO dto);
    ReadingResponseDTO update(Integer id, ReadingRequestDTO dto);
    void delete(Integer id);
    ReadingResponseDTO findById(Integer id);
    List<ReadingResponseDTO> findAll();
}

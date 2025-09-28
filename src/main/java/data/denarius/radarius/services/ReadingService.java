package data.denarius.radarius.services;

import data.denarius.radarius.dto.ReadingRequestDTO;
import data.denarius.radarius.dto.ReadingResponseDTO;

import java.util.List;

public interface ReadingService {

    List<ReadingResponseDTO> findAll();

    ReadingResponseDTO findById(Integer id);

    ReadingResponseDTO save(ReadingRequestDTO dto);

    ReadingResponseDTO update(Integer id, ReadingRequestDTO dto);

    void delete(Integer id);
}

package data.denarius.radarius.services.impl;

import data.denarius.radarius.dto.ProtocolRequestDTO;
import data.denarius.radarius.dto.ProtocolResponseDTO;
import data.denarius.radarius.entity.Protocol;
import data.denarius.radarius.entity.User;
import data.denarius.radarius.repositories.ProtocolRepository;
import data.denarius.radarius.repositories.UserRepository;
import data.denarius.radarius.services.ProtocolService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProtocolServiceImpl implements ProtocolService {

    private final ProtocolRepository repository;
    private final UserRepository userRepository;

    public ProtocolServiceImpl(ProtocolRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private ProtocolResponseDTO toDTO(Protocol protocol) {
        ProtocolResponseDTO dto = new ProtocolResponseDTO();
        dto.setProtocolId(protocol.getProtocolId());
        dto.setName(protocol.getName());
        dto.setCreatedAt(protocol.getCreatedAt());
        dto.setCreatedById(protocol.getCreatedBy() != null ? protocol.getCreatedBy().getUserId() : null);
        return dto;
    }

    private void mapDTOToEntity(ProtocolRequestDTO dto, Protocol entity) {
        entity.setName(dto.getName());
        entity.setCreatedAt(dto.getCreatedAt());

        if (dto.getCreatedById() != null) {
            User user = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getCreatedById()));
            entity.setCreatedBy(user);
        }
    }

    @Override
    public List<ProtocolResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProtocolResponseDTO findById(Integer id) {
        return repository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Protocol not found with id " + id));
    }

    @Override
    public ProtocolResponseDTO save(ProtocolRequestDTO dto) {
        Protocol protocol = new Protocol();
        mapDTOToEntity(dto, protocol);
        return toDTO(repository.save(protocol));
    }

    @Override
    public ProtocolResponseDTO update(Integer id, ProtocolRequestDTO dto) {
        Protocol protocol = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocol not found with id " + id));
        mapDTOToEntity(dto, protocol);
        return toDTO(repository.save(protocol));
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Protocol not found with id " + id);
        }
        repository.deleteById(id);
    }
}

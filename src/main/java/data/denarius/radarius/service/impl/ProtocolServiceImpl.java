package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.protocol.ProtocolRequestDTO;
import data.denarius.radarius.dto.protocol.ProtocolResponseDTO;
import data.denarius.radarius.entity.Person;
import data.denarius.radarius.entity.Protocol;
import data.denarius.radarius.repository.PersonRepository;
import data.denarius.radarius.repository.ProtocolRepository;
import data.denarius.radarius.service.ProtocolService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProtocolServiceImpl implements ProtocolService {

    @Autowired
    private ProtocolRepository protocolRepository;
    @Autowired
    private PersonRepository personRepository;

    @Override
    public ProtocolResponseDTO create(ProtocolRequestDTO dto) {
        Protocol protocol = mapToEntity(dto);
        return mapToDTO(protocolRepository.save(protocol));
    }

    @Override
    public ProtocolResponseDTO update(Integer id, ProtocolRequestDTO dto) {
        Protocol protocol = protocolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Protocol não encontrado"));
        updateEntity(protocol, dto);
        return mapToDTO(protocolRepository.save(protocol));
    }

    @Override
    public void delete(Integer id) {
        protocolRepository.deleteById(id);
    }

    @Override
    public ProtocolResponseDTO findById(Integer id) {
        return protocolRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Protocol não encontrado"));
    }

    @Override
    public List<ProtocolResponseDTO> findAll() {
        return protocolRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Protocol mapToEntity(ProtocolRequestDTO dto) {
        Protocol protocol = new Protocol();
        updateEntity(protocol, dto);
        return protocol;
    }

    private void updateEntity(Protocol protocol, ProtocolRequestDTO dto) {
        protocol.setName(dto.getName());
        protocol.setDescription(dto.getDescription());
        protocol.setCreatedAt(dto.getCreatedAt());

        if (dto.getCreatedById() != null)
            protocol.setCreatedBy(personRepository.findById(dto.getCreatedById()).orElse(null));
    }

    private ProtocolResponseDTO mapToDTO(Protocol protocol) {
        ProtocolResponseDTO dto = new ProtocolResponseDTO();
        dto.setId(protocol.getId());
        dto.setName(protocol.getName());
        dto.setDescription(protocol.getDescription());
        dto.setCreatedAt(protocol.getCreatedAt());
        dto.setCreatedByName(protocol.getCreatedBy() != null ? protocol.getCreatedBy().getName() : null);
        return dto;
    }
}

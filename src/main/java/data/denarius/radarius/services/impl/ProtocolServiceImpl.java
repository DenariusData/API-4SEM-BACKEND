package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.Protocol;
import data.denarius.radarius.repository.ProtocolRepository;
import data.denarius.radarius.service.ProtocolService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProtocolServiceImpl implements ProtocolService {

    private final ProtocolRepository protocolRepository;

    public ProtocolServiceImpl(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    @Override
    public List<Protocol> findAll() {
        return protocolRepository.findAll();
    }

    @Override
    public Optional<Protocol> findById(Integer id) {
        return protocolRepository.findById(id);
    }

    @Override
    public Protocol save(Protocol protocol) {
        return protocolRepository.save(protocol);
    }

    @Override
    public Protocol update(Integer id, Protocol protocol) {
        return protocolRepository.findById(id)
                .map(existing -> {
                    existing.setName(protocol.getName());
                    existing.setCreatedAt(protocol.getCreatedAt());
                    existing.setCreatedBy(protocol.getCreatedBy());
                    existing.setAlerts(protocol.getAlerts());
                    return protocolRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Protocol not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!protocolRepository.existsById(id)) {
            throw new RuntimeException("Protocol not found with id " + id);
        }
        protocolRepository.deleteById(id);
    }
}

package data.denarius.radarius.services.impl;

import data.denarius.radarius.entity.Road;
import data.denarius.radarius.repository.RoadRepository;
import data.denarius.radarius.service.RoadService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoadServiceImpl implements RoadService {

    private final RoadRepository roadRepository;

    public RoadServiceImpl(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
    }

    @Override
    public List<Road> findAll() {
        return roadRepository.findAll();
    }

    @Override
    public Optional<Road> findById(Integer id) {
        return roadRepository.findById(id);
    }

    @Override
    public Road save(Road road) {
        return roadRepository.save(road);
    }

    @Override
    public Road update(Integer id, Road road) {
        return roadRepository.findById(id)
                .map(existing -> {
                    existing.setAddress(road.getAddress());
                    existing.setSpeedLimit(road.getSpeedLimit());
                    existing.setCreatedAt(road.getCreatedAt());
                    existing.setUpdatedAt(road.getUpdatedAt());
                    existing.setCameras(road.getCameras());
                    return roadRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Road not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!roadRepository.existsById(id)) {
            throw new RuntimeException("Road not found with id " + id);
        }
        roadRepository.deleteById(id);
    }
}

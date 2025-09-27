package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.Reading;
import data.denarius.radarius.repository.ReadingRepository;
import data.denarius.radarius.service.ReadingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;

    public ReadingServiceImpl(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    @Override
    public List<Reading> findAll() {
        return readingRepository.findAll();
    }

    @Override
    public Optional<Reading> findById(Integer id) {
        return readingRepository.findById(id);
    }

    @Override
    public Reading save(Reading reading) {
        return readingRepository.save(reading);
    }

    @Override
    public Reading update(Integer id, Reading reading) {
        return readingRepository.findById(id)
                .map(existing -> {
                    existing.setCamera(reading.getCamera());
                    existing.setTimestamp(reading.getTimestamp());
                    existing.setVehicleType(reading.getVehicleType());
                    existing.setSpeed(reading.getSpeed());
                    existing.setPlate(reading.getPlate());
                    return readingRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reading not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!readingRepository.existsById(id)) {
            throw new RuntimeException("Reading not found with id " + id);
        }
        readingRepository.deleteById(id);
    }
}

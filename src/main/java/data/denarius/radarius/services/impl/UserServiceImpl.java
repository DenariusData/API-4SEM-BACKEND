package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.User;
import data.denarius.radarius.repository.UserRepository;
import data.denarius.radarius.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Integer id, User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setName(user.getName());
                    existing.setWhatsapp(user.getWhatsapp());
                    existing.setEmail(user.getEmail());
                    existing.setRole(user.getRole());
                    existing.setCreatedAt(user.getCreatedAt());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}

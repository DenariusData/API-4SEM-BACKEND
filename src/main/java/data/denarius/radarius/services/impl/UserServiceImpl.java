package data.denarius.radarius.services.impl;

import data.denarius.radarius.dto.UserRequestDTO;
import data.denarius.radarius.dto.UserResponseDTO;
import data.denarius.radarius.entity.User;
import data.denarius.radarius.repository.UserRepository;
import data.denarius.radarius.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setWhatsapp(user.getWhatsapp());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private void mapDTOToEntity(UserRequestDTO dto, User user) {
        user.setName(dto.getName());
        user.setWhatsapp(dto.getWhatsapp());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setCreatedAt(dto.getCreatedAt());
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO findById(Integer id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public UserResponseDTO save(UserRequestDTO dto) {
        User user = new User();
        mapDTOToEntity(dto, user);
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO update(Integer id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        mapDTOToEntity(dto, user);
        return toDTO(userRepository.save(user));
    }

    @Override
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}

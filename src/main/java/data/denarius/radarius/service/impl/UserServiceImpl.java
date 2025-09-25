package data.denarius.radarius.service.impl;

import data.denarius.radarius.entity.User;
import data.denarius.radarius.service.UserService;
import org.springframework.stereotype.Service;
import data.denarius.radarius.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

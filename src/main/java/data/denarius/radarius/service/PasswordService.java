package data.denarius.radarius.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return passwordEncoder.encode(rawPassword);
    }
}
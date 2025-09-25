package data.denarius.radarius.service;

import data.denarius.radarius.dto.login.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(String email, String password);
}

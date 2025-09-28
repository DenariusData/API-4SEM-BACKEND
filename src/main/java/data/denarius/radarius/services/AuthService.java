package data.denarius.radarius.services;

import data.denarius.radarius.dtos.login.LoginResponseDTO;

import javax.naming.AuthenticationException;

public interface AuthService {
    LoginResponseDTO login(String email, String password) throws AuthenticationException;
}

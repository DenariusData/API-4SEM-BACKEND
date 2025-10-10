package data.denarius.radarius.controller;

import data.denarius.radarius.dto.login.LoginRequestDTO;
import data.denarius.radarius.dto.login.LoginResponseDTO;
import data.denarius.radarius.exceptions.AuthorizationException;
import data.denarius.radarius.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) throws AuthenticationException {
        return ResponseEntity.ok(authService.login(dto.email(), dto.password()));
    }
}

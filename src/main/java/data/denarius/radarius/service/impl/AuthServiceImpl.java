package data.denarius.radarius.service.impl;

import data.denarius.radarius.dto.login.LoginResponseDTO;
import data.denarius.radarius.security.JwtIssuer;
import data.denarius.radarius.security.UserPrincipal;
import data.denarius.radarius.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponseDTO login(String email, String password) {
        var auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(auth);
        var userPrincipal = (UserPrincipal)auth.getPrincipal();

        var token = jwtIssuer.issue(
            userPrincipal.getUserId(),
            userPrincipal.getEmail(),
            userPrincipal.getRole().toString()
        );

        return new LoginResponseDTO(token, userPrincipal.getRole());
    }
}

package data.denarius.radarius.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

@Component
public class JwtToPrincipalConverter {
    public static UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(Integer.valueOf(jwt.getSubject()))
                .email(jwt.getClaim("e").asString())
                .build();
    }
}

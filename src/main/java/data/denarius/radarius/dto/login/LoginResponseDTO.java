package data.denarius.radarius.dto.login;

import data.denarius.radarius.enums.RoleEnum;

public record LoginResponseDTO(
        String token,
        RoleEnum role,
        String name,
        String email
) {}

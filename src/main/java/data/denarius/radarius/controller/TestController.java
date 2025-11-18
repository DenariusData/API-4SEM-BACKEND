package data.denarius.radarius.controller;

import data.denarius.radarius.enums.RoleEnum;
import data.denarius.radarius.security.UserPrincipal;
import data.denarius.radarius.security.annotations.RequireAgenteOrGestorRole;
import data.denarius.radarius.security.annotations.RequireAnyRole;
import data.denarius.radarius.security.annotations.RequireGestorRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api-test")
public class TestController {

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API funcionando!");
    }

    @GetMapping("/secured")
    public ResponseEntity<Map<String, Object>> securedEndpoint(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Map.of(
            "message", "Endpoint protegido acessado com sucesso!",
            "userId", userPrincipal.getUserId(),
            "email", userPrincipal.getEmail(),
            "role", userPrincipal.getRole() != null ? userPrincipal.getRole().toString() : "ROLE_NOT_SET"
        ));
    }

    @GetMapping("/role-check")
    @RequireAnyRole
    public ResponseEntity<Map<String, Object>> roleCheck(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        RoleEnum userRole = userPrincipal.getRole();
        
        return ResponseEntity.ok(Map.of(
            "userId", userPrincipal.getUserId(),
            "email", userPrincipal.getEmail(),
            "role", userRole != null ? userRole.toString() : "ROLE_NOT_SET",
            "permissions", getPermissionsByRole(userRole)
        ));
    }

    @GetMapping("/gestor-only")
    @RequireGestorRole
    public ResponseEntity<Map<String, Object>> gestorOnly(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Map.of(
            "message", "Acesso exclusivo para GESTOR",
            "user", userPrincipal.getEmail(),
            "role", userPrincipal.getRole().toString()
        ));
    }

    @GetMapping("/agente-or-gestor")
    @RequireAgenteOrGestorRole
    public ResponseEntity<Map<String, Object>> agenteOrGestor(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(Map.of(
            "message", "Acesso para AGENTE ou GESTOR",
            "user", userPrincipal.getEmail(),
            "role", userPrincipal.getRole().toString()
        ));
    }

    private Map<String, Boolean> getPermissionsByRole(RoleEnum role) {
        if (role == null) {
            return Map.of(
                "canManageUsers", false,
                "canViewReports", false,
                "canCreateAlerts", false,
                "canViewPublicData", true
            );
        }
        
        return switch (role) {
            case ROLE_ADMIN, ROLE_GESTOR -> Map.of(
                    "canManageUsers", true,
                    "canViewReports", true,
                    "canCreateAlerts", true,
                    "canViewPublicData", true,
                    "canManageSystem", true
            );
            case ROLE_AGENTE -> Map.of(
                "canManageUsers", false,
                "canViewReports", true,
                "canCreateAlerts", true,
                "canViewPublicData", true,
                "canManageSystem", false
            );
            case ROLE_CIVIL -> Map.of(
                "canManageUsers", false,
                "canViewReports", false,
                "canCreateAlerts", false,
                "canViewPublicData", true,
                "canManageSystem", false
            );
        };
    }
}

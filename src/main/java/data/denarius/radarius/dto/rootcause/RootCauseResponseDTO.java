package data.denarius.radarius.dto.rootcause;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RootCauseResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String personName;
}

package data.denarius.radarius.dto.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String createdByName;
}

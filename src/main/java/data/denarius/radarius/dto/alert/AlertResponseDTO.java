package data.denarius.radarius.dto.alert;

import data.denarius.radarius.enums.SourceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponseDTO {
    private Integer id;
    private Short level;
    private String message;
    private String conclusion;
    private SourceTypeEnum sourceType;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    private String createdByName;
    private String assignedToName;

    private String criterionName;
    private Integer criterionId;

    private String regionName;
    private Integer regionId;

    private String rootCauseName;
    private String protocolName;
}

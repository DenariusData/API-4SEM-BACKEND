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
public class AlertRequestDTO {
    private Short level;
    private String message;
    private String conclusion;
    private SourceTypeEnum sourceType;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Integer createdById;
    private Integer assignedToId;
    private Integer regionId;
    private Integer criterionId;
    private Integer rootCauseId;
    private Integer protocolId;
}

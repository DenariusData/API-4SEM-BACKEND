package data.denarius.radarius.dto.alertlog;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertLogRecentResponseDTO {
    private Integer id;
    private Integer alertId;
    private String indicator;
    private Short previousLevel;
    private Short newLevel;
    private String location;
    private String timestamp;
    private Boolean finalized;
}

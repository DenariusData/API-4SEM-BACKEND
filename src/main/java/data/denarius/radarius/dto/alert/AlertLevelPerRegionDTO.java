package data.denarius.radarius.dto.alert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AlertLevelPerRegionDTO {
    
    @JsonProperty("region_id")
    private Integer regionId;
    
    @JsonProperty("level")
    private Integer level;
}

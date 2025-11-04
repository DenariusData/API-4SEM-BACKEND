package data.denarius.radarius.dto.error;

import lombok.Getter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ErrorResponse {
    private final String timestamp;
    private final String error;
    private final String message;
    private final int status;

    public ErrorResponse(String error, String message, int status) {
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.error = error;
        this.message = message;
        this.status = status;
    }
}
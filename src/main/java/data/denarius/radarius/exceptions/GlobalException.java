package data.denarius.radarius.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class GlobalException extends RuntimeException {
    public ProblemDetail toProblemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pb.setTitle("Library Api Internal Server Error");

        return pb;
    }
}

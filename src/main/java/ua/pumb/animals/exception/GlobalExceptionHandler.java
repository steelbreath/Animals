package ua.pumb.animals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({SuchFormatNotSupportedException.class})
    public ResponseEntity<ProblemDetail> handleSuchFormatNotSupportedException(
            SuchFormatNotSupportedException ex, WebRequest request) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setInstance(URI.create(request.getDescription(false)));
        detail.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.of(detail).build();
    }
}

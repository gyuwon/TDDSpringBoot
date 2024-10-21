package commerce.api.controller;

import commerce.commandmodel.InvariantViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvariantViolationExceptionHandler {

    @ExceptionHandler(InvariantViolationException.class)
    public ResponseEntity<?> handle() {
        return ResponseEntity.badRequest().build();
    }
}

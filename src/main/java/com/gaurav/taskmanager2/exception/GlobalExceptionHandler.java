package com.gaurav.taskmanager2.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 📌 Triggered by:
     * - POST /api/task/create
     * - PUT /api/task/update-task
     * 🧨 Reason: Invalid JSON or bad format like wrong `dueDate`
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidInput(HttpMessageNotReadableException httpMessageNotReadableException) {
        Throwable rootCause = httpMessageNotReadableException.getCause();
        if (rootCause instanceof DateTimeParseException) {
            return new ResponseEntity<>(Map.of(
                    "error", "Invalid format. Expected format: yyyy-MM-dd for date fields"
            ), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("error", "Malformed JSON or bad request format"), HttpStatus.BAD_REQUEST);
    }

    /**
     * 📌 Triggered by:
     * - POST /api/public/sign-up
     * - PUT /api/admin/update-user
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException validException) {
        String message = validException.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Invalid input");
        return new ResponseEntity<>(Map.of("message",message),HttpStatus.BAD_REQUEST);
    }

    /**
     * 📌 Triggered by:
     * - Any validation that fails outside DTO (e.g. @RequestParam)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        return ResponseEntity.badRequest().body(Map.of("error",constraintViolationException.getMessage()));
    }

    /**
     * 📌 Triggered by:
     * - GET /api/task/get-task/status
     * - GET /api/task/get-task/priority
     * - PUT /api/admin/update-role
     * 🧨 Reason: Invalid enum value (e.g., status = "high")
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ResponseEntity.badRequest().body(Map.of("error",illegalArgumentException.getMessage()));
    }

    /**
     * 📌 Triggered by:
     * - POST /auth (JWT login)

     * 🧨 Reason: Wrong username or password
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialException(BadCredentialsException badCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid username of password"));
    }

    /**
     * 📌 Triggered by:
     * - GET /api/task/get-task
     * - DELETE /api/task/delete-task
     * - PUT /api/task/update-task
     * - DELETE /api/admin/delete-user
     * - GET /api/admin/get-user
     * - PUT /api/admin/update-role
     * 🧨 Reason: Custom service layer errors (not found types)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeException(RuntimeException runtimeException) {
        return ResponseEntity.badRequest().body(Map.of("error",runtimeException.getMessage()));
    }

    /**
     * 📌 Catch-all fallback handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","something went wrong please try again"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Missing required parameter: " + ex.getParameterName()
        ));
    }

}

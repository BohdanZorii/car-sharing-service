package com.zorii.carsharing.exception;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessage> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    String errors = ex.getAllErrors().stream()
        .map(this::getErrorMessage)
        .collect(Collectors.joining(", "));
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessage(errors));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ErrorMessage(ex.getMessage()));
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorMessage> handleDuplicateEmailException(DuplicateEmailException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ErrorMessage(ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorMessage(ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ErrorMessage(ex.getMessage()));
  }

  private String getErrorMessage(ObjectError objectError) {
    if (objectError instanceof FieldError fieldError) {
      return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }
    return objectError.getDefaultMessage();
  }
}

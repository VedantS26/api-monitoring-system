package com.vedant.apimonitor.ExceptionHandler;

import com.vedant.apimonitor.Exception.DuplicateResourceException;
import com.vedant.apimonitor.Exception.InvalidRequestException;
import com.vedant.apimonitor.Exception.ResourceNotFoundException;
import com.vedant.apimonitor.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String code
    ) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                code,
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);

    }

    //Business Exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex){
        logger.error("Resource not Found", ex);
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(),"Resource_Not_Found" );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex){
        logger.error("Duplicate resource", ex);
        return buildResponse(HttpStatus.CONFLICT,ex.getMessage(),"Duplicate_Resource");
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidRequestException ex){
        logger.error("Invalid Request", ex);
        return buildResponse(HttpStatus.BAD_REQUEST,ex.getMessage(),"Invalid_Request");
    }

    //Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName,errorMessage);
        });

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                "Validation_Error",
                LocalDateTime.now(),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);



    }

    //Security

      @ExceptionHandler()
      public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex){
          logger.error("Bad credentials",ex);
          return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password","BAD_CREDENTIALS");

      }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex) {
        logger.error("User disabled", ex);
        return buildResponse(HttpStatus.FORBIDDEN, "User account is disabled", "USER_DISABLED");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        logger.error("Access denied", ex);
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", "ACCESS_DENIED");
    }

    // ================= JWT =================

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        logger.error("JWT expired", ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "JWT token expired", "TOKEN_EXPIRED");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ex) {
        logger.error("Malformed JWT", ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid JWT format", "MALFORMED_TOKEN");
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignature(SignatureException ex) {
        logger.error("Invalid signature", ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid JWT signature", "INVALID_SIGNATURE");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> handleUnsupported(UnsupportedJwtException ex) {
        logger.error("Unsupported JWT", ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unsupported JWT token", "UNSUPPORTED_TOKEN");
    }

    // ================= OTHER =================

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        logger.error("Endpoint not found", ex);
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Endpoint not found: " + ex.getRequestURL(),
                "ENDPOINT_NOT_FOUND"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        logger.error("Invalid JSON", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format", "INVALID_JSON");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Illegal argument", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "ILLEGAL_ARGUMENT");
    }

    // ================= FALLBACK =================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        logger.error("Unexpected error", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR"
        );
    }
}





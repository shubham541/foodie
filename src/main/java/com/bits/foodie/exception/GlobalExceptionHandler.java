package com.bits.foodie.exception;

import com.bits.foodie.dto.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseDTO handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseDTO(ex.getMessage(), false);
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDTO handleInvalidInputException(InvalidInputException ex) {
        return new ResponseDTO(ex.getMessage(), false);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDTO handleBadRequestException(IllegalArgumentException ex) {
        return new ResponseDTO(ex.getMessage(), false);
    }

    @ExceptionHandler(OperationFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDTO handleOperationFailedException(OperationFailedException ex) {
        return new ResponseDTO("Operation failed: " + ex.getMessage(), false);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDTO handleGenericException(Exception ex) {
        return new ResponseDTO("An unexpected error occurred: " + ex.getMessage(), false);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseDTO handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseDTO("Requested resource not found: " + ex.getMessage(), false);
    }
}

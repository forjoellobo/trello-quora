package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
// control is handed over to this class when a user defined exception is thrown
public class RestExceptionHandler {
//@ExceptionHandler annotation handles exceptions in specific handler classes or handler methods.
    @ExceptionHandler(AuthorizationFailedException.class)
     // This method handles the AuthorizationFailed exception
    // Takes AuthorizationFailedException object and a Webrequest object as a parameter and returns a ResponseEntity of type "ErrorResponse"
    public ResponseEntity<ErrorResponse> authorizationFailedException(
            AuthorizationFailedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidQuestionException.class)
    // This method handles the invalidQuestion exception
    // Takes InvalidQuestionException object and a Webrequest object as a parameter and returns a ResponseEntity of type "ErrorResponse"
    public ResponseEntity<ErrorResponse> invalidQuestionResponse(
            InvalidQuestionException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AnswerNotFoundException.class)
    // This method handles the AnswerNotFound exception
    // Takes AnswerNotFoundException object and a Webrequest object as a parameter and returns a ResponseEntity of type "ErrorResponse"
    public ResponseEntity<ErrorResponse> answerNotFoundResponse(
            AnswerNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }
}

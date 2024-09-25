package com.hairsalonbookingapp.hairsalon.exception;

import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationHandler {
    //dinh nghia cho moi khi chay gap exception nao do
    @ExceptionHandler(MethodArgumentNotValidException.class) //MethodArgumentNotValidException la loi khi ng dung nhap sai
    public ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
        String message = "";
        //cu 1 thuoc tinh loi => su li
        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()){
            //FieldError la name, studentCode, score
            message += fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity handleBadCredentials(AccountNotFoundException exception){
        return new ResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity handleAccountBlocked(AccountBlockedException exception){
        return new ResponseEntity(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    //@ResponseStatus(HttpStatus.BAD_REQUEST)// cái này báo cho front end biết mã lỗi status: INPUT đầu vào sai, front end check lại
    public ResponseEntity handleGenericException(Exception exception){
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }


}

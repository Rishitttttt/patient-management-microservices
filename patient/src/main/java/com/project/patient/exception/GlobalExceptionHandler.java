package com.project.patient.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<Map<String,String>> hanleEmailAlreadyExistsException(EmailAlreadyExistsException ex){

        Map<String,String> errors = new HashMap<>();
        errors.put("message","Email Already exists");
        return ResponseEntity.badRequest().body(errors);

    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String,String>> handlePateintNotFOundExcception(PatientNotFoundException ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("message","Pateint not Found");
        return ResponseEntity.badRequest().body(errors);
    }


}

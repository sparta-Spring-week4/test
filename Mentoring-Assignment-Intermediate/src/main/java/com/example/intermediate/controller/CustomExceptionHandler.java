package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseDto<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult()
        .getAllErrors()
        .get(0)
        .getDefaultMessage();

    return ResponseDto.fail("BAD_REQUEST", errorMessage);
  }

  //업로드 용량 초과 시
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected ResponseDto<?> handleMaxUploadSizeExceededException(
          MaxUploadSizeExceededException e) {
      String errorMessage = "업로드 용량이 초과되었습니다.";
      return ResponseDto.fail("UploadSizeExceeded",errorMessage);
  }

}

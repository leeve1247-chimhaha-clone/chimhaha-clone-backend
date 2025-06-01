package com.multirkh.chimhahaclone.common.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse() {

            @NotNull
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatus.BAD_REQUEST;
            }

            @NotNull
            @Override
            public ProblemDetail getBody() {
                ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                problemDetail.setDetail(ex.getMessage());
                return problemDetail;
            }

            @NotNull
            @Override
            public String getTypeMessageCode() {
                return "";
            }

            @NotNull
            @Override
            public String getTitleMessageCode() {
                return "";
            }

            @NotNull
            @Override
            public String getDetailMessageCode() {
                return "";
            }
        });
    }
}


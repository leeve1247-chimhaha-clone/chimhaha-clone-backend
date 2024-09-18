package com.multirkh.chimhahaclone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class FindDeletedPostException extends RuntimeException {
    public FindDeletedPostException() {
        super("This post has been deleted");
    }
}
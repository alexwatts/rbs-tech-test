package com.rbs.transfer.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNPROCESSABLE_ENTITY)
public class InsufficentFundsException extends Exception {

    public InsufficentFundsException(String message) {
        super(message);
    }
}

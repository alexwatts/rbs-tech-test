package com.rbs.transfer.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNPROCESSABLE_ENTITY)
public class AccountDetailsInvalidException extends Exception {

    public AccountDetailsInvalidException(String meessage) {
        super(meessage);
    }

}

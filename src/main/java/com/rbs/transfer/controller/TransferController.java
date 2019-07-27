package com.rbs.transfer.controller;

import com.rbs.transfer.model.Transfer;
import com.rbs.transfer.service.BankingService;
import com.rbs.transfer.service.exception.AccountDetailsInvalidException;
import com.rbs.transfer.service.exception.InsufficentFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transfers")
public class TransferController {

    @Autowired
    private BankingService bankingService;

    @ResponseBody
    @RequestMapping(consumes = "application/json", produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<Transfer> createTransfer(@RequestBody Transfer transfer) throws InsufficentFundsException, AccountDetailsInvalidException {
        return new ResponseEntity<>(bankingService.transfer(transfer), HttpStatus.CREATED);
    }

}
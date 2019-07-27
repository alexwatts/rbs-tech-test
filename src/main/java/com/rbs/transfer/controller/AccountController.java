package com.rbs.transfer.controller;

import com.rbs.transfer.model.Account;
import com.rbs.transfer.model.AccountIdentifier;
import com.rbs.transfer.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/accounts")
public class AccountController {

    @Autowired
    private BankingService bankingService;

    @ResponseBody
    @RequestMapping(consumes = "application/json", produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return new ResponseEntity<>(bankingService.createAccount(account), HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(value = "{sortCode}/{accountNumber}",produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<Account> getAccount(@PathVariable("sortCode") String sortCode, @PathVariable("accountNumber") String accountNumber) {
        AccountIdentifier accountIdentifier = new AccountIdentifier(sortCode, accountNumber);
        return new ResponseEntity<>(bankingService.getAccount(accountIdentifier), HttpStatus.OK);
    }

}
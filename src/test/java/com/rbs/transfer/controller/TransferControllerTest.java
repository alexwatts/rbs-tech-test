package com.rbs.transfer.controller;

import com.rbs.transfer.app.TestApplication;
import com.rbs.transfer.model.AccountIdentifier;
import com.rbs.transfer.model.Money;
import com.rbs.transfer.model.Transfer;
import com.rbs.transfer.service.BankingService;
import com.rbs.transfer.service.exception.AccountDetailsInvalidException;
import com.rbs.transfer.service.exception.InsufficentFundsException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {TestApplication.class})
public class TransferControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private BankingService bankingService;

    private HttpHeaders headers;

    @Before
    public void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldBeAbleToMakeTransfer() throws Exception {

        AccountIdentifier sourceAccount =  new AccountIdentifier("67-23-65", "4773267");
        AccountIdentifier destinationAccount = new AccountIdentifier("23-54-77", "234234");

        Transfer aTransfer = new Transfer(sourceAccount, destinationAccount, new Money(Currency.getInstance(Locale.UK), new BigDecimal("17.64")));

        when(bankingService.transfer(aTransfer)).thenReturn(aTransfer);

        ResponseEntity<Transfer> createdTransfer = testRestTemplate.exchange("/transfers", HttpMethod.PUT, new HttpEntity<>(aTransfer, headers), new ParameterizedTypeReference<Transfer>() {});

        assertThat(createdTransfer.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(createdTransfer.getBody(), equalTo(aTransfer));
    }

    @Test
    public void shouldNotMakeTransferWhenInsufficientFunds() throws Exception {

        AccountIdentifier sourceAccount =  new AccountIdentifier("67-23-65", "4773267");
        AccountIdentifier destinationAccount = new AccountIdentifier("23-54-77", "234234");

        Transfer aTransfer = new Transfer(sourceAccount, destinationAccount, new Money(Currency.getInstance(Locale.UK), new BigDecimal("17.64")));

        when(bankingService.transfer(aTransfer)).thenThrow(new InsufficentFundsException("Insufficient funds"));

        ResponseEntity<InsufficentFundsException> exchange = testRestTemplate.exchange("/transfers", HttpMethod.PUT, new HttpEntity<>(aTransfer, headers), InsufficentFundsException.class);

        assertThat(exchange.getStatusCode(), equalTo(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(exchange.getBody().getMessage(), equalTo("Insufficient funds"));
    }

    @Test
    public void shouldNotMakeTransferWhenAccountDetailsInvalid() throws Exception {

        AccountIdentifier sourceAccount =  new AccountIdentifier("67-23-65", "4773267");
        AccountIdentifier destinationAccount = new AccountIdentifier("23-54-77", "234234");

        Transfer aTransfer = new Transfer(sourceAccount, destinationAccount, new Money(Currency.getInstance(Locale.UK), new BigDecimal("17.64")));

        when(bankingService.transfer(aTransfer)).thenThrow(new AccountDetailsInvalidException("This account was not found. 67-23-65/4773267"));

        ResponseEntity<AccountDetailsInvalidException> exchange = testRestTemplate.exchange("/transfers", HttpMethod.PUT, new HttpEntity<>(aTransfer, headers), AccountDetailsInvalidException.class);

        assertThat(exchange.getStatusCode(), equalTo(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(exchange.getBody().getMessage(), equalTo("This account was not found. 67-23-65/4773267"));
    }

}

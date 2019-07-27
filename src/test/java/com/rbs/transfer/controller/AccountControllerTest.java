package com.rbs.transfer.controller;

import com.rbs.transfer.app.TestApplication;
import com.rbs.transfer.model.Account;
import com.rbs.transfer.model.AccountIdentifier;
import com.rbs.transfer.model.Money;
import com.rbs.transfer.service.BankingService;
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
public class AccountControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private BankingService bankingService;

    private HttpHeaders headers;

    @Before
    public void setUp() {
        headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldBeAbleToCreateAnAccount() {
        Account anAccount = new Account(new AccountIdentifier("67-23-65", "4773267"), new Money(Currency.getInstance(Locale.UK), BigDecimal.ZERO));

        when(bankingService.createAccount(anAccount)).thenReturn(anAccount);

        ResponseEntity<Account> createdAccount = testRestTemplate.exchange("/accounts", HttpMethod.PUT, new HttpEntity<>(anAccount, headers), new ParameterizedTypeReference<Account>() {});

        assertThat(createdAccount.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(createdAccount.getBody(), equalTo(anAccount));
    }

    @Test
    public void shouldBeAbleToGetAccount() {

        AccountIdentifier accountIdentifier = new AccountIdentifier("67-23-65", "4773267");
        Account anAccount = new Account(accountIdentifier, new Money(Currency.getInstance(Locale.UK), BigDecimal.ZERO));

        when(bankingService.getAccount(accountIdentifier)).thenReturn(anAccount);

        ResponseEntity<Account> retrievedAccount = testRestTemplate.exchange(String.format("/accounts/%s/%s", accountIdentifier.getSortCode(), accountIdentifier.getAccountNumber()), HttpMethod.GET, null, new ParameterizedTypeReference<Account>() {});

        assertThat(retrievedAccount.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(retrievedAccount.getBody(), equalTo(anAccount));
    }

}

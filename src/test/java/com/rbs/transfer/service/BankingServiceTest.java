package com.rbs.transfer.service;

import com.rbs.transfer.model.Account;
import com.rbs.transfer.model.AccountIdentifier;
import com.rbs.transfer.model.Money;
import com.rbs.transfer.model.Transfer;
import com.rbs.transfer.service.exception.AccountDetailsInvalidException;
import com.rbs.transfer.service.exception.InsufficentFundsException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BankingServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private BankingService subject = new BankingService();

    @Test
    public void shouldBeAbleToCreateAccount() {
        Account testAccount = new Account(new AccountIdentifier("create", "account"), givenMoney("00.20"));
        assertThat(subject.createAccount(testAccount), equalTo(testAccount));
    }

    @Test
    public void shouldBeAbleToGetAccount() {
        Account account = givenAccount("1", givenMoney("00.10"));
        assertThat(subject.getAccount(account.getAccountIdentifier()), equalTo(account));
    }

    @Test
    public void shouldBeAbleToMakeTransfer() throws Exception {
        Account sourceAccount = givenAccount("1", givenMoney("100.00"));
        Account destinationAccount = givenAccount("2", givenMoney("00.00"));

        subject.transfer(new Transfer(sourceAccount.getAccountIdentifier(), destinationAccount.getAccountIdentifier(), givenMoney("00.20")));
        Account retrievedSourceAccount = subject.getAccount(sourceAccount.getAccountIdentifier());
        Account retrievedDestinationAccount = subject.getAccount(destinationAccount.getAccountIdentifier());

        assertThat(retrievedSourceAccount.getBalance(), equalTo(givenMoney("99.80")));
        assertThat(retrievedDestinationAccount.getBalance(), equalTo(givenMoney("00.20")));
    }

    @Test
    public void shouldBeAbleToMakeTransferWithInvalidScales() throws Exception {
        Account sourceAccount = givenAccount("1", givenMoney("100.00234342123123124"));
        Account destinationAccount = givenAccount("2", givenMoney("00.0000000001"));

        subject.transfer(new Transfer(sourceAccount.getAccountIdentifier(), destinationAccount.getAccountIdentifier(), givenMoney("00.20042340234123")));
        Account retrievedSourceAccount = subject.getAccount(sourceAccount.getAccountIdentifier());
        Account retrievedDestinationAccount = subject.getAccount(destinationAccount.getAccountIdentifier());

        assertThat(retrievedSourceAccount.getBalance(), equalTo(givenMoney("99.80")));
        assertThat(retrievedDestinationAccount.getBalance(), equalTo(givenMoney("00.20")));
    }

    @Test
    public void shouldBeAbleToMakeTransferWhenFundsExactlySameAsTransferAmount() throws Exception {
        Account sourceAccount = givenAccount("1", givenMoney("100.00"));
        Account destinationAccount = givenAccount("2", givenMoney("00.00"));

        subject.transfer(new Transfer(sourceAccount.getAccountIdentifier(), destinationAccount.getAccountIdentifier(), givenMoney("100.00")));
        Account retrievedSourceAccount = subject.getAccount(sourceAccount.getAccountIdentifier());
        Account retrievedDestinationAccount = subject.getAccount(destinationAccount.getAccountIdentifier());

        assertThat(retrievedSourceAccount.getBalance(), equalTo(givenMoney("00.00")));
        assertThat(retrievedDestinationAccount.getBalance(), equalTo(givenMoney("100.00")));
    }

    @Test
    public void shouldNotBeAbleToMakeTransferWhenInsufficientFunds() throws Exception {
        exception.expect(InsufficentFundsException.class);
        exception.expectMessage("Insufficient Funds");

        Account sourceAccount = givenAccount("1", givenMoney("00.00"));
        Account destinationAccount = givenAccount("2", givenMoney("00.00"));

        subject.transfer(new Transfer(sourceAccount.getAccountIdentifier(), destinationAccount.getAccountIdentifier(), givenMoney("00.20")));
    }

    @Test
    public void shouldNotBeAbleToMakeTransferWhenSourceAccountDoesNotExist() throws Exception {
        exception.expect(AccountDetailsInvalidException.class);
        exception.expectMessage("This account was not found. nonsense/account");

        Account destinationAccount = givenAccount("2", givenMoney("00.00"));

        subject.transfer(new Transfer(new AccountIdentifier("nonsense", "account"), destinationAccount.getAccountIdentifier(), givenMoney("00.20")));
    }

    @Test
    public void shouldNotBeAbleToMakeTransferWhenDestinationAccountDoesNotExist() throws Exception {
        exception.expect(AccountDetailsInvalidException.class);
        exception.expectMessage("This account was not found. nonsense/account");

        Account sourceAccount = givenAccount("1", givenMoney("00.00"));

        subject.transfer(new Transfer(sourceAccount.getAccountIdentifier(), new AccountIdentifier("nonsense", "account"), givenMoney("00.20")));
    }

    private Money givenMoney(String money) {
        return new Money(Currency.getInstance(Locale.UK), new BigDecimal(money));
    }

    private Account givenAccount(String accountId, Money money) {
        Account account = new Account(new AccountIdentifier("test", accountId), money);
        subject.createAccount(account);
        return account;
    }

}

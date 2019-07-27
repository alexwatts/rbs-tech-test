package com.rbs.transfer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Account {

    private final AccountIdentifier accountIdentifier;
    private final Money balance;

    @JsonCreator
    public Account(
            @JsonProperty("accountIdentifier") AccountIdentifier accountIdentifier,
            @JsonProperty("balance") Money balance) {
        this.accountIdentifier = accountIdentifier;
        this.balance = balance;
    }

    public AccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public Money getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equal(accountIdentifier, account.accountIdentifier) &&
                Objects.equal(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountIdentifier, balance);
    }
}

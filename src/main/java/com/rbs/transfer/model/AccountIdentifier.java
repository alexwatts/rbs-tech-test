package com.rbs.transfer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class AccountIdentifier {

    private final String sortCode;
    private final String accountNumber;

    @JsonCreator
    public AccountIdentifier(@JsonProperty("sortCode") String sortCode, @JsonProperty("accountNumber")String accountNumber) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountIdentifier that = (AccountIdentifier) o;
        return Objects.equal(sortCode, that.sortCode) &&
                Objects.equal(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sortCode, accountNumber);
    }
}

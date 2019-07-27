package com.rbs.transfer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Transfer {

    private final AccountIdentifier sourceAccountIdentifier;
    private final AccountIdentifier destinationAccountIdentifier;
    private final Money transferValue;

    @JsonCreator
    public Transfer(
            @JsonProperty("sourceAccountIdentifier") AccountIdentifier sourceAccountIdentifier,
            @JsonProperty("destinationAccountIdentifier") AccountIdentifier destinationAccountIdentifier,
            @JsonProperty("transferValue") Money transferValue) {
        this.sourceAccountIdentifier = sourceAccountIdentifier;
        this.destinationAccountIdentifier = destinationAccountIdentifier;
        this.transferValue = transferValue;
    }

    public AccountIdentifier getSourceAccountIdentifier() {
        return sourceAccountIdentifier;
    }

    public AccountIdentifier getDestinationAccountIdentifier() {
        return destinationAccountIdentifier;
    }

    public Money getTransferValue() {
        return transferValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equal(sourceAccountIdentifier, transfer.sourceAccountIdentifier) &&
                Objects.equal(destinationAccountIdentifier, transfer.destinationAccountIdentifier) &&
                Objects.equal(transferValue, transfer.transferValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sourceAccountIdentifier, destinationAccountIdentifier, transferValue);
    }

}

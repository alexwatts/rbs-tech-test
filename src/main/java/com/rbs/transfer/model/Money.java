package com.rbs.transfer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Currency;

public class Money {

    private final Currency currency;
    private final BigDecimal value;

    @JsonCreator
    public Money(@JsonProperty("currency") Currency currency, @JsonProperty("value") BigDecimal value) {
        this.currency = currency;
        this.value = value;
    }

    public Money add(Money money) {
        return new Money(money.getCurrency(), getValue().add(money.getValue()));
    }

    public Money subtract(Money money) {
        return new Money(money.getCurrency(), getValue().subtract(money.getValue()));
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getValue() {
        return this.value.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equal(currency, money.currency) &&
                Objects.equal(getValue(), money.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currency, getValue());
    }

    @Override
    public String toString() {
        return "Money{" +
                "currency=" + currency +
                ", value=" + value +
                '}';
    }

}

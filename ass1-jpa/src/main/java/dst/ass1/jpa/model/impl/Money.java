package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Money implements IMoney {

    private String currency;

    private BigDecimal currencyValue;

    public Money() {}

    public Money(String currency, BigDecimal currencyValue) {
        this.currency = currency;
        this.currencyValue = currencyValue;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public BigDecimal getCurrencyValue() {
        return currencyValue;
    }

    @Override
    public void setCurrencyValue(BigDecimal currencyValue) {
        this.currencyValue = currencyValue;
    }
}

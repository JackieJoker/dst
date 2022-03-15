package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Money;

import java.math.BigDecimal;

public class RecentSpending {
    private IRider iRider;
    private String currency;
    private IMoney iMoney;

    public RecentSpending(IRider iRider, String currency, BigDecimal currencyValue) {
        this.iRider = iRider;
        this.currency = currency;
        iMoney = new Money(currency, currencyValue);
    }

    public IRider getiRider() {
        return iRider;
    }

    public String getCurrency() {
        return currency;
    }

    public IMoney getiMoney() {
        return iMoney;
    }

    @Override
    public String toString() {
        return "riderName: " + iRider.getName() +
                " | currency: " + currency +
                " | moneyValue: " + iMoney.getCurrencyValue();
    }
}

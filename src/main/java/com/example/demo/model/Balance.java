package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.demo.utils.CommonUtils.*;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Balance implements Cloneable {

    public Balance(Double startBalance) {
        this.startBalance = startBalance;
        this.currentBalance = startBalance;
    }

    private Double startBalance = 0d;

    private Double currentBalance = 0d;

    private Double profit = 0d;

    @Override
    public Balance clone() throws CloneNotSupportedException {
        return (Balance) super.clone();
    }

    public Balance goodBet(long teamSize, double coefficient) {
        currentBalance += startBalance * Math.pow(coefficient, teamSize);
        profit = currentBalance - startBalance;
        makeRoundingWithBigDecimal();
        return this;
    }

    public Balance poorBet() {
        currentBalance -= startBalance;
        profit = currentBalance - startBalance;
        makeRoundingWithBigDecimal();
        return this;
    }

    private void makeRoundingWithBigDecimal() {
        currentBalance = roundingWithBigDecimal(currentBalance);
        profit = roundingWithBigDecimal(profit);
    }
}

package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.demo.utils.CommonUtils.*;
import static java.lang.Math.pow;

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

    public Balance(Double startBalance, int strick) {
        this.startBalance = startBalance;
        this.currentBalance = startBalance;
        this.strick = strick;
    }

    private Double startBalance = 0d;

    private Double currentBalance = 0d;

    private Double profit = 0d;

    private int strick;

    private int currentStrick;

    @Override
    public Balance clone() throws CloneNotSupportedException {
        return (Balance) super.clone();
    }

    public Balance goodBet(long teamSize, double coefficient) {
        if (strick < 1) {
            currentBalance += startBalance * pow(coefficient, teamSize) - startBalance;
        } else {
            incrementCurrentStrick();
            currentBalance += startBalance * pow(pow(coefficient, teamSize), currentStrick) - (startBalance * pow(pow(coefficient, teamSize), currentStrick - 1));
        }
        profit = currentBalance - startBalance;
        makeRoundingWithBigDecimal();
        return this;
    }

    public Balance poorBet(long teamSize, double coefficient) {
        if (strick < 1) {
            currentBalance -= startBalance;
        } else {
            decrementCurrentStrick();
            currentBalance -= startBalance * pow(pow(coefficient, teamSize), currentStrick);
            currentStrick = 0;
        }
        profit = currentBalance - startBalance;
        makeRoundingWithBigDecimal();
        return this;
    }

    private void incrementCurrentStrick() {
        currentStrick = ++currentStrick > strick ? 1 : currentStrick;
    }

    private void decrementCurrentStrick() {
        currentStrick = currentStrick == strick ? 0 : currentStrick;
    }

    private void makeRoundingWithBigDecimal() {
        currentBalance = roundingWithBigDecimal(currentBalance);
        profit = roundingWithBigDecimal(profit);
    }
}

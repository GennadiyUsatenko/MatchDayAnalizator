package com.example.demo.model;

import com.example.demo.model.enums.StrategyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Balance {

    private Double balance = 0d;

    private StrategyType strategyType = StrategyType.STANDART;
}

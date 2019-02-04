package com.example.demo.controller.bean;

import com.example.demo.model.Balance;
import com.example.demo.model.Championship;
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
public class ResponseBean {

    private Championship championship;

    private Balance balance;
}

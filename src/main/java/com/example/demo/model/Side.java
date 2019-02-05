package com.example.demo.model;

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
public class Side {

    public Side(Team team) {
        this.team = team;
    }

    public Side(Team team, int goals) {
        this.team = team;
        this.goals = goals;
    }

    private Team team;

    private int goals;
}

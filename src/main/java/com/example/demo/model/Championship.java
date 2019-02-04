package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Championship {

    public Championship(String name, int seasonNumber) {
        this.name = name;
        this.seasonNumber = seasonNumber;
    }

    private String name;

    private List<Team> teams;

    private List<MatchDay> matchDays;

    private int seasonNumber;

    public Map<Team, TableStatistics> getTable() {
        return null;
    }
}

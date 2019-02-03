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
public class MatchDay {

    private List<Match> matches;

    private int matchDayNumber;

    private boolean isInFuture;

    public Map<Team, TableStatistics> getResult() {

    }
}

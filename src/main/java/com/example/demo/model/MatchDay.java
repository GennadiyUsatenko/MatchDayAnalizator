package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class MatchDay {

    public MatchDay(List<Match> matches) {
        this.matches = matches;
    }

    public MatchDay(List<Match> matches, int matchDayNumber) {
        this.matches = matches;
        this.matchDayNumber = matchDayNumber;
    }

    public MatchDay(List<Match> matches, int matchDayNumber, boolean isInFuture) {
        this.matches = matches;
        this.matchDayNumber = matchDayNumber;
        this.isInFuture = isInFuture;
    }

    private List<Match> matches;

    private int matchDayNumber;

    private boolean isInFuture;

    public MatchDayResult getMatchDayResult() {
        return new MatchDayResult(this, matches.stream().map(Match::getTableStatList).flatMap(List::stream).collect(Collectors.toList()));
    }

}

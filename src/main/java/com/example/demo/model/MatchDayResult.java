package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class MatchDayResult {

    public MatchDayResult(MatchDay matchDay) {
        this.matchDay = matchDay;
    }

    public MatchDayResult(MatchDay matchDay, List<TableStatistics> tableStatistics) {
        this.matchDay = matchDay;
        this.tableStatistics = tableStatistics;
    }

    private MatchDay matchDay;

    private List<TableStatistics> tableStatistics;

    public TableStatistics findTableStatByTeam(Team team) {
        return tableStatistics.stream().filter(t -> t.getTeam().equals(team)).findFirst().orElse(null);
    }

    public MatchDayResult add(MatchDayResult matchDayResult) {
        tableStatistics.forEach(t -> t.add(matchDayResult.findTableStatByTeam(t.getTeam())));
        return this;
    }

    public MatchDayResult addAll(List<MatchDayResult> matchDayResults) {
        matchDayResults.forEach(this::add);
        tableStatistics = tableStatistics.stream().sorted(Comparator.comparingInt(t -> ((TableStatistics) t).getPoints()).reversed()).collect(Collectors.toList());
        return this;
    }

}

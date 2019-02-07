package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

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

    private List<List<TableStatistics>> tableStatistics;

    public Championship addToMatchDays(List<MatchDay> matchDays) {
        this.matchDays = Optional.ofNullable(this.matchDays).orElse(new ArrayList<>());
        if (this.matchDays.stream().anyMatch(matchDay -> matchDay.getMatchDayNumber() == matchDays.get(0).getMatchDayNumber())) {
            this.matchDays.stream().filter(matchDay -> matchDay.getMatchDayNumber() == matchDays.get(0).getMatchDayNumber()).findFirst().get().getMatches().addAll(matchDays.get(0).getMatches());
            matchDays.remove(0);
        }
        this.matchDays.addAll(matchDays);
        this.matchDays = this.matchDays.stream().sorted(Comparator.comparingInt(m -> m.getMatchDayNumber())).collect(Collectors.toList());
        return this;
    }

    public Team findTeamByName(String teamName) {
        return teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null);
    }

    public Championship setTableStatList() {
        List<List<TableStatistics>> lists = new ArrayList<>(matchDays.size());
        for (int matchDayNamber = 1; matchDayNamber <= matchDays.size(); matchDayNamber++) {
            lists.add(getTableStatList(matchDayNamber));
        }
        tableStatistics = lists;
        return this;
    }

    public Championship setTableStatList(int matchDayLimit) {
        List<List<TableStatistics>> lists = new ArrayList<>(matchDayLimit);
        for (int matchDayNamber = 1; matchDayNamber <= matchDayLimit; matchDayNamber++) {
            lists.add(getTableStatList(matchDayNamber));
        }
        tableStatistics = lists;
        return this;
    }

    public List<TableStatistics> getTableStatList() {
        List<MatchDayResult> matchDayResults = matchDays.stream().map(MatchDay::getMatchDayResult).collect(Collectors.toList());
        return matchDayResults.get(0).addAll(matchDayResults.stream().skip(1).collect(Collectors.toList())).getTableStatistics();
    }

    public List<TableStatistics> getTableStatList(int matchDayLimit) {
        List<MatchDayResult> matchDayResults = matchDays.stream().limit(matchDayLimit).map(MatchDay::getMatchDayResult).collect(Collectors.toList());
        return matchDayResults.get(0).addAll(matchDayResults.stream().skip(1).collect(Collectors.toList())).getTableStatistics();
    }
}

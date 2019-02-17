package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.example.demo.utils.CommonUtils.*;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Season {

    public Season(String name, int seasonNumber) {
        this.name = name;
        this.seasonNumber = seasonNumber;
    }

    public Season(String name, int seasonNumber, int sortsRuSeasonNumber) {
        this.name = name;
        this.seasonNumber = seasonNumber;
        this.sortsRuSeasonNumber = sortsRuSeasonNumber;
    }

    private String name;

    private List<Team> teams;

    private List<MatchDay> matchDays;

    private int seasonNumber;

    private int sortsRuSeasonNumber;

    private List<List<TableStatistics>> tableStatistics;

    private Double averageGoalForHomeSide;

    private Double averageGoalForGuestSide;

    public void setAverageGoals() {
        averageGoalForHomeSide = roundingWithBigDecimal(matchDays.stream().filter(matchDay -> !matchDay.isInFuture()).mapToDouble(MatchDay::getAverageGoalForHomeSide).summaryStatistics().getAverage());
        averageGoalForGuestSide = roundingWithBigDecimal(matchDays.stream().filter(matchDay -> !matchDay.isInFuture()).mapToDouble(MatchDay::getAverageGoalForGuestSide).summaryStatistics().getAverage());
    }

    public boolean isValid() {
        return teams.size() != 0 && matchDays.size() == (teams.size() - 1) * 2 && matchDays.stream().allMatch(m -> m.getMatches().size() == (double)teams.size() / 2d);
    }

    public Season addToMatchDays(List<MatchDay> matchDays) {
        this.matchDays = Optional.ofNullable(this.matchDays).orElse(new ArrayList<>());

        List<Integer> matchDaysNumbers = this.matchDays.stream().map(MatchDay::getMatchDayNumber).collect(Collectors.toList());

        List<MatchDay> matchDaysAlreadyIn = matchDays.stream().filter(matchDay -> matchDaysNumbers.contains(matchDay.getMatchDayNumber())).collect(Collectors.toList());
        List<MatchDay> matchDaysNotInYet = matchDays.stream().filter(matchDay -> !matchDaysNumbers.contains(matchDay.getMatchDayNumber())).collect(Collectors.toList());

        matchDaysAlreadyIn.forEach(matchDay -> {
            this.matchDays.stream().filter(subMatchDay -> subMatchDay.getMatchDayNumber() == matchDay.getMatchDayNumber()).findFirst().get().getMatches().addAll(matchDay.getMatches());
        });

        this.matchDays.addAll(matchDaysNotInYet);
        this.matchDays = this.matchDays.stream().sorted(Comparator.comparingInt(MatchDay::getMatchDayNumber)).collect(Collectors.toList());
        return this;
    }

    public Team findTeamByName(String teamName) {
        return teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null);
    }

    public Season setTableStatList() {
        List<List<TableStatistics>> lists = new ArrayList<>(matchDays.size());
        for (int matchDayNamber = 1; matchDayNamber <= matchDays.size(); matchDayNamber++) {
            lists.add(getTableStatList(matchDayNamber, Comparator.comparingInt(t -> ((TableStatistics) t).getPoints()).reversed()));
        }
        tableStatistics = lists;
        setAverageGoals();
        return this;
    }

    public Season setTableStatList(int matchDayLimit) {
        List<List<TableStatistics>> lists = new ArrayList<>(matchDayLimit);
        for (int matchDayNamber = 1; matchDayNamber <= matchDayLimit; matchDayNamber++) {
            lists.add(getTableStatList(matchDayNamber, Comparator.comparingInt(t -> ((TableStatistics) t).getPoints()).reversed()));
        }
        tableStatistics = lists;
        setAverageGoals();
        return this;
    }

    public List<TableStatistics> getTableStatList(Comparator<Object> comparator) {
        List<MatchDayResult> matchDayResults = matchDays.stream().map(MatchDay::getMatchDayResult).collect(Collectors.toList());
        return matchDayResults.get(0).addAll(matchDayResults.stream().skip(1).collect(Collectors.toList()), comparator).getTableStatistics();
    }

    public List<TableStatistics> getTableStatList(int matchDayLimit, Comparator<Object> comparator) {
        if (comparator == null) comparator = Comparator.comparingInt(t -> ((TableStatistics) t).getPoints()).reversed();
        List<MatchDayResult> matchDayResults = matchDays.stream().limit(matchDayLimit).map(MatchDay::getMatchDayResult).collect(Collectors.toList());
        return matchDayResults.get(0).addAll(matchDayResults.stream().skip(1).collect(Collectors.toList()), comparator).getTableStatistics();
    }

}

package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.enums.BettingStrategyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.example.demo.model.enums.BettingStrategyType.CLASSIC_WITH_3_TEAMS;

@Component
@Slf4j
public class BettingStrategyService {

    private static final Integer PREPARE_STATISTICS_MATCH_DAY_LIMIT = 3;
    private static final Double DEFAULT_COEFFICIENT = 1.65;
    private static final Double MINIMAL_POSSIBLE_PERCENT_OF_SCORED_FOR_3_TEAMS = 0.7;
    private static final Double MINIMAL_POSSIBLE_PERCENT_OF_SCORED_FOR_POOL_FOR_3_TEAMS = 0.4;
    private static final BettingStrategyType DEFAULT_STATEGY_TYPE = CLASSIC_WITH_3_TEAMS;

    public List<Balance> prepareBalance(Season season, BettingStrategyType strategyType) throws CloneNotSupportedException {
        List<Balance> balances = new ArrayList<>(season.getMatchDays().size());
        balances.add(new Balance(10d));
        prepareStatistics(season, strategyType);

        for (MatchDay matchDay: season.getMatchDays().stream().skip(1).collect(Collectors.toList())) {
            if (matchDay.getMatches().stream().anyMatch(Match::isPotentiallyScored)) {
                if (matchDay.getMatches().stream().filter(Match::isPotentiallyScored).allMatch(Match::isScored)) {
                    balances.add(balances.get(balances.size() - 1).clone().goodBet(matchDay.getMatches().stream().filter(Match::isPotentiallyScored).count(), DEFAULT_COEFFICIENT));
                } else {
                    balances.add(balances.get(balances.size() - 1).clone().poorBet());
                }
            } else {
                balances.add(balances.get(balances.size() - 1).clone());
            }
        }
        return balances;
    }

    public Season prepareStatistics(Season season) {
        for (AtomicInteger matchDayNumber = new AtomicInteger(PREPARE_STATISTICS_MATCH_DAY_LIMIT); matchDayNumber.get() < season.getMatchDays().size(); matchDayNumber.incrementAndGet()) {
            Map<Team, TableStatistics> tableStatisticsMap = season.getTableStatList(matchDayNumber.get(), null).stream().collect(Collectors.toMap(TableStatistics::getTeam, t -> t));
            season.getMatchDays().get(matchDayNumber.get()).getMatches().forEach(match -> {
                match.setPossibleScoredPercent(tableStatisticsMap.get(match.getHomeTeam()).getPossibleScoredPercent() * tableStatisticsMap.get(match.getGuestTeam()).getPossibleScoredPercent());
            });
        }
        return season;
    }

    // bad version
    private void prepareStatistics(Season season, BettingStrategyType strategyType) {
        switch (strategyType) {
            case CLASSIC_WITH_3_TEAMS:
                for (AtomicInteger matchDayNumber = new AtomicInteger(PREPARE_STATISTICS_MATCH_DAY_LIMIT); matchDayNumber.get() < season.getMatchDays().size(); matchDayNumber.incrementAndGet()) {
                    List<Team> potentiallyScoredTeams = season.getTableStatList(
                            matchDayNumber.get(), Comparator.comparingInt(t -> ((TableStatistics) t).getMatchesScored()).reversed()
                                    .thenComparing(Comparator.comparingInt(t -> Integer.min(((TableStatistics) t).getGoalsFor(), ((TableStatistics) t).getGoalsAgainst())).reversed())
                    ).stream().filter(t -> ((double)t.getMatchesScored() / (double)t.getMatchesPlayed()) > MINIMAL_POSSIBLE_PERCENT_OF_SCORED_FOR_3_TEAMS).limit(3)
                            .map(TableStatistics::getTeam).collect(Collectors.toList());

                    List<Team> potentiallyScoredTeamPool = season.getTableStatList(
                            matchDayNumber.get(), Comparator.comparingInt(t -> ((TableStatistics) t).getMatchesScored()).reversed()
                                    .thenComparing(Comparator.comparingInt(t -> Integer.min(((TableStatistics) t).getGoalsFor(), ((TableStatistics) t).getGoalsAgainst())).reversed())
                    ).stream().filter(t -> ((double)t.getMatchesScored() / (double)t.getMatchesPlayed()) > MINIMAL_POSSIBLE_PERCENT_OF_SCORED_FOR_POOL_FOR_3_TEAMS).skip(3)
                            .map(TableStatistics::getTeam).collect(Collectors.toList());

                    season.getMatchDays().get(matchDayNumber.get()).getMatches().forEach(match -> {
                        if (potentiallyScoredTeams.contains(match.getHomeSide().getTeam()) || potentiallyScoredTeams.contains(match.getGuestSide().getTeam())) {
                            if (potentiallyScoredTeamPool.contains(match.getHomeSide().getTeam()) || potentiallyScoredTeamPool.contains(match.getGuestSide().getTeam())) {
                                match.setPotentiallyScored(true);
                                season.getTableStatistics().get(matchDayNumber.get() - 1).forEach(t -> {
                                    if (t.getTeam().equals(match.getHomeSide().getTeam()) || t.getTeam().equals(match.getGuestSide().getTeam())) {
                                        t.setPotentiallyScored(potentiallyScoredTeams.contains(t.getTeam()));
                                        t.setPotentiallyScoredInPool(potentiallyScoredTeamPool.contains(t.getTeam()));
                                    }
                                });
                            }
                        }
                    });
                }
                break;
        }
    }
}

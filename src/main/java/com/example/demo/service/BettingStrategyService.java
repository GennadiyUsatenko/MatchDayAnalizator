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
    private static final Double DEFAULT_COEFFICIENT = 1.6;
    private static final BettingStrategyType DEFAULT_STATEGY_TYPE = CLASSIC_WITH_3_TEAMS;

    public List<Balance> prepareBalance(Season season, BettingStrategyType strategyType) throws CloneNotSupportedException {
        List<Balance> balances = new ArrayList<>(season.getMatchDays().size());
        balances.add(new Balance(10d));
        prepareStatistics(season, strategyType);

        for (MatchDay matchDay: season.getMatchDays().stream().skip(1).collect(Collectors.toList())) {
            if (matchDay.getMatches().stream().anyMatch(Match::isPotentiallyScored) && matchDay.getMatches().stream().filter(Match::isPotentiallyScored).noneMatch(Match::isInFuture)) {
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

    public Season prepareStatistics(Season season, BettingStrategyType strategyType) {
        int teamStrategy = 2;
        switch (strategyType) {
            case CLASSIC_WITH_2_TEAMS:
                teamStrategy = 2;
                break;
            case CLASSIC_WITH_3_TEAMS:
                teamStrategy = 3;
                break;
            case CLASSIC_WITH_4_TEAMS:
                teamStrategy = 4;
                break;
            case CLASSIC_WITH_5_TEAMS:
                teamStrategy = 5;
                break;
        }

        for (AtomicInteger matchDayNumber = new AtomicInteger(PREPARE_STATISTICS_MATCH_DAY_LIMIT); matchDayNumber.get() < season.getMatchDays().size(); matchDayNumber.incrementAndGet()) {
            Map<Team, TableStatistics> tableStatisticsMap = season.getTableStatList(matchDayNumber.get(), null).stream().collect(Collectors.toMap(TableStatistics::getTeam, t -> t));
            season.getMatchDays().get(matchDayNumber.get()).getMatches().forEach(match -> {
                match.setPossibleScoredPercent(tableStatisticsMap.get(match.getHomeTeam()).getPossibleDoubleScoredPercent() * tableStatisticsMap.get(match.getGuestTeam()).getPossibleDoubleScoredPercent());
            });
            season.getMatchDays().get(matchDayNumber.get()).getMatches().stream().sorted(Comparator.comparingDouble(Match::getPossibleScoredPercent).reversed()).limit(teamStrategy).forEach(match -> {
                match.setPotentiallyScored(true);
            });
        }
        return season;
    }
}

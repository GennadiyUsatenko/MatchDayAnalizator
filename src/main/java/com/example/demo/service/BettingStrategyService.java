package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.enums.BettingStrategyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.model.enums.BettingStrategyType.CLASSIC_WITH_3_TEAMS;

@Component
@Slf4j
public class BettingStrategyService {

    private static final Integer PREPARE_STATISTICS_MATCH_DAY_LIMIT = 3;
    private static final Double DEFAULT_COEFFICIENT = 1.65;
    private static final BettingStrategyType DEFAULT_STATEGY_TYPE = CLASSIC_WITH_3_TEAMS;

    private void prepareStatistics(Season season, BettingStrategyType strategyType) {
        switch (strategyType) {
            case CLASSIC_WITH_3_TEAMS:
                for (int matchDayNumber = PREPARE_STATISTICS_MATCH_DAY_LIMIT; matchDayNumber < season.getMatchDays().size(); matchDayNumber++) {
                    List<Team> potentiallyScoredTeams = season.getTableStatList(matchDayNumber, Comparator.comparingInt(t -> ((TableStatistics) t).getMatchesScored()).reversed())
                            .stream().limit(3).map(TableStatistics::getTeam).collect(Collectors.toList());

                    season.getMatchDays().get(matchDayNumber).getMatches().forEach(match -> {
                        if (potentiallyScoredTeams.contains(match.getHomeSide().getTeam()) || potentiallyScoredTeams.contains(match.getGuestSide().getTeam())) {
                            match.setPotentiallyScored(true);
                        }
                    });
                }
                break;
        }
    }

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
}

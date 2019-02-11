package com.example.demo.model;

import com.example.demo.model.enums.MatchResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.example.demo.model.enums.MatchResult.WIN;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class TableStatistics {

    public TableStatistics(Match match, boolean isForHomeSide) {
        if (!match.isInFuture()) {
            switch (MatchResult.getResult(match, isForHomeSide)) {
                case WIN:
                    matchesWon = 1;
                    matchesScored = match.isScored() ? 1 : 0;
                    points = 3;
                    goalsFor = isForHomeSide ? match.getHomeSide().getGoals() : match.getGuestSide().getGoals();
                    goalsAgainst = isForHomeSide ? match.getGuestSide().getGoals() : match.getHomeSide().getGoals();
                    break;
                case DRAW:
                    matchesDrawn = 1;
                    matchesScored = match.isScored() ? 1 : 0;
                    points = 1;
                    goalsFor = isForHomeSide ? match.getHomeSide().getGoals() : match.getGuestSide().getGoals();
                    goalsAgainst = isForHomeSide ? match.getGuestSide().getGoals() : match.getHomeSide().getGoals();
                    break;
                case LOSE:
                    matchesLost = 1;
                    matchesScored = match.isScored() ? 1 : 0;
                    goalsFor = isForHomeSide ? match.getHomeSide().getGoals() : match.getGuestSide().getGoals();
                    goalsAgainst = isForHomeSide ? match.getGuestSide().getGoals() : match.getHomeSide().getGoals();
                    break;
            }
            matchesPlayed = 1;
            goalsDifference = goalsFor - goalsAgainst;
        }
        team = isForHomeSide ? match.getHomeSide().getTeam() : match.getGuestSide().getTeam();
    }

    private Team team;

    private int matchesPlayed;

    private int matchesWon;

    private int matchesDrawn;

    private int matchesLost;

    private int matchesScored;

    private int points;

    private int goalsFor;

    private int goalsAgainst;

    private int goalsDifference;

    private boolean isPotentiallyScored;

    private boolean isPotentiallyScoredInPool;

    public TableStatistics add(TableStatistics tableStatistics) {
        this.matchesPlayed += tableStatistics.getMatchesPlayed();
        this.matchesWon += tableStatistics.getMatchesWon();
        this.matchesDrawn += tableStatistics.getMatchesDrawn();
        this.matchesLost += tableStatistics.getMatchesLost();
        this.matchesScored += tableStatistics.getMatchesScored();
        this.points += tableStatistics.getPoints();
        this.goalsFor += tableStatistics.getGoalsFor();
        this.goalsAgainst += tableStatistics.getGoalsAgainst();
        this.goalsDifference += tableStatistics.getGoalsDifference();
        return this;
    }
}

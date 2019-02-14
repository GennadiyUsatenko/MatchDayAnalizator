package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.*;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Match {

    public Match(String date, Side homeSide, Side guestSide) {
        this.date = date;
        this.homeSide = homeSide;
        this.guestSide = guestSide;
    }

    public Match(String date, Side homeSide, Side guestSide, boolean isScored) {
        this.date = date;
        this.homeSide = homeSide;
        this.guestSide = guestSide;
        this.isScored = isScored;
    }

    public Match(String date, Side homeSide, Side guestSide, boolean isScored, boolean isDoubleScored) {
        this.date = date;
        this.homeSide = homeSide;
        this.guestSide = guestSide;
        this.isScored = isScored;
        this.isDoubleScored = isDoubleScored;
    }

    private String date;

    private Side homeSide;

    private Side guestSide;

    private boolean isScored;

    private boolean isDoubleScored;

    private boolean isPotentiallyScored;

    private boolean isInFuture;

    private Double possibleScoredPercent;

    private Integer minimalGoalIndicator;

    public Team getHomeTeam() {
        return homeSide.getTeam();
    }

    public Team getGuestTeam() {
        return guestSide.getTeam();
    }

    public List<TableStatistics> getTableStatList() {
        return new ArrayList<>(Arrays.asList(new TableStatistics(this, true), new TableStatistics(this, false)));
    }
}

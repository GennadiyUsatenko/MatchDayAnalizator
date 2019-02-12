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

    public Match(Side homeSide, Side guestSide) {
        this.homeSide = homeSide;
        this.guestSide = guestSide;
    }

    public Match(Side homeSide, Side guestSide, boolean isScored) {
        this.homeSide = homeSide;
        this.guestSide = guestSide;
        this.isScored = isScored;
    }

    private Side homeSide;

    private Side guestSide;

    private boolean isScored;

    private boolean isPotentiallyScored;

    private boolean isInFuture;

    private Double possibleScoredPercent;

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

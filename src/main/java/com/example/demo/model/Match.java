package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

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

    public Map<Team, TableStatistics> getResult() {
        Map<Team, TableStatistics> result = new HashMap<Team, TableStatistics>(2);
        result.put(homeSide.getTeam(), new TableStatistics());
        result.put(guestSide.getTeam(), new TableStatistics());

        if (isInFuture) {
            return result;
        } else {
            result.put(homeSide.getTeam(), new TableStatistics(this, true));
            result.put(guestSide.getTeam(), new TableStatistics(this, false));
            return result;
        }
    }
}

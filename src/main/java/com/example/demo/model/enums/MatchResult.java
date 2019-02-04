package com.example.demo.model.enums;

import com.example.demo.model.Match;

public enum MatchResult {
    WIN, DRAW, LOSE;

    public static MatchResult getResult(Match match, boolean isForHomeSide) {
        int compare = Integer.compare(match.getHomeSide().getGoals(), match.getGuestSide().getGoals());
        compare = isForHomeSide ? compare : -1 * compare;
        switch (compare) {
            case 1:
                return WIN;
            case 0:
                return DRAW;
            case -1:
                return LOSE;
            default:
                return null;
        }
    }
}

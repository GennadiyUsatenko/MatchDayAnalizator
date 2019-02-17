package com.example.demo.model.enums;

public enum BettingStrategyType {
    CLASSIC_WITH_2_TEAMS, CLASSIC_WITH_3_TEAMS, CLASSIC_WITH_4_TEAMS, CLASSIC_WITH_5_TEAMS;

    public static BettingStrategyType of(Integer teamStrategy) {
        if (teamStrategy == null) return CLASSIC_WITH_2_TEAMS;
        switch (teamStrategy) {
            case 2:
                return CLASSIC_WITH_2_TEAMS;
            case 3:
                return CLASSIC_WITH_3_TEAMS;
            case 4:
                return CLASSIC_WITH_4_TEAMS;
            case 5:
                return CLASSIC_WITH_5_TEAMS;
            default:
                return CLASSIC_WITH_2_TEAMS;
        }
    }
}

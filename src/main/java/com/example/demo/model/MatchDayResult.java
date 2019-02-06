package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class MatchDayResult {

    public MatchDayResult(MatchDay matchDay) {
        this.matchDay = matchDay;
    }

    public MatchDayResult(MatchDay matchDay, List<TableStatistics> tableStatistics) {
        this.matchDay = matchDay;
        this.tableStatistics = tableStatistics;
    }

    private MatchDay matchDay;

    private List<TableStatistics> tableStatistics;

    public MatchDayResult add(MatchDayResult matchDayResult) {
        return null;
    }

}

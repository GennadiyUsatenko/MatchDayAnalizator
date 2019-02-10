package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class TableResult {

    public TableResult(Season season, List<MatchDayResult> matchDayResults) {
        this.season = season;
        this.matchDayResults = matchDayResults;
        matchDaySize = matchDayResults.size();
    }

    private Season season;

    private List<MatchDayResult> matchDayResults;

    private int matchDaySize;

    public List<TableStatistics> getTableStatList() {
        return null;
//        return matchDayResults.get(0).addAll(matchDayResults.stream().skip(1).collect(Collectors.toList())).getTableStatistics();
    }
}

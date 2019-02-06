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

    public TableResult(Championship championship, List<MatchDayResult> matchDayResults) {
        this.championship = championship;
        this.matchDayResults = matchDayResults;
        matchDaySize = matchDayResults.size();
    }

    private Championship championship;

    private List<MatchDayResult> matchDayResults;

    private int matchDaySize;

    public List<TableStatistics> getTableStatList() {
        return matchDayResults.get(0).addAll(matchDayResults.stream().skip(1).collect(Collectors.toList())).getTableStatistics();
    }
}

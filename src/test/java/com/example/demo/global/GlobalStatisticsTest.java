package com.example.demo.global;

import com.example.demo.model.Match;
import com.example.demo.model.MatchDay;
import com.example.demo.model.Season;
import com.example.demo.service.BettingStrategyService;
import com.example.demo.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MainService.class, BettingStrategyService.class})
@SpringBootTest
@Slf4j
public class GlobalStatisticsTest {

    @Autowired
    private MainService mainService;

    @Autowired
    private BettingStrategyService bettingStrategyService;

    @Test
    public void globalTest() {
        log.info("Testing %s - Run globalTest how really teams play scored", GlobalStatisticsTest.class);
        List<Season> seasons = new ArrayList<>();
        List<String> countries = Arrays.asList("epl", "la-liga", "seria-a", "bundesliga");


        for (String country : countries) {
            for (int i = 2010; i <= 2018; i++) {
                try {
//                    seasons.add(bettingStrategyService.prepareStatistics(mainService.parseSeason(country, i)));
                } catch (Exception e) {
                    System.out.println();
                }
            }
        }

        Map<Double, List<Match>> globalStatMap = seasons.stream()
                .map(Season::getMatchDays).flatMap(List::stream)
                .map(MatchDay::getMatches).flatMap(List::stream)
                .filter(m -> m.getPossibleScoredPercent() != null)
                .peek(m -> m.setPossibleScoredPercent(Integer.valueOf((int)(m.getPossibleScoredPercent() * 100)).doubleValue()))
                .sorted(Comparator.comparingDouble(Match::getPossibleScoredPercent))
                .collect(Collectors.groupingBy(Match::getPossibleScoredPercent));

        Map<Double, Double> globalStatResultMap = new HashMap<>();
        globalStatMap.forEach((k, v) -> {
            if (v.size() > 100) globalStatResultMap.put(k, ((double) v.stream().filter(Match::isScored).count() / (double) v.size()) * 100);
        });
        System.out.println();

    }
}

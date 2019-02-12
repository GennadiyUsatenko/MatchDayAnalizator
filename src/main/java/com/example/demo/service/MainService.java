package com.example.demo.service;

import com.example.demo.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class MainService {

    private static final int MAX_DOWNLOAD_RETRIES = 3;
    private static final String SPORTS_RU_TABLE_LINK = "https://www.sports.ru/%s/table/?s=%s&sub=table";
    private static final String SPORTS_RU_CALENDAR_LINK = "https://www.sports.ru/%s/calendar/?s=%s&m=%s";

    public Season parseSeason(String countryName, Integer seasonNumber) {
        Season season = convertToSeasonBySortsRu(countryName, seasonNumber);

        Document document = jsoupConnect(String.format(SPORTS_RU_TABLE_LINK, countryName, season.getSortsRuSeasonNumber()));
        if (document != null) {
            season.setTeams(document.select("table.stat-table tbody a.name").stream().map(t -> new Team(t.attr("title"))).collect(Collectors.toList()));
        }
        for (int month = 1; month <= 12; month++) {
            document = jsoupConnect(String.format(SPORTS_RU_CALENDAR_LINK, countryName, season.getSortsRuSeasonNumber(), month));
            if (document != null) {
                Iterator<Integer> matchDayNumbers = document.select("h3.titleH3").stream().filter(e -> e.text().contains("тур")).map(e -> Integer.parseInt(e.text().replaceAll("\\D", ""))).collect(Collectors.toList()).iterator();
                List<MatchDay> matchDays = document.select("table.stat-table tbody").stream().limit(document.select("h3.titleH3").stream().filter(e -> e.text().contains("тур")).count()).map(matchDay -> {
                    List<Match> matches = matchDay.select("tr").stream().map(match -> {
                        Team homeTeam = season.findTeamByName(match.select("td.owner-td").text());
                        Team guestTeam = season.findTeamByName(match.select("td.guests-td").text());
                        int[] goals = Stream.of(match.select("td.score-td").text().split(":")).filter(s -> s.matches(".*\\d+.*")).mapToInt(s -> Integer.parseInt(s.trim())).toArray();

                        if (goals.length > 0) {
                            boolean isScored = goals[0] > 0 && goals[1] > 0;
                            return new Match(new Side(homeTeam, goals[0]), new Side(guestTeam, goals[1]), isScored);
                        } else {
                            return new Match(new Side(homeTeam), new Side(guestTeam)).setInFuture(true);
                        }
                    }).collect(Collectors.toList());

                    return new MatchDay(matches, matchDayNumbers.next(), matches.stream().allMatch(Match::isInFuture));
                }).collect(Collectors.toList());

                season.addToMatchDays(matchDays);
            }
        }
        if (!season.isValid()) {
            throw new IllegalArgumentException(String.format("Season - %s/%s is not valid", season.getName(), season.getSeasonNumber()));
        }
        return season.setTableStatList();
    }

    private Document jsoupConnect(String url) {
        for (int i = 0; i < MAX_DOWNLOAD_RETRIES; i++) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url).timeout(0).get();
            } catch (IOException e) {
                log.debug("Can not take any information from url - %s ", url);
            }
            if (doc != null) {
                return doc;
            }
        }
        return null;
    }

    private Season convertToSeasonBySortsRu(String countryName, Integer seasonNumber) {
        switch (countryName) {
            case "epl":
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6881);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6300);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5540);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4951);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4383);
                    case 2013:
                        return new Season(countryName, seasonNumber, 3270);
                    case 2012:
                        return new Season(countryName, seasonNumber, 1845);
                    case 2011:
                        return new Season(countryName, seasonNumber, 1243);
                    case 2010:
                        return new Season(countryName, seasonNumber, 574);
                    case 2009:
                        return new Season(countryName, seasonNumber, 63);
                    case 2008:
                        return new Season(countryName, seasonNumber, 3);
                    case 2007:
                        return new Season(countryName, seasonNumber, 858);
                    case 2006:
                        return new Season(countryName, seasonNumber, 859);
                    case 2005:
                        return new Season(countryName, seasonNumber, 860);
                }
            case "la-liga":
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6911);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6358);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5646);
                    case 2015:
                        return new Season(countryName, seasonNumber, 5022);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4467);
                    case 2013:
                        return new Season(countryName, seasonNumber, 3244);
                    case 2012:
                        return new Season(countryName, seasonNumber, 1968);
                    case 2011:
                        return new Season(countryName, seasonNumber, 1334);
                    case 2010:
                        return new Season(countryName, seasonNumber, 664);
                    case 2009:
                        return new Season(countryName, seasonNumber, 83);
                    case 2008:
                        return new Season(countryName, seasonNumber, 4);
                    case 2007:
                        return new Season(countryName, seasonNumber, 2176);
                    case 2006:
                        return new Season(countryName, seasonNumber, 2177);
                    case 2005:
                        return new Season(countryName, seasonNumber, 2178);
                }
            case "seria-a":
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6935);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6361);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5670);
                    case 2015:
                        return new Season(countryName, seasonNumber, 5026);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4470);
                    case 2013:
                        return new Season(countryName, seasonNumber, 3255);
                    case 2012:
                        return new Season(countryName, seasonNumber, 1973);
                    case 2011:
                        return new Season(countryName, seasonNumber, 1395);
                    case 2010:
                        return new Season(countryName, seasonNumber, 680);
                    case 2009:
                        return new Season(countryName, seasonNumber, 94);
                    case 2008:
                        return new Season(countryName, seasonNumber, 5);
                    case 2007:
                        return new Season(countryName, seasonNumber, 2323);
                    case 2006:
                        return new Season(countryName, seasonNumber, 2324);
                    case 2005:
                        return new Season(countryName, seasonNumber, 2325);
                }
            case "bundesliga":
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6878);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6317);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5596);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4974);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4459);
                    case 2013:
                        return new Season(countryName, seasonNumber, 3302);
                    case 2012:
                        return new Season(countryName, seasonNumber, 1954);
                    case 2011:
                        return new Season(countryName, seasonNumber, 1263);
                    case 2010:
                        return new Season(countryName, seasonNumber, 626);
                    case 2009:
                        return new Season(countryName, seasonNumber, 74);
                    case 2008:
                        return new Season(countryName, seasonNumber, 9);
                    case 2007:
                        return new Season(countryName, seasonNumber, 2841);
                    case 2006:
                        return new Season(countryName, seasonNumber, 2842);
                    case 2005:
                        return new Season(countryName, seasonNumber, 2843);
                }
            default:
                return null;
        }
    }

}

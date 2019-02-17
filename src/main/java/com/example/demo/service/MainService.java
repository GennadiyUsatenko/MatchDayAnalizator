package com.example.demo.service;

import com.example.demo.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<Match> matchesForBelgium = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            document = jsoupConnect(String.format(SPORTS_RU_CALENDAR_LINK, countryName, season.getSortsRuSeasonNumber(), month));

            if (document != null) {
                if (countryName.equals("jupiler-league")) {
                    List<Match> matches = document.select("table.stat-table tbody tr").stream().map(match -> {
                        String date = match.select("td.name-td").text();
                        Team homeTeam = season.findTeamByName(match.select("td.owner-td").text());
                        Team guestTeam = season.findTeamByName(match.select("td.guests-td").text());
                        int[] goals = Stream.of(match.select("td.score-td").text().split(":")).filter(s -> s.matches(".*\\d+.*")).mapToInt(s -> Integer.parseInt(s.trim())).toArray();

                        if (goals.length > 0) {
                            boolean isScored = goals[0] > 0 && goals[1] > 0;
                            return new Match(date, new Side(homeTeam, goals[0]), new Side(guestTeam, goals[1]), isScored, goals[0] > 1 && goals[1] > 1);
                        } else {
                            return new Match(date, new Side(homeTeam), new Side(guestTeam)).setInFuture(true);
                        }
                    }).collect(Collectors.toList());

                    matchesForBelgium.addAll(matches);
                    continue;
                }


                Iterator<Integer> matchDayNumbers = document.select("h3.titleH3").stream().filter(e -> e.text().contains("тур")).map(e -> Integer.parseInt(e.text().replaceAll("\\D", ""))).collect(Collectors.toList()).iterator();

                List<MatchDay> matchDays = document.select("h3.titleH3").stream().filter(e -> e.text().contains("тур")).map(matchDay -> {
                    List<Match> matches = matchDay.nextElementSibling().select("table.stat-table tbody tr").stream().map(match -> {
                        String date = match.select("td.name-td").text();
                        Team homeTeam = season.findTeamByName(match.select("td.owner-td").text());
                        Team guestTeam = season.findTeamByName(match.select("td.guests-td").text());
                        int[] goals = Stream.of(match.select("td.score-td").text().split(":")).filter(s -> s.matches(".*\\d+.*")).mapToInt(s -> Integer.parseInt(s.replaceAll("\\D", "").trim())).toArray();

                        if (goals.length > 0) {
                            boolean isScored = goals[0] > 0 && goals[1] > 0;
                            return new Match(date, new Side(homeTeam, goals[0]), new Side(guestTeam, goals[1]), isScored, goals[0] > 1 && goals[1] > 1);
                        } else {
                            return new Match(date, new Side(homeTeam), new Side(guestTeam)).setInFuture(true);
                        }
                    }).collect(Collectors.toList());

                    return new MatchDay(matches, matchDayNumbers.next(), matches.stream().allMatch(Match::isInFuture));
                }).collect(Collectors.toList());

                season.addToMatchDays(matchDays);
            }
        }

        if (countryName.equals("jupiler-league")) {
            matchesForBelgium = matchesForBelgium.stream()
                    .sorted((m1, m2) -> {
                        return  LocalDate.parse(m1.getDate().split("\\|")[0], DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay().atOffset(ZoneOffset.UTC).compareTo(
                                LocalDate.parse(m2.getDate().split("\\|")[0], DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay().atOffset(ZoneOffset.UTC));
                    }).collect(Collectors.toList());

            for (int i = 1; i <= 30; i++) {
                List<Match> matches = matchesForBelgium.stream().limit(8 * i).skip(8 * i - 8).collect(Collectors.toList());
                season.addToMatchDays(Arrays.asList(new MatchDay(matches, i, matches.stream().allMatch(Match::isInFuture))));
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
            case "ligue-1":
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6870);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6299);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5558);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4957);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4392);
                }
            case "eredivisie":
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6879);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6301);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5609);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4981);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4462);
                }
            case "liga-zon-sagres": // portugal
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6898);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6387);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5651);
                    case 2015:
                        return new Season(countryName, seasonNumber, 5042);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4410);
                }
            case "super-lig": // turkey
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6903);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6398);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5679);
                    case 2015:
                        return new Season(countryName, seasonNumber, 5054);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4484);
                }
            case "upl": // ukraine
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6857);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6340);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5620);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4973);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4487);
                }
            case "rfpl": // russia
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6886);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6297);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5582);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4959);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4385);
                }
            case "championship": // championship
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6883);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6309);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5548);
                    case 2015:
                        return new Season(countryName, seasonNumber, 4953);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4384);
                }
            case "jupiler-league": // belgium
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6931);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6336);
                    case 2016:
                        return new Season(countryName, seasonNumber, 5608);
                    case 2015:
                        return new Season(countryName, seasonNumber, 5000);
                    case 2014:
                        return new Season(countryName, seasonNumber, 4448);
                }
            case "argentina-primera-division": // argentina
                switch (seasonNumber) {
                    case 2018:
                        return new Season(countryName, seasonNumber, 6929);
                    case 2017:
                        return new Season(countryName, seasonNumber, 6333);
                }
            default:
                return null;
        }
    }

}

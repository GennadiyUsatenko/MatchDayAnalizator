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
    private static final String EPL_TABLE_LINK = "https://www.sports.ru/epl/table/";
    private static final String EPL_CALENDAR_LINK = "https://www.sports.ru/epl/calendar/?s=%s&m=%s";

    public Championship parseEPL() {
        Championship epl = new Championship("EPL", 2018);
        int sprotsRuSeson = 6881;

        Document document = jsoupConnect(EPL_TABLE_LINK);
        if (document != null) {
            epl.setTeams(document.select("table.stat-table tbody a.name").stream().map(t -> new Team(t.attr("title"))).collect(Collectors.toList()));
        }
        for (int month = 1; month <= 12; month++) {
            if (month == 6 || month == 7) continue;
            document = jsoupConnect(String.format(EPL_CALENDAR_LINK, sprotsRuSeson, month));
            if (document != null) {
                Iterator<Integer> matchDayNumbers = document.select("h3.titleH3").stream().filter(e -> e.text().contains("тур")).map(e -> Integer.parseInt(e.text().replaceAll("\\D", ""))).collect(Collectors.toList()).iterator();
                List<MatchDay> matchDays = document.select("table.stat-table tbody").stream().map(matchDay -> {
                    List<Match> matches = matchDay.select("tr").stream().map(match -> {
                        Team homeTeam = epl.findTeamByName(match.select("td.owner-td").text());
                        Team guestTeam = epl.findTeamByName(match.select("td.guests-td").text());
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

                epl.addToMatchDays(matchDays);
            }
        }
        return epl.setTableStatList();
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
}

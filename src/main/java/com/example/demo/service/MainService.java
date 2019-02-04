package com.example.demo.service;

import com.example.demo.model.Championship;
import com.example.demo.model.Team;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MainService {

    private static final int MAX_DOWNLOAD_RETRIES = 3;
    private static final String EPL_TABLE_LINK = "https://www.sports.ru/epl/table/";

    public Championship parseEPL() {
        Championship epl = new Championship("EPL", 2018);

        Document document = jsoupConnect(EPL_TABLE_LINK);
        if (document != null) {
            epl.setTeams(document.select("table.stat-table tbody a.name").stream().map(t -> new Team(t.attr("title"))).collect(Collectors.toList()));
        }
        return epl;
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

package com.example.demo.controller;

import com.example.demo.model.Balance;
import com.example.demo.model.Season;
import com.example.demo.model.enums.BettingStrategyType;
import com.example.demo.service.BettingStrategyService;
import com.example.demo.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Михаил on 03.02.2019.
 */
@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private BettingStrategyService bettingStrategyService;

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> status() {
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @GetMapping("/season/{countryName}/{seasonNumber}")
    public ModelAndView season(@PathVariable("countryName") String countryName,
                               @PathVariable("seasonNumber") Integer seasonNumber,
                               @RequestParam(value = "byGoal", required = false, defaultValue = "false") Boolean isUseByGoalStat,
                               @RequestParam(value = "strick", required = false, defaultValue = "1") Integer strick,
                               @RequestParam(value = "teamStrategy", required = false, defaultValue = "2") Integer teamStrategy) throws CloneNotSupportedException {
        ModelAndView modelAndView = new ModelAndView("season");
        Season season = mainService.parseSeason(countryName, seasonNumber);
        List<Balance> balances = bettingStrategyService.prepareBalance(season, BettingStrategyType.of(teamStrategy), isUseByGoalStat, strick);

        modelAndView.addObject("season", season);
        modelAndView.addObject("balance", balances);

        return modelAndView;
    }
}

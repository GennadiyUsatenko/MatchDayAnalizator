package com.example.demo.controller;

import com.example.demo.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;

/**
 * Created by Михаил on 03.02.2019.
 */
@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> status() {
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @GetMapping("/start")
    public ModelAndView start() {
        mainService.parseEPL();
        ModelAndView modelAndView = new ModelAndView("start");
        return modelAndView;
    }
}

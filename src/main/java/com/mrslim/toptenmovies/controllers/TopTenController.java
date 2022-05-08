package com.mrslim.toptenmovies.controllers;

import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.services.MainMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Controller
public class TopTenController {
    @Autowired
    MainMovieService mainMovieService;

    @GetMapping("/main")
    private String main(@RequestParam(value = "error", required = false) String error, Model model) {
        try {

        } catch (Exception e) {
            model.addAttribute("error", error != null);
            model.addAttribute("message", String.format("Не удалось отобразить данные. Причина:\n%s", e.getMessage()));
        }
        return "/main";
    }

    @PostMapping(value = {"/main", "chart/{years}"})
    private String main(@RequestParam(value = "error", required = false) String error, Model model, String fromYear, String toYear) {
        try {
            if (toYear != null && !toYear.isEmpty()){
                System.out.println(fromYear + " " + toYear);
                return String.format("redirect:/chart/%s-%s", fromYear, toYear);
            }
            else return "redirect:/chart/" + fromYear;
        } catch (Exception e) {
            model.addAttribute("error", error != null);
            model.addAttribute("message", String.format("Не удалось отобразить данные. Причина:\n%s", e.getMessage()));
            return "/main";
        }
    }

    @GetMapping("chart/{years}")
    private String chart(@PathVariable(value = "years") String years, Model model) {
        try {
            LinkedList<MovieEntity> chart = mainMovieService.getMovies(years);
            model.addAttribute("chart", chart);
            model.addAttribute("years", years);
        } catch (Exception e) {
            model.addAttribute("message", String.format("Не удалось отобразить данные. Причина:\n%s", e.getMessage()));
        }
        return "/chart";
    }

}

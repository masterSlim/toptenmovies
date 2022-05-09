package com.mrslim.toptenmovies.controllers;

import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.services.MainMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

@Controller
public class TopTenController {
    @Autowired
    MainMovieService mainMovieService;

    @GetMapping("/main")
    private String main(@RequestParam(value = "error", required = false) String error, Model model) {
            model.addAttribute("error", error != null);
            model.addAttribute("message", String.format("Не удалось отобразить данные."));
        return "/main";
    }

    @PostMapping(value = {"/main", "chart/{years}"})
    private String main(@RequestParam(value = "error", required = false) String error, Model model, String fromYear, String toYear) {
        try {
            if (toYear != null && !toYear.isEmpty()){
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
    private String chart(@PathVariable(value = "years") String years, @RequestParam(value = "error", required = false) String error, Model model) throws URISyntaxException, IOException, InterruptedException {
            LinkedList<MovieEntity> chart = mainMovieService.getMovies(years);
            if (chart == null || chart.isEmpty()){
                model.addAttribute("error", true);
                model.addAttribute("message", String.format("Не удалось получить данные о топе," +
                        "\n так как Кинопоиск временно закрыл доступ роботам:)" +
                        "\n Попробуйте снова позже. Когда — одному Богу известно" ));
                return "chart";
            }
            model.addAttribute("chart", chart);
            model.addAttribute("years", years);

        return "/chart";
    }

}

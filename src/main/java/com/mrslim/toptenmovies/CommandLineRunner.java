package com.mrslim.toptenmovies;

import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import com.mrslim.toptenmovies.services.ParseMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
    @Autowired
    ParseMovieService parseMovieService;
    @Autowired
    ChartRepository chartRepository;
    @Autowired
    MovieRepository movieRepository;

    LinkedHashMap<String, String> commands = new LinkedHashMap<>();

    {
        commands.put("help", "выводит список команд");
        commands.put("get 2021", "получить топ-10 фильмов кинопоиска за 2021 год");
        commands.put("get 2018-2021", "получить топ-10 фильмов кинопоиска за период 2018-2021 год");
        commands.put("drop", "очистить локальную базу данных");
        commands.put("q", "завершить работу");
    }


    @Override
    public void run(String... args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        help();
        if (args.length > 0) parseInput(Arrays.toString(args));
        while (true) {
            String userInput = reader.readLine();
            if (parseInput(userInput)) {
                System.out.println("successful");
                continue;
            } else System.out.println("Ошибка ввода");
        }
    }

    private boolean parseInput(String userInput) throws IOException {
        String temp = userInput.trim().toLowerCase();
        if (temp.isEmpty()) return false;

        // b граница слова
        Pattern command = Pattern.compile("\\bhelp\\b");
        Matcher commandMatch = command.matcher(temp);
        if (commandMatch.find()) return help();

        // b граница слова
        command = Pattern.compile("\\bget\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()) return get(userInput);

        // b граница слова
        command = Pattern.compile("\\bdrop\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()){
            return drop();
        }

        // b граница слова
        command = Pattern.compile("\\bq\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()) {
            System.exit(0);
            return true;
        }

        return false;
    }

    private boolean drop() {
        //TODO не очищает файл
        chartRepository.deleteAll();
        movieRepository.deleteAll();
        System.out.println("Локальные базы данных успешно очищены");
        return true;
    }

    private boolean help() {
        StringBuilder showConmands = new StringBuilder();
        commands.forEach((key, value) -> showConmands.append(key).append(" ").append(value).append("\n"));
        System.out.println("Команды приложения:");
        for(String key: commands.keySet()){
            System.out.printf("%-15s %s\n", key, commands.get(key));
        }
        //System.out.println(showConmands);
        return true;
    }

    //TODO проверять сначала кэш, потом базу, потом уже идти парсить
    private boolean get(String command) throws IOException {
        LinkedList<MovieEntity> result = null;
        String successMessage = "В базу данных были добавлены следующие фильмы из рейтинга";
        {
            // b     граница слова (в том числе группы цифр)
            // d{4}  четыре цифры
            // s*    ни одного или сколько угодно пробелов
            // W+    один и более симовлов, не относящихся к буквам и цифрам
            // s*    ни одного или сколько угодно пробелов
            // d{4}  четыре цифры
            // b     граница слова (в том числе группы цифр)
            Pattern p = Pattern.compile("\\b\\d{4}\\s*\\W+\\s*\\d{4}\\b");
            Matcher m = p.matcher(command);
            if (m.find()) {
                String range = m.group(0);
                // d{4} четыре цифры
                Pattern p2 = Pattern.compile("\\d{4}");
                Matcher m2 = p2.matcher(range);
                m2.find();
                int firstYear = Integer.parseInt(m2.group());
                m2.find();
                int secondYear = Integer.parseInt(m2.group());
                if (firstYear < secondYear) {
                    result = parseMovieService.getMovies(firstYear, secondYear);
                    System.out.printf(successMessage + " за период %d - %d \n%n", firstYear, secondYear);
                    result.forEach(System.out::println);
                } else {
                    result = parseMovieService.getMovies(secondYear, firstYear);
                    System.out.printf(successMessage + " за период %d - %d \n%n", secondYear, firstYear);
                    result.forEach(System.out::println);
                }
                return true;
            }
            // b     граница слова (в том числе группы цифр)
            // d{4}  четыре цифры
            // b     граница слова (в том числе группы цифр)
            p = Pattern.compile("\\b\\d{4}\\b");
            m = p.matcher(command);
            if (m.find()) {
                int forYear = Integer.parseInt(m.group());
                result = parseMovieService.getMovies(forYear);
                System.out.printf(successMessage + " за %d год\n", forYear);
                result.forEach(System.out::println);
                return true;
            }
        }
        return false;
    }

}

package com.mrslim.toptenmovies;

import com.mrslim.toptenmovies.config.GeneralConfig;
import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import com.mrslim.toptenmovies.services.MainMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
    @Autowired
    ChartRepository chartRepo;
    @Autowired
    MovieRepository movieRepo;
    @Autowired
    GeneralConfig appConfig;
    @Autowired
    MainMovieService mainMovieService;


    LinkedHashMap<String, String> commands = new LinkedHashMap<>();

    {
        commands.put("help", "выводит список команд");
        commands.put("get 2021", "получить топ-10 фильмов Кинопоиска за 2021 год");
        commands.put("get 2018-2021", "получить топ-10 фильмов Кинопоиска за период 2018-2021 год");
        commands.put("data", "количество записей в локальной базе данных");
        commands.put("clear", "очистить локальную базу данных");
        commands.put("q", "завершить работу");
    }


    @Override
    public void run(String... args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        help();
        if (args.length > 0) parseInput(Arrays.toString(args));
        while (true) {
            String usersInput = reader.readLine();
            if (parseInput(usersInput)) {
                continue;
            } else System.out.println("Ошибка ввода");
        }
    }

    private boolean parseInput(String userInput) throws IOException, URISyntaxException, InterruptedException {
        String temp = userInput.trim().toLowerCase();
        boolean executed = false;

        if (temp.isEmpty()) return false;

        // b граница слова
        Pattern command = Pattern.compile("\\bhelp\\b");
        Matcher commandMatch = command.matcher(temp);
        if (commandMatch.find()) executed = help();

        // b граница слова
        command = Pattern.compile("\\bget\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()) executed = get(userInput);

        // b граница слова
        command = Pattern.compile("\\bdata\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()) executed = data();

        // b граница слова
        command = Pattern.compile("\\bclear\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()) executed = clear();

        // b граница слова
        command = Pattern.compile("\\bq\\b");
        commandMatch = command.matcher(temp);
        if (commandMatch.find()) {
            System.exit(0);
        }

        return executed;
    }

    public boolean help() {
        System.out.println("Команды приложения:");
        for (String key : commands.keySet()) {
            System.out.printf("%-15s %s\n", key, commands.get(key));
        }
        return true;
    }

    //TODO проверять сначала кэш, потом базу, потом уже идти парсить
    public boolean get(String command) throws IOException, URISyntaxException, InterruptedException {

        // b     граница слова (в том числе группы цифр)
        // d{4}  четыре цифры
        // s*    ни одного или сколько угодно пробелов
        // W+    один и более симовлов, не относящихся к буквам и цифрам
        // s*    ни одного или сколько угодно пробелов
        // d{4}  четыре цифры
        // b     граница слова (в том числе группы цифр)
        Pattern p = Pattern.compile("\\b\\d{4}\\s*\\W+\\s*\\d{4}\\b");
        Matcher m = p.matcher(command);
        // выполняется, если найден ввод диапазона (например 2021-2022)
        if (m.find()) {
            
            String range = m.group(0);
            // d{4} четыре цифры
            Pattern p2 = Pattern.compile("\\d{4}");
            Matcher m2 = p2.matcher(range);
            m2.find();
            int firstEntry = Integer.parseInt(m2.group());
            m2.find();
            int secondEntry = Integer.parseInt(m2.group());
            int fromYear = Math.min(firstEntry, secondEntry);
            int toYear = Math.max(firstEntry, secondEntry);
            mainMovieService.getMovies(fromYear, toYear);
            return true;
        }

        // b     граница слова (в том числе группы цифр)
        // d{4}  четыре цифры
        // b     граница слова (в том числе группы цифр)
        p = Pattern.compile("\\b\\d{4}\\b");
        m = p.matcher(command);

        // выполняется, если найден ввод одного года (например 2021)
        // Этот блок выполняется только тогда, когда не был найден диапазон,
        // иначе будет обработан только первый найденный год из диапазона.
        // Например при запросе get 2021-2022 был бы обработан get 2021
        if (m.find()) {
            int year = Integer.parseInt(m.group());
            mainMovieService.getMovies(year);
            return true;
        }
        return false;
    }

    public boolean data() {
        long chartSize = chartRepo.count();
        long movieSize = movieRepo.count();
        System.out.printf("В базе данных %d рейтингов и %d фильмов\n", chartSize, movieSize);
        return true;
    }

    public boolean clear() {
        chartRepo.deleteAll();
        movieRepo.deleteAll();
        System.out.println("Локальные базы данных успешно очищены");
        return true;
    }


}

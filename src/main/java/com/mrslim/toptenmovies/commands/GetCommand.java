package com.mrslim.toptenmovies.commands;

import com.mrslim.toptenmovies.services.MainMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component

public class GetCommand implements Command {
    @Autowired
    MainMovieService mainMovieService;

    @Override
    public boolean execute(String input) throws URISyntaxException, IOException, InterruptedException {

        // b     граница слова (в том числе группы цифр)
        // d{4}  четыре цифры
        // b     граница слова (в том числе группы цифр)
        // s*    ни одного или сколько угодно пробелов
        // W+    один и более символов, не относящихся к буквам и цифрам
        // s*    ни одного или сколько угодно пробелов
        // b     граница слова (в том числе группы цифр)
        // d{4}  четыре цифры
        // b     граница слова (в том числе группы цифр)
        Pattern p = Pattern.compile("\\b\\d{4}\\b\\s*\\W+\\s*\\b\\d{4}\\b");
        Matcher m = p.matcher(input);
        // Выполняется, если найден ввод диапазона (например, 2021-2022)
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
            mainMovieService.getFromSite(fromYear, toYear);
            return true;
        }

        // b     граница слова (в том числе группы цифр)
        // d{4}  четыре цифры
        // b     граница слова (в том числе группы цифр)
        p = Pattern.compile("\\b\\d{4}\\b");
        m = p.matcher(input);

        // Выполняется, если найден ввод одного года (например 2021)
        // Этот блок выполняется только тогда, когда не был найден диапазон,
        // иначе будет обработан только первый найденный год из диапазона.
        // Например при запросе get 2021-2022 был бы обработан get 2021
        if (m.find()) {
            int year = Integer.parseInt(m.group());
            mainMovieService.getFromSite(year);
            return true;
        }
        return false;
    }
}
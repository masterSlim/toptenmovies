package com.mrslim.toptenmovies;

import com.mrslim.toptenmovies.commands.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
    @Autowired
    CommandsInvoker invoker;
    @Autowired
    DataCommand data;
    @Autowired
    GetCommand get;
    @Autowired
    HelpCommand help;
    @Autowired
    QuitCommand quit;
    @Autowired
    ClearCommand clear;


    @Override
    public void run(String... args) throws Exception {
        invoker.addCommand("help", help, "выводит список команд");
        invoker.addCommand("get", get, "получить топ-10 фильмов за год или период (например, get 2021 или get 2018-2022)");
        invoker.addCommand("data", data, "количество записей в локальной базе данных");
        invoker.addCommand("clear", clear, "очистить локальную базу данных");
        invoker.addCommand("q", quit, "завершить работу");

        invoker.execute("help", null);
        LinkedList<String> userCommands;
        if (args.length > 0) {
            userCommands = parseCommands(Arrays.toString(args));
            userCommands.forEach((command) -> {
                try {
                    boolean executed = invoker.execute(command, Arrays.toString(args));
                    if (!executed){
                        System.err.println("Ошибка в аргументах запуска приложения");
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String usersInput = reader.readLine();
            userCommands = parseCommands(usersInput);
            userCommands.forEach((command) -> {
                try {
                    boolean executed = invoker.execute(command, usersInput);
                    if(!executed){
                        System.err.println("Ошибка ввода команды");
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private LinkedList<String> parseCommands(String userInput) {
        String input = userInput.trim().toLowerCase();
        LinkedList<String> result = new LinkedList<>();
        if (input.isEmpty()) return result;

        for(String existingCommand : CommandsInvoker.getDescriptions().keySet()){
            // b граница слова
            String pattern = "\\b" + existingCommand + "\\b";
            Pattern command = Pattern.compile(pattern);
            Matcher commandMatch = command.matcher(input);
            if (commandMatch.find()) result.add(existingCommand);
        }

        return result;
    }
}

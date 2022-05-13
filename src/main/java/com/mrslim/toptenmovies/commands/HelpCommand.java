package com.mrslim.toptenmovies.commands;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

@Component

public class HelpCommand implements Command{
    @Override
    public boolean execute(String input) throws URISyntaxException, IOException, InterruptedException {
        LinkedHashMap<String, String> commands = CommandsInvoker.getDescriptions();
        System.out.println("Команды приложения:");
        for (String key : commands.keySet()) {
            System.out.printf("%-8s %s\n", key, commands.get(key));
        }
        return true;
    }
}

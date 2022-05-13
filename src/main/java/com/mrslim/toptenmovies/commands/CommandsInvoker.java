package com.mrslim.toptenmovies.commands;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

@Component
public class CommandsInvoker {
    private static final LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> descriptions = new LinkedHashMap<>();

    public void addCommand(String name, Command command, String description) {
        commands.put(name, command);
        descriptions.put(name, description);
    }

    public static LinkedHashMap<String, String> getDescriptions() {
        return descriptions;
    }

    public boolean execute(String name, String input) throws URISyntaxException, IOException, InterruptedException {
     Command command = commands.get(name);
     if(command == null){
         System.err.printf("Команда \"%s\" не зарегистрирована", name);
         return false;
     }
     return command.execute(input);
    }

}


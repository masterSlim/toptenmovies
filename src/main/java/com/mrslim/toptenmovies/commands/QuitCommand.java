package com.mrslim.toptenmovies.commands;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
@Component

public class QuitCommand implements Command{
    @Override
    public boolean execute(String input) throws URISyntaxException, IOException, InterruptedException {
        System.exit(0);
        return true;
    }
}

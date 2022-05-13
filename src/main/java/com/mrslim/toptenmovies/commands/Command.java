package com.mrslim.toptenmovies.commands;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
@FunctionalInterface
public interface Command {
    public boolean execute(String input) throws URISyntaxException, IOException, InterruptedException;
}

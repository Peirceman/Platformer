package com.platformer;

import com.args.ArgParser;
import com.args.Option;
import com.platformer.main.Game;
import com.platformer.main.Menu;
import com.platformer.main.Window;
import com.platformer.supers.GamePanel;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.platformer.util.Format.withoutBraces;
import static javax.swing.SwingUtilities.invokeLater;

public class Main {

    static String location = "center";
    static Window window;
    static List<String> locations = Stream.of("topLeft", "topCenter", "topRight", "centerLeft", "center",
                                              "centerRight", "bottomLeft", "bottomCenter", "bottomRight")
                                                  .map(l -> l.toLowerCase(Locale.ROOT)).toList();
                                                                // all locations that can be supplied with the -l key

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, URISyntaxException {
        Option[] options = new Option[] {
            new Option("location to place the screen when running :\n    " + withoutBraces(locations.toArray(new String[0])), "-l", "--location")};
        ArgParser parser = new ArgParser("Usage: platformer " + options[0].namesCombined("|") + " <String location>]", options, "-h", "--help");
        parser.parse(args);
        if (parser.hasValue(options[0])) {
            String givenLocation = parser.getString(options[0]);
            if (!locations.contains(givenLocation.toLowerCase(Locale.ROOT)))
                parser.printError("Error: invalid location '" + location + "'");

            location = givenLocation;
        }

        Game.playerLevels = Paths.get(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent(),"my.lev")
                .toString();

        invokeLater(() -> window = new Window(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, "platformer", new Menu(), location));
    }

    public static void addGame(String level) {
        window.clearSocket();
        window.dispose();
        Game.currentLevel = level;
        invokeLater(() -> window = new Window(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, "platformer", new Game() , location));
    }

    @SuppressWarnings("unused")
    public static long time(Object o, Method m, int amountOfTimes, Object... args)
            throws InvocationTargetException, IllegalAccessException {
        long start, end;
        start = System.nanoTime();
        for (int i = 0; i < amountOfTimes; i++) {
            m.invoke(o, args);
        }
        end = System.nanoTime();
        return end - start;
    }
}

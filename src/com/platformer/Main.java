package com.platformer;

import com.args.ArgParser;
import com.args.Option;
import com.platformer.main.Game;
import com.platformer.main.Menu;
import com.platformer.main.Window;
import com.platformer.supers.GamePanel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.platformer.util.Format.withoutBraces;
import static javax.swing.SwingUtilities.invokeLater;

public class Main {
    public static final File playerLevelsDir;

    static {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            playerLevelsDir = new File(System.getenv("APPDATA") + "\\platformer");
        } else if (os.contains("nux") | os.contains("nix") | os.contains("aix") | os.contains("mac")) {
            playerLevelsDir = new File(System.getenv("user.home") + "/.platformer");
        } else {
            throw new Error("Unknown os");
        }

        if (playerLevelsDir.mkdirs())
            System.out.println("creating " + playerLevelsDir.getPath());
    }
    static String location = "center";
    static Window window;
    static List<String> locations = Stream.of("topLeft", "topCenter", "topRight", "centerLeft", "center",
                                              "centerRight", "bottomLeft", "bottomCenter", "bottomRight")
                                                  .map(l -> l.toLowerCase(Locale.ROOT)).toList(); // all locations that can be supplied with the -l key

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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

        invokeLater(() -> window = new Window(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, "platformer", new Menu(), location));
    }

    public static void addGame(String level) {
        Game.editing = !(level.toCharArray()[0] >= '0' & level.toCharArray()[0] <= '9');
        window.dispose();
        Game.currentLevel = level;
        invokeLater(() -> window = new Window(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, "platformer", new Game() , location));
    }

    public static String getPlayerLevelPath(String level) {
        return Path.of(playerLevelsDir.getPath(), level + ".lev").toString();
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

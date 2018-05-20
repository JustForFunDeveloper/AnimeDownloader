package tapsi.logic;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FileHandler {

    private static String path = "\\\\192.168.1.113\\Anime2"; //"D:\\Anime";

    private static Map<String, Anime> animeMap = new HashMap<>();
    private static List<String> animeNames = new ArrayList<>();

    protected static Map<String, Anime> getAnimeMap() {
        return animeMap;
    }

    protected static List<String> getAnimeNames() {
        return animeNames;
    }

    private static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                //System.out.println(fileEntry.getName());
                addEntry(fileEntry.getName());
            }
        }
    }

    public static void readFolders () {
        final File folder = new File(path);
        listFilesForFolder(folder);
    }

    @NotNull
    private static void addEntry (String name) {
        Pair<String, String> value = safeNameNumberInList(name);

        if (animeMap.containsKey(value.getKey())) {
            Anime anime= animeMap.get(value.getKey());
            anime.addAnimeEpisode(value.getValue(), name);
        } else {
            Anime anime = new Anime(value.getKey());
            anime.addAnimeEpisode(value.getValue(), name);
            animeMap.put(value.getKey(), anime);
            animeNames.add(value.getKey());
        }
    }

    @NotNull
    private static Pair<String, String> safeNameNumberInList(String value) {
        String name = value.replace("[HorribleSubs] ", "");
        String number = name;
        int before = name.length();
        name = name.replaceAll(" - [0-9]*.* \\[[0-9]*p].mkv", "");
        if (before == name.length())
            System.out.println(name);
        number = number.replace(name, "");
        number = number.replace("- ", "");
        number = number.replace("[720p].mkv", "");

        return new Pair<>(name, number);
    }
}

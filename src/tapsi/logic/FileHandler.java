package tapsi.logic;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FileHandler {

    //private static String path = "D:\\Anime"; // "C:\\Users\\atapp\\Downloads\\Privat"; "\\\\192.168.1.113\\Anime2"; //"D:\\Anime";

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
                addEntry(fileEntry.getName());
            }
        }
    }

    protected static void readFolders (String path) {
        final File folder = new File(path);
        listFilesForFolder(folder);
    }

    @NotNull
    private static void addEntry (String name) {
        Pair<String, String> value = safeNameNumberInList(name);

        if (animeMap.containsKey(value.getKey())) {
            Anime anime= animeMap.get(value.getKey());
            if (!anime.containsAnimeEntryByFileName(name))
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
        name = name.replaceAll(" - [0-9]#*.* \\[[0-9]*p].mkv", "");
        name = name.replaceAll("'","");
        number = number.replace(name, "");
        number = number.replace("- ", "");
        number = number.replaceAll(" \\[[0-9]*p].mkv", "");

        if (number.contains(".")) {
            number = number.substring(0,number.indexOf("."));
        } else if (number.contains("v")) {
            number = number.substring(0, number.indexOf("v"));
        }

        return new Pair<>(name, number);
    }
}

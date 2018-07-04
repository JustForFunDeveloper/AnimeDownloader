package tapsi.logic.handler;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;
import tapsi.logic.container.Anime;
import tapsi.logic.container.AnimeEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FileHandler {

    private static Map<String, Anime> animeMap;
    private static List<String> animeNames;

    protected static Map<String, Anime> getAnimeMap() {
        return animeMap;
    }

    protected static List<String> getAnimeNames() {
        return animeNames;
    }

    protected static void readFolders(List<String> paths) {
        animeMap = new HashMap<>();
        animeNames = new ArrayList<>();
        for (String path : paths) {
            File folder = new File(path);
            listFilesForFolder(folder);
        }
    }

    protected static void deleteFiles(List<AnimeEntry> animeEntries) {
        for (AnimeEntry entry : animeEntries) {
            try {
                Path path = Paths.get(entry.getFullPathName());
                Files.delete(path);
            } catch (IOException e) {
                System.err.println("Couldn't find file or permission issue!");
                e.printStackTrace();
            }
        }
    }

    @NotNull
    private static void addEntry(String name, String path) {
        Pair<String, String> value = safeNameNumberInList(name);

        if (animeMap.containsKey(value.getKey())) {
            Anime anime = animeMap.get(value.getKey());
            if (!anime.containsAnimeEntryByFileName(name))
                anime.addAnimeEpisode(value.getValue(), name, path);
        } else {
            Anime anime = new Anime(value.getKey());
            anime.addAnimeEpisode(value.getValue(), name, path);

            animeMap.put(value.getKey(), anime);
            animeNames.add(value.getKey());
        }
    }

    @NotNull
    private static Pair<String, String> safeNameNumberInList(String value) {
        String name = value.replace("[HorribleSubs] ", "");
        String number = name;
        name = name.replaceAll(" - [0-9]#*.* \\[[0-9]*p].mkv", "");
        name = name.replaceAll("'", "");
        number = number.replace(name, "");
        number = number.replace("- ", "");
        number = number.replaceAll(" \\[[0-9]*p].mkv", "");
        number = number.replaceAll("\\s", "");

        if (number.contains(".")) {
            number = number.substring(0, number.indexOf("."));
        } else if (number.contains("v")) {
            number = number.substring(0, number.indexOf("v"));
        }

        return new Pair<>(name, number);
    }

    private static void listFilesForFolder(File folder) {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                addEntry(fileEntry.getName(), fileEntry.getPath());
            }
        }
    }
}

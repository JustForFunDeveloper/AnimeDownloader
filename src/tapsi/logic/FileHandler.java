package tapsi.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static String path = "C:\\Anime";

    private static List<AnimeEntry> allAnimeEpisodes = new ArrayList<>();

    private static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }

    public static void readFolders () {
        final File folder = new File(path);
        listFilesForFolder(folder);
    }
}

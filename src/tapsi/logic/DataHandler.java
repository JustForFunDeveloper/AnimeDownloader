package tapsi.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {

    private static String path1 = "http://horriblesubs.info/rss.php?res=720";

    private static Map<String, Anime> animeMap = new HashMap<>();
    private static List<String> animeNames = new ArrayList<>();
    
    private static List<AnimeEntry> animeEntries = new ArrayList<>();

    protected static List<String> getLocalAnimeNames() {
        updateData();
        return animeNames;
    }

    protected static List<List<String>> getAnimeStatistics() {
        return null;
    }

    protected static List<String> getAnimesWithScope(AnimeScope scope) {
        return null;
    }

    protected static List<String> getAnimesWithStatus(AnimeStatus status) {
        return null;
    }

    protected static List<String> getFeedAnimes() {
        List<AnimeEntry> animeEntries = FeedHandler.downloadFile(path1);
        DataHandler.animeEntries.addAll(animeEntries);
        return toStringList(animeEntries);
    }

    private static void updateData() {
        FileHandler.readFolders();
        animeMap = FileHandler.getAnimeMap();
        animeNames = FileHandler.getAnimeNames();
    }
    
    private static List<String> toStringList(List<AnimeEntry> animeEntries) {
        List<String> animeEntriesList = new ArrayList<>();
        for (AnimeEntry animeEntry : animeEntries) {
            animeEntriesList.add(animeEntry.getName());
        }
        return animeEntriesList;
    }
}

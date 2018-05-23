package tapsi.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Finish comments and implement loading from db at startup
//TODO: Implement Statistics

public class DataHandler {

    private static String path1 = "http://horriblesubs.info/rss.php?res=720";

    private static Map<String, Anime> animeMap = new HashMap<>();
    private static List<String> animeNames = new ArrayList<>();
    
    private static List<AnimeEntry> feedEntries = new ArrayList<>();

    private static DBHandler dbHandler = new DBHandler();

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

    protected static List<String> getFeedAnimeNames() {
        List<AnimeEntry> animeEntries = FeedHandler.downloadFile(path1);
        DataHandler.feedEntries = new ArrayList<>(animeEntries);
        return toStringList(animeEntries);
    }

    protected static Anime getAnimeByName (String anime) {
        return animeMap.get(anime);
    }

    protected static AnimeEntry getFeedEntryByName (String entryName) {
        for (AnimeEntry entry : feedEntries) {
            if (entry.getName().equals(entryName))
                return entry;
        }
        return null;
    }

    protected static List<String> getAutomaticDownloadFeeds () {
        List<String> returnValue = new ArrayList<>();

        for (AnimeEntry entry : feedEntries) {
            Anime anime  = getAnimeByName(entry.getName());
            if (anime != null) {
                if (anime.getAnimeScope().equals(AnimeScope.MUSTHAVE)) {
                    returnValue.add(entry.getName());
                }
            }
        }
        return returnValue;
    }

    protected static void setAnimeData (Anime anime) {
        dbHandler.insertClient(anime.getName(),anime.getAnimeScope().toString(), anime.getAnimeStatus().toString(), anime.getSeasonCount());
    }

    private static void syncDatabaseData () {
        List<Anime> dbAnime = dbHandler.readAllObjects();

        if (dbAnime == null)
            return;

        for (Anime anime : dbAnime) {
            Anime localAnime = getAnimeByName(anime.getName());
            if (localAnime != null) {
                localAnime.setAnimeScope(anime.getAnimeScope());
                localAnime.setAnimeStatus(anime.getAnimeStatus());
                localAnime.setSeasonCount(anime.getSeasonCount());
            } else {
                //TODO: delete DB Entry
            }
        }
    }
    
    private static List<String> toStringList(List<AnimeEntry> animeEntries) {
        List<String> animeEntriesList = new ArrayList<>();
        for (AnimeEntry animeEntry : animeEntries) {
            animeEntriesList.add(animeEntry.getName());
        }
        return animeEntriesList;
    }

    protected static List<String> toStringListWithNumber(List<AnimeEntry> animeEntries) {
        List<String> animeEntriesList = new ArrayList<>();
        for (AnimeEntry animeEntry : animeEntries) {
            animeEntriesList.add(animeEntry.getFileName());
        }
        return animeEntriesList;
    }

    protected static void closeApplication () {
        dbHandler.closeDB();
    }

    private static void updateData() {
        FileHandler.readFolders();
        animeMap = FileHandler.getAnimeMap();
        animeNames = FileHandler.getAnimeNames();
        syncDatabaseData();
    }
}

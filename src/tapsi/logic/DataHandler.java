package tapsi.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {

    //TODO: Implement Path Input Handling perhpas Multiple paths!
    private static String path1 = "http://horriblesubs.info/rss.php?res=720";

    private static Map<String, Anime> animeMap = new HashMap<>();
    private static List<String> animeNames = new ArrayList<>();
    
    private static List<AnimeEntry> feedEntries = new ArrayList<>();

    private static DBHandler dbHandler = new DBHandler();

    protected static List<String> getLocalAnimeNames() {
        updateData();
        return animeNames;
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
        dbHandler.insertAnime(anime.getName(),anime.getAnimeScope().toString(), anime.getAnimeStatus().toString(), anime.getSeasonCount());
    }

    protected static void addTempAnime (Anime anime) {
        animeMap.put(anime.getName(), anime);
    }

    protected static void startDownload (String animeName) {
        String magnetUrl = "";

        for (AnimeEntry entry : feedEntries) {
            if (entry.getName().equals(animeName))
                magnetUrl = entry.getMagnetUrl();
        }

        FeedHandler.openLink(magnetUrl);
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
                dbHandler.deleteAnime(anime.getName());
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

    private static void updateData() {
        FileHandler.readFolders();
        animeMap = FileHandler.getAnimeMap();
        animeNames = FileHandler.getAnimeNames();
        syncDatabaseData();
    }
}

package tapsi.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {

    private static String feedPath = ""; // = "http://horriblesubs.info/rss.php?res=720";
    private static String localPath = ""; // "D:\\Anime";

    private static Map<String, Anime> animeMap = new HashMap<>();
    private static List<String> animeNames = new ArrayList<>();

    private static List<AnimeEntry> feedEntries = new ArrayList<>();

    private static DBHandler dbHandler = new DBHandler();

    protected static void loadPaths() {
        List<String> paths = dbHandler.readAllPaths();
        localPath = paths.get(0);
        feedPath = paths.get(1);
    }

    protected static String getLocalPath() {
        loadPaths();
        return localPath;
    }

    protected static String getFeedPath() {
        loadPaths();
        return feedPath;
    }

    protected static List<String> getLocalAnimeNames() {
        updateLocalData();
        return animeNames;
    }

    protected static List<String> getFeedAnimeNames() {
        if (feedPath.isEmpty())
            return null;
        List<AnimeEntry> animeEntries = FeedHandler.downloadFile(feedPath);
        DataHandler.feedEntries = new ArrayList<>(animeEntries);
        return toStringList(animeEntries);
    }

    protected static Anime getAnimeByName(String anime) {
        return animeMap.get(anime);
    }

    protected static AnimeEntry getFeedEntryByName(String entryName) {
        for (AnimeEntry entry : feedEntries) {
            if (entry.getName().equals(entryName))
                return entry;
        }
        return null;
    }

    protected static List<String> getAutomaticDownloadFeeds() {
        List<String> returnValue = new ArrayList<>();

        for (AnimeEntry entry : feedEntries) {
            Anime anime = getAnimeByName(entry.getName());
            if (anime != null) {
                if (anime.getAnimeScope().equals(AnimeScope.MUSTHAVE) && !anime.getAnimeStatus().equals(AnimeStatus.FINISHED)) {
                    boolean exists = false;
                    for (AnimeEntry episode : anime.getAnimeEntries()) {
                        if (episode.getNumber().equals(entry.getNumber()))
                            exists = true;
                    }
                    if (!exists)
                        returnValue.add(entry.getName());
                }
            }
        }
        return returnValue;
    }

    protected static void setPaths(String feedPath, String localPath) {
        DataHandler.localPath = localPath;
        DataHandler.feedPath = feedPath;
        dbHandler.insertPath(0, localPath);
        dbHandler.insertPath(1, feedPath);
    }

    protected static void setAnimeData(Anime anime) {
        dbHandler.insertAnime(anime.getName(), anime.getAnimeScope().toString(), anime.getAnimeStatus().toString(), anime.getSeasonCount());
    }

    protected static void addTempAnime(Anime anime) {
        animeMap.put(anime.getName(), anime);
    }

    protected static void startDownload(String animeName) {
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

    protected static void closeApplication() {
        dbHandler.closeDB();
    }

    protected static void syncDatabaseData() {
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

    protected static void deleteAnime(String name) {
        FileHandler.deleteFiles(animeMap.get(name).getAnimeEntries());
    }

    private static List<String> toStringList(List<AnimeEntry> animeEntries) {
        List<String> animeEntriesList = new ArrayList<>();
        for (AnimeEntry animeEntry : animeEntries) {
            animeEntriesList.add(animeEntry.getName());
        }
        return animeEntriesList;
    }

    private static void updateLocalData() {
        if (localPath.isEmpty())
            return;
        FileHandler.readFolders(localPath);
        animeMap = FileHandler.getAnimeMap();
        animeNames = FileHandler.getAnimeNames();
        syncDatabaseData();
    }
}

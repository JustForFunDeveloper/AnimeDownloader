package tapsi.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {

    private static String feedPath = "";
    private static List<String> localPaths;

    private static Map<String, Anime> animeMap = new HashMap<>();
    private static List<String> animeNames = new ArrayList<>();

    private static List<AnimeEntry> feedEntries = new ArrayList<>();

    private static DBHandler dbHandler = new DBHandler();

    protected static void loadPaths() {
        List<String> paths = dbHandler.readAllPaths();
        feedPath = paths.get(0);
        localPaths = new ArrayList<>();
        int iter = 0;
        for (String path : paths) {
            if (iter > 0)
                localPaths.add(path);
            iter++;
        }
    }

    protected static List<String> getLocalPaths() {
        loadPaths();
        return localPaths;
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

    protected static void setPaths(String feedPath, List<String> localPaths) {
        DataHandler.localPaths = new ArrayList<>(localPaths);
        DataHandler.feedPath = feedPath;
        dbHandler.insertPath(0, feedPath);
        int iter = 1;
        for (String paths : localPaths) {
            dbHandler.insertPath(iter, paths);
            iter++;
        }
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
        if (localPaths.size() == 0)
            return;
        FileHandler.readFolders(localPaths);
        animeMap = FileHandler.getAnimeMap();
        animeNames = FileHandler.getAnimeNames();
        syncDatabaseData();
    }
}

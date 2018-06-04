package tapsi.logic.handler;

import tapsi.exception.MyException;
import tapsi.logic.container.Anime;
import tapsi.logic.container.AnimeEntry;
import tapsi.logic.container.AnimeScope;
import tapsi.logic.container.AnimeStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The {@link DataHandler} class manages all data which was collected from the {@link FileHandler} and {@link FeedHandler}.
 * The class implements also the {@link DBHandler} and is only available through the {@link DataInterface}.
 */
public class DataHandler {

    /**
     * The {@link String} feedPath instance represents the stored path to the feed.
     */
    private static String feedPath = "";
    /**
     * The {@link List<String>} localPaths instance represents all given local paths which holds anime.
     */
    private static List<String> localPaths;
    /**
     * The {@link Map<String, Anime>} animeMap instance holds all {@link Anime} objects which where found in the given paths.
     */
    private static Map<String, Anime> animeMap = new HashMap<>();
    /**
     * The {@link List<String>} animeNames instance holds all anime names.
     */
    private static List<String> animeNames = new ArrayList<>();
    /**
     * The {@link List<AnimeEntry>} feedEntries instance holds all feed entries at the time.
     */
    private static List<AnimeEntry> feedEntries = new ArrayList<>();
    /**
     * The {@link DBHandler} dbHandler instance to get and set entries to the database.
     */
    private static DBHandler dbHandler;

    static {
        try {
            dbHandler = new DBHandler();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the paths from the database if exists.
     */
    protected static void loadPaths() {
        List<String> paths = dbHandler.readAllPaths();
        if (paths.size() == 0)
            return;
        feedPath = new String(paths.get(0));
        localPaths = new ArrayList<>();
        int iter = 0;
        for (String path : paths) {
            if (iter > 0)
                localPaths.add(path);
            iter++;
        }
    }

    /**
     * @return the {@link String} local path
     */
    protected static List<String> getLocalPaths() {
        return localPaths;
    }

    /**
     * @return the {@link String} feed path
     */
    protected static String getFeedPath() {
        return feedPath;
    }

    public static Map<String, Anime> getAnimeMap() {
        return animeMap;
    }

    /**
     * @return the {@link String} anime names
     */
    protected static List<String> getLocalAnimeNames() {
        updateLocalData();
        return animeNames;
    }

    protected static List<String> getFeedAnimeNames() {
        updateLocalData();
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
        AnimeEntry currentEntry = null;

        for (AnimeEntry entry : feedEntries) {
            if (entry.getName().equals(animeName))
                currentEntry = entry;
        }

        if (currentEntry == null)
            return;

        FeedHandler.openLink(currentEntry.getMagnetUrl());

        Locale locale = Locale.getDefault();
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        String currentDate = calendar.getTime().toString();

        Anime localAnime = getAnimeByName(animeName);
        if (localAnime != null) {
            List<AnimeEntry> entries = localAnime.getAnimeEntries();

            for (AnimeEntry entry : entries) {
                if (entry.getNumber().equals(currentEntry.getNumber())) {
                    entry.setDownloadDate(currentDate);
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
                    try {
                        cal.setTime(sdf.parse(currentDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (localAnime.getNewestEntry() == null)
                        localAnime.setNewestEntry(cal);
                    else {
                        if (cal.after(localAnime.getNewestEntry())) ;
                        localAnime.setNewestEntry(cal);
                    }

                    if (localAnime.getOldestEntry() == null)
                        localAnime.setOldestEntry(cal);
                    else {
                        if (cal.before(localAnime.getOldestEntry()))
                            localAnime.setOldestEntry(cal);
                    }
                }
            }
        }

        try {
            dbHandler.insertEntry(currentEntry.getName(), currentEntry.getNumber(), currentDate);
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    protected static List<String> toStringListWithNumber(List<AnimeEntry> animeEntries) {
        List<String> animeEntriesList = new ArrayList<>();
        for (AnimeEntry animeEntry : animeEntries) {
            animeEntriesList.add(animeEntry.getFileName());
        }
        return animeEntriesList;
    }

    protected static void closeApplication() {
        try {
            dbHandler.closeDB();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    protected static void syncDatabaseData() {
        List<Anime> dbAnime = dbHandler.readAllAnime();

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

        List<List<String>> entries = dbHandler.readAllEntries();

        if (entries == null)
            return;

        for (List<String> entry : entries) {
            Anime localAnime = getAnimeByName(entry.get(0));
            if (localAnime != null) {
                List<AnimeEntry> animeEntries = localAnime.getAnimeEntries();
                for (AnimeEntry animeEntry : animeEntries) {
                    if (animeEntry.getNumber().equals(String.valueOf(entry.get(1)))) {
                        animeEntry.setDownloadDate(entry.get(2));
                    }
                }
            } else {
                try {
                    dbHandler.deleteEntry(entry.get(0), entry.get(1));
                } catch (MyException e) {
                    e.printStackTrace();
                }
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
        loadPaths();
        FileHandler.readFolders(localPaths);
        animeMap = FileHandler.getAnimeMap();
        animeNames = FileHandler.getAnimeNames();
        syncDatabaseData();
    }
}

package tapsi.logic.handler;

import tapsi.logic.container.Anime;
import tapsi.logic.container.AnimeEntry;

import java.util.List;
import java.util.Map;

/**
 * {@link DataInterface} handles all access from the views to the logic
 */
public interface DataInterface {

    static List<String> getLocalPaths() {
        return DataHandler.getLocalPaths();
    }

    static String getFeedPath() {
        return DataHandler.getFeedPath();
    }

    static Map<String, Anime> getAnimeMap() {
        return DataHandler.getAnimeMap();
    }

    /**
     * Gets the names of all local stored Animes
     *
     * @return List<String> returns all local AnimeNames
     */
    static List<String> getLocalAnimeNames() {
        return DataHandler.getLocalAnimeNames();
    }

    /**
     * Returns the names of the newest anime feed.
     *
     * @return List<String> returns the list of the feed
     */
    static List<String> getFeedAnimeNames() {
        return DataHandler.getFeedAnimeNames();
    }

    static List<AnimeEntry> getFeedEntries() {
        return DataHandler.getFeedEntries();
    }

    static List<String> getNewAnimeNames() {
        return DataHandler.getNewAnimeNames();
    }

    /**
     * Returns the Anime with the given anime
     *
     * @param anime Name of the anime
     * @return (@ Link Anime) returns the anime with the selected name
     */
    static Anime getAnimeByName(String anime) {
        return DataHandler.getAnimeByName(anime);
    }

    static AnimeEntry getFeedEntryByNameAndNumber(String entryName, String number) {
        return DataHandler.getFeedEntryByNameAndNumber(entryName, number);
    }

    static List<AnimeEntry> getAutomaticDownloadFeeds() {
        return DataHandler.getAutomaticDownloadFeeds();
    }

    static void setPaths(String feedPath, List<String> localPath) {
        DataHandler.setPaths(feedPath, localPath);
    }

    static void setAnimeData(Anime anime) {
        DataHandler.setAnimeData(anime);
    }

    static void addTempAnime(Anime anime) {
        DataHandler.addTempAnime(anime);
    }

    static void startDownload(AnimeEntry downloadEntry) {
        DataHandler.startDownload(downloadEntry);
    }

    static void deleteAnime(String name) {
        DataHandler.deleteAnime(name);
    }

    static List<String> toStringListWithNumber(List<AnimeEntry> animeEntries) {
        return DataHandler.toStringListWithNumber(animeEntries);
    }

    static void closeApplication() {
        DataHandler.closeApplication();
    }
}

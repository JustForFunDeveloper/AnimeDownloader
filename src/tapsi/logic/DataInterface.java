package tapsi.logic;

import java.util.List;

/**
 * {@link DataInterface} handles all access from the views to the logic
 */
public interface DataInterface {

//    static List<String> getPaths() {
//        return DataHandler.getPaths();
//    }

    static String getLocalPath() {
        return DataHandler.getLocalPath();
    }

    static String getFeedPath() {
        return DataHandler.getFeedPath();
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

    /**
     * Returns the Anime with the given anime
     *
     * @param anime Name of the anime
     * @return (@Link Anime) returns the anime with the selected name
     */
    static Anime getAnimeByName(String anime) {
        return DataHandler.getAnimeByName(anime);
    }

    static AnimeEntry getFeedEntryByName (String entryName) {
        return DataHandler.getFeedEntryByName(entryName);
    }

    static List<String> getAutomaticDownloadFeeds () {
        return DataHandler.getAutomaticDownloadFeeds();
    }

    static void setPaths(String feedPath, String localPath) {
        DataHandler.setPaths(feedPath, localPath);
    }

    static void setAnimeData (Anime anime) {
        DataHandler.setAnimeData(anime);
    }

    static void addTempAnime (Anime anime) {
        DataHandler.addTempAnime(anime);
    }

    static void startDownload (String animeName) {
        DataHandler.startDownload(animeName);
    }

    static void deleteAnime (String name) {
        DataHandler.deleteAnime(name);
    }

    static List<String> toStringListWithNumber (List<AnimeEntry> animeEntries) {
        return DataHandler.toStringListWithNumber(animeEntries);
    }

    static void closeApplication () {
        DataHandler.closeApplication();
    }
}

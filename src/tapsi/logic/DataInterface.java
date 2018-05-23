package tapsi.logic;

import java.util.ArrayList;
import java.util.List;

//TODO: Finish comments

/**
 * {@link DataInterface} handles all access from the views to the logic
 */
public interface DataInterface {

    /**
     * Gets the names of all local stored Animes
     *
     * @return List<String> returns all local AnimeNames
     */
    static List<String> getLocalAnimeNames() {
        return DataHandler.getLocalAnimeNames();
    }

    /**
     * Gets thh whole statistic for the local anime data
     * list(0)(0): Count of anime's which have a scope of {@link AnimeScope}.IGNORE
     * list(0)(1): Count of anime's which have a scope of {@link AnimeScope}.MUSTHAVE
     * list(0)(2): Count of anime's which have a scope of {@link AnimeScope}.NOTDEFINED
     * <p>
     * list(1)(0): Count of anime's which have a status of {@link AnimeStatus}.FINISHED
     * list(1)(1): Count of anime's which have a status of {@link AnimeStatus}.UNFINISHED
     * list(1)(2): Count of anime's which have a status of {@link AnimeStatus}.ONAIR
     * list(1)(3): Count of anime's which have a status of {@link AnimeStatus}.INFOMISSING
     *
     * @return List<List   <   String>> returns the statistic of every status and scope
     */
    static List<List<String>> getAnimeStatistics() {
        return DataHandler.getAnimeStatistics();
    }

    /**
     * Returns a list of all anime's with the given scope
     *
     * @param scope the given {@link AnimeScope}
     * @return List<String> returns the list filtered through the scope
     */
    static  List<String> getAnimesWithScope(AnimeScope scope){
        return DataHandler.getAnimesWithScope(scope);
    }

    /**
     * Returns a list of the all anime's with the given status
     *
     * @param status the given {@link AnimeStatus}
     * @return List<String> returns the list filtered through the status
     */
    static List<String> getAnimesWithStatus(AnimeStatus status){
        return DataHandler.getAnimesWithStatus(status);
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
        //TODO: Test here is an issue
        return DataHandler.getAutomaticDownloadFeeds();
    }

    static void setAnimeData (Anime anime) {
        DataHandler.setAnimeData(anime);
    }

    static List<String> toStringListWithNumber (List<AnimeEntry> animeEntries) {
        return DataHandler.toStringListWithNumber(animeEntries);
    }

    static void closeApplication () {
        DataHandler.closeApplication();
    }
}

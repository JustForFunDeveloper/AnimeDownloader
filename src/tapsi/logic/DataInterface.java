package tapsi.logic;

import java.util.List;

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
    static List<String> getFeedAnimes() {
        return DataHandler.getFeedAnimes();
    }
}

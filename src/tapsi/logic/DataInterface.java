package tapsi.logic;

import java.util.List;

public interface DataInterface {
    /**
     * Gets the names of all local stored Animes
     *
     * @return List<String> returns all local AnimeNames
     */
    List<String> getLocalAnimeNames();

    /**
     *
     * Gets thh whole statistic for the local anime data
     *  list(1)(1): Count of Anime which have a scope of {@link AnimeScope}.IGNORE
     *  list(1)(2): Count of Anime which have a scope of {@link AnimeScope}.MUSTHAVE
     *  list(1)(3): Count of Anime which have a scope of {@link AnimeScope}.NOTDEFINED
     *
     *  list(2)(1): Count of Anime which have a scope of A.IGNORE
     *  list(2)(2): Count of Anime which have a scope of {@link AnimeScope}.MUSTHAVE
     *  list(2)(3): Count of Anime which have a scope of {@link AnimeScope}.NOTDEFINED
     *
     *
     * @return
     */
    List<List<String>> getAnimeStatistics();


}

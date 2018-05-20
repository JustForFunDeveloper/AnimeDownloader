package tapsi.logic;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

class Anime {

    private String name;
    private Integer localEpisodes;
    private List<AnimeEntry> animeEntries;
    private Integer seasonCount = null;

    private AnimeScope animeScope;
    private AnimeStatus animeStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLocalEpisodes() {
        return localEpisodes;
    }

    public void setLocalEpisodes(Integer localEpisodes) {
        this.localEpisodes = localEpisodes;
    }

    public List<AnimeEntry> getAnimeEntries() {
        return animeEntries;
    }

    public void setAnimeEntries(List<AnimeEntry> animeEntries) {
        this.animeEntries = animeEntries;
    }

    @NotNull
    public Integer getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(Integer seasonCount) {
        this.seasonCount = seasonCount;
        if (this.seasonCount != null && animeEntries != null && seasonCount > 0) {
            if (this.seasonCount == animeEntries.size())
                animeStatus = AnimeStatus.FINISHED;
            else if (this.seasonCount < animeEntries.size()) {
                if (animeScope == AnimeScope.IGNORE)
                    animeStatus = AnimeStatus.UNFINISHED;
                else if (animeScope == AnimeScope.MUSTHAVE)
                    animeStatus = AnimeStatus.ONAIR;
                else if (animeScope == AnimeScope.NOTDEFINED)
                    animeStatus = AnimeStatus.INFOMISSING;
            }
        }
    }

    public AnimeScope getAnimeScope() {
        return animeScope;
    }

    public void setAnimeScope(AnimeScope animeScope) {
        this.animeScope = animeScope;
    }

    public AnimeStatus getAnimeStatus() {
        return animeStatus;
    }

    public void setAnimeStatus(AnimeStatus animeStatus) {
        this.animeStatus = animeStatus;
    }

    protected Anime(String name) {
        this.name = name;
        this.animeEntries = new ArrayList<>();
        this.animeScope = AnimeScope.NOTDEFINED;
        this.animeStatus = AnimeStatus.INFOMISSING;
    }

    protected void addAnimeEpisode (String number, String fileName) {
        AnimeEntry entry = new AnimeEntry();
        entry.addAnimeEntry(name, number, fileName);
        animeEntries.add(entry);
        updateLocalEpisodes();
    }

    private void updateLocalEpisodes() {
        localEpisodes = animeEntries.size();
    }
}

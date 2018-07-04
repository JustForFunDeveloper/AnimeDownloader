package tapsi.logic.container;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Anime {

    private String name;
    private Integer localEpisodes;
    private List<AnimeEntry> animeEntries;
    private Integer seasonCount = null;

    private AnimeScope animeScope;
    private AnimeStatus animeStatus;
    private Calendar newestEntry = null;
    private Calendar oldestEntry = null;

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

    public Calendar getNewestEntry() {
        return newestEntry;
    }

    public void setNewestEntry(Calendar newestEntry) {
        this.newestEntry = newestEntry;
    }

    public Calendar getOldestEntry() {
        return oldestEntry;
    }

    public void setOldestEntry(Calendar oldestEntry) {
        this.oldestEntry = oldestEntry;
    }

    @NotNull
    public Integer getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(Integer seasonCount) {
        if (seasonCount.equals(0)) {
            this.seasonCount = null;
            animeStatus = AnimeStatus.INFOMISSING;
        }
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

    public Anime(String name) {
        this.name = name;
        this.animeEntries = new ArrayList<>();
        this.animeScope = AnimeScope.NOTDEFINED;
        this.animeStatus = AnimeStatus.INFOMISSING;
        this.seasonCount = 0;
    }

    public void addAnimeEpisode(String number, String fileName, String localPath) {
        AnimeEntry entry = new AnimeEntry();
        entry.addAnimeEntry(name, number, fileName, localPath);
        animeEntries.add(entry);
        updateLocalEpisodes();
    }

    public boolean containsAnimeEntryByFileName(String fileName) {
        for (AnimeEntry animeEntry : animeEntries) {
            if (animeEntry.getFileName().equals(fileName))
                return true;
        }
        return false;
    }

    public AnimeEntry getAnimeEntryByFileName(String fileName) {
        for (AnimeEntry animeEntry : animeEntries) {
            if (animeEntry.getFileName().equals(fileName))
                return animeEntry;
        }
        return null;
    }

    private void updateLocalEpisodes() {
        localEpisodes = animeEntries.size();
    }

    @Override
    public String toString() {
        return "Anime{" +
                "animeEntries=" + animeEntries +
                '}';
    }
}

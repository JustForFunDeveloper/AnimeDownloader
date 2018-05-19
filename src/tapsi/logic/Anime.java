package tapsi.logic;

import java.util.ArrayList;
import java.util.List;

public class Anime {

    private String name;
    private Integer localEpisodes;
    private List<AnimeEntry> animeEntries;

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

    public Anime(String name) {
        this.name = name;
        this.animeEntries = new ArrayList<>();
    }

    public void addAnimeEpisode (String number, String fileName) {
        AnimeEntry entry = new AnimeEntry();
        entry.addAnimeEntry(name, number, fileName);
        animeEntries.add(entry);
        updateLocalEpisodes();
    }

    private void updateLocalEpisodes() {
        localEpisodes = animeEntries.size();
    }
}

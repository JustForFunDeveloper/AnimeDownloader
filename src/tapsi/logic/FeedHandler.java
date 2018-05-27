package tapsi.logic;

import com.sun.istack.internal.NotNull;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import javafx.util.Pair;

import java.awt.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class FeedHandler {

    private static final String TITLE = "SyndEntryImpl.title";
    private static final String MAGNETURL = "SyndEntryImpl.link";

    //private static String path = "http://horriblesubs.info/rss.php?res=720";

    protected static boolean openLink(String url) {
        URI uri = URI.create(url);
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected static List<AnimeEntry> downloadFile(String path) {
        List<Object> entries = new ArrayList<>();
        System.setProperty("http.agent", "Chrome");
        SyndFeed feed = null;
        try {
            URL feedUrl = new URL(path);
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(feedUrl));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }

        if (feed.getEntries() != null && feed.getEntries().size() >0 ) {
            entries = feed.getEntries();
        }
        return createAnimeListFromFeed(entries);
    }

    private static List<AnimeEntry> createAnimeListFromFeed(List<Object> entries) {
        List<AnimeEntry> newEpisodes = new ArrayList<>();
        for (Object entry1 : entries) {
            String entry = entry1.toString();

            Pair<Integer, Integer> position = getLineFromEntry(TITLE, entry);
            String name = entry.substring(position.getKey(), position.getValue());
            Pair<String, String> value = safeNameNumberInList(name);

            position = getLineFromEntry(MAGNETURL, entry);
            String url = entry.substring(position.getKey(), position.getValue());
            String magnetLink = safeUrlInList(url);

            AnimeEntry animeEntry = new AnimeEntry();
            animeEntry.addNewAnimeEntry(value.getKey(), value.getValue(), magnetLink, null);
            newEpisodes.add(animeEntry);
        }
        return newEpisodes;
    }

    @NotNull
    private static Pair<Integer, Integer> getLineFromEntry (String signature, String entry) {
        int start = entry.indexOf(signature);
        int end = start;
        int iter = entry.indexOf(signature);
        while (iter < entry.length()) {
            if (entry.charAt(iter) != '\n') {
                iter++;
            } else {
                end = iter;
                break;
            }
        }
        return new Pair<>(start, end);
    }

    @NotNull
    private static Pair<String, String> safeNameNumberInList(String value) {
        String name = value.replace("SyndEntryImpl.title=[HorribleSubs] ", "");
        String number = name;
        name = name.replaceAll(" - [0-9][0-9]* \\[720p].mkv", "");
        name = name.replaceAll("'","");
        number = number.replace(name, "");
        number = number.replace("- ", "");
        number = number.replace("[720p].mkv", "");
        number = number.replaceAll("\\s","");

        return new Pair<>(name, number);
    }

    private static String safeUrlInList(String value) {
        return value.replace(MAGNETURL + "=", "");
    }
}
package tapsi.logic;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static String path = "http://horriblesubs.info/rss.php?res=720";
    private static List<Object> entries;

    public static List<Object> getEntries() {
        return entries;
    }

    public static void downloadFile() {
        boolean ok = false;
        System.setProperty("http.agent", "Chrome");
        SyndFeed feed = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL feedUrl = new URL(path);

            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(feedUrl));

            //System.out.println(feed.getTitleEx());

            ok = true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: "+ex.getMessage());
        }

        if (!ok) {
            System.out.println();
            System.out.println("FeedReader reads and prints any RSS/Atom feed type.");
            System.out.println("The first parameter must be the URL of the feed to read.");
            System.out.println();
        }
        entries = new ArrayList<>();
        entries = feed.getEntries();
        getAnimeListFromFeed();
    }

    private static void getAnimeListFromFeed () {

        for (int listIter = 0; listIter < entries.size(); listIter++) {
            String entry = entries.get(listIter).toString();
            String substring = "";
            int start = entry.indexOf("SyndEntryImpl.title");
            int end = start;
            int iter = entry.indexOf("SyndEntryImpl.title");
            while (iter < entry.length()) {
                if (entry.charAt(iter) != '\n') {
                    iter++;
                } else {
                    end = iter;
                    break;
                }
            }

            substring = entry.substring(start, end);
            System.out.println("Substring: " + substring);
            getName(substring);
        }
    }

    private static void getName (String value) {
        String returnValue = value.replace("SyndEntryImpl.title=[HorribleSubs] ","");
        returnValue = returnValue.replaceAll(" - [0-9][0-9]* \\[720p\\].mkv","");
        System.out.println("Regex: " + returnValue);
    }

}
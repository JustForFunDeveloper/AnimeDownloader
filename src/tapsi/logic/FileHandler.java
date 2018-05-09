package tapsi.logic;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URL;
import java.util.List;

public class FileHandler {

    private static String path = "http://horriblesubs.info/rss.php?res=720";

    public static String downloadFile() {
        boolean ok = false;
        System.setProperty("http.agent", "Chrome");
        SyndFeed feed = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL feedUrl = new URL(path);

            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(feedUrl));

            System.out.println(feed.getTitleEx());

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


        return feed.getEntries().get(4).toString();
    }
}
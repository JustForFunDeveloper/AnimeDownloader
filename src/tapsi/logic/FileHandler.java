package tapsi.logic;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static String path = "http://horriblesubs.info/rss.php?res=720";
    private static List<Object> entries;

    public static List<Object> getEntries() {
        return entries;
    }

    public static void downloadFile() {
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
        entries = new ArrayList<>();
        entries = feed.getEntries();
        getAnimeListFromFeed();
    }

    private static void getAnimeListFromFeed() {

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
            getName(substring);
        }
//        String url = "magnet:?xt=urn:btih:ZOPSEULF4I6V3PRPQTDO3NM2ZDTITSOT&amp;tr=http://nyaa.tracker.wf:7777/announce&amp;tr=udp://tracker.coppersurfer.tk:6969/announce&amp;tr=udp://tracker.internetwarriors.net:1337/announce&amp;tr=udp://tracker.leechersparadise.org:6969/announce&amp;tr=udp://tracker.opentrackr.org:1337/announce&amp;tr=udp://open.stealth.si:80/announce&amp;tr=udp://p4p.arenabg.com:1337/announce&amp;tr=udp://mgtracker.org:6969/announce&amp;tr=udp://tracker.tiny-vps.com:6969/announce&amp;tr=udp://peerfect.org:6969/announce&amp;tr=http://share.camoe.cn:8080/announce&amp;tr=http://t.nyaatracker.com:80/announce&amp;tr=https://open.kickasstracker.com:443/announce";
//        URI myUri = URI.create(url);
//        openWebpage(myUri);
//
//        String url2 = "magnet:?xt=urn:btih:T4A5GFATJ45XY6FWKKJHYHWYUDTYLOSR&tr=http://nyaa.tracker.wf:7777/announce&tr=udp://tracker.coppersurfer.tk:6969/announce&tr=udp://tracker.internetwarriors.net:1337/announce&tr=udp://tracker.leechersparadise.org:6969/announce&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://open.stealth.si:80/announce&tr=udp://p4p.arenabg.com:1337/announce&tr=udp://mgtracker.org:6969/announce&tr=udp://tracker.tiny-vps.com:6969/announce&tr=udp://peerfect.org:6969/announce&tr=http://share.camoe.cn:8080/announce&tr=http://t.nyaatracker.com:80/announce&tr=https://open.kickasstracker.com:443/announce";
//        URI myUri2 = URI.create(url2);
//        openWebpage(myUri2);

        //System.out.println(getItem("http://localhost:5555/gui/?token=chwQSn0NSXSlNHlALmMlW47Dn_ZjcyovgD-bWNfvOKc-YtV4gUrBfXA29FoAAAAA&list=1"));
    }

    private static void getName(String value) {
        String name = value.replace("SyndEntryImpl.title=[HorribleSubs] ", "");
        String number = name;
        name = name.replaceAll(" - [0-9][0-9]* \\[720p\\].mkv", "");
        number = number.replace(name, "");
        number = number.replace("- ", "");
        number = number.replace("[720p].mkv", "");

        //System.out.println("Name: " + name);
        //System.out.println("Number: " + number);
    }

    public static boolean openWebpage(URI uri) {
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

    public static String getItem(String item) {
        String url = item;
        String value = "";

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setRequestProperty("User-Agent", "Chrome/66.0.3359.139");

        int responseCode = 0;
        try {
            responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            value = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
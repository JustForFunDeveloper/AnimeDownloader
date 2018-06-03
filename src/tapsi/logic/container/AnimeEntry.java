package tapsi.logic.container;

public class AnimeEntry {

    private String name;
    private String number;
    private String magnetUrl;
    private String fileName;
    private String path;
    private String downloadDate = null;

    public void addNewAnimeEntry(String name, String number, String magnetUrl, String path) {
        this.name = name;
        this.number = number;
        this.magnetUrl = magnetUrl;
        this.path = path;
    }

    public void addAnimeEntry(String name, String number, String fileName, String path) {
        this.name = name;
        this.number = number;
        this.fileName = fileName;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMagnetUrl() {
        return magnetUrl;
    }

    public void setMagnetUrl(String magnetUrl) {
        this.magnetUrl = magnetUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(String downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getFullPathName() {
        return new String(path);
    }

    @Override
    public String toString() {
        return "AnimeEntry{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                "}\n";
    }
}

package tapsi.logic;

public class AnimeEntry {

    private String name;
    private String number;
    private String magnetUrl;
    private String fileName;

    public AnimeEntry() {
    }

    public void addNewAnimeEntry(String name, String number, String magnetUrl) {
        this.name = name;
        this.number = number;
        this.magnetUrl = magnetUrl;
    }

    public void addAnimeEntry(String name, String number, String fileName) {
        this.name = name;
        this.number = number;
        this.fileName = fileName;
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

    @Override
    public String toString() {
        return "AnimeEntry{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                "}\n";
    }
}

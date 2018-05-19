package tapsi.logic;

public class AnimeEntry {

    private String name;
    private String number;
    private String magnetUrl;

    public AnimeEntry(String name, String number, String magnetUrl) {
        this.name = name;
        this.number = number;
        this.magnetUrl = magnetUrl;
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

    @Override
    public String toString() {
        return "AnimeEntry{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                "}\n";
    }
}

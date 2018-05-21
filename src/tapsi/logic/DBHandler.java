package tapsi.logic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    private String dbUrl = "jdbc:sqlite:my.db";
    private Connection c = null;
    private Statement stmt = null;


    public DBHandler() {

        // Connect to database or create if no existent
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(dbUrl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create table if not existent
        try {
            stmt = c.createStatement();
            String sql = "create table if not exists Anime " +
                    "(name TEXT PRIMARY KEY UNIQUE, " +
                    "animeScope TEXT NOT NULL, animeStatus TEXT NOT NULL, seasonCount INTEGER DEFAULT 0)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertClient(String name, String animeScope, String animeStatus, int seasonCount) {

        boolean checkName = checkAnimeByName(name);
        //boolean checkName = checkClientByName(name);

        if (!checkName) {
            String sql = "insert into Anime(name, animeScope, animeStatus, seasonCount)" +
                    " select '" + name + "', '" + animeScope + "', '" + animeStatus + "', " + seasonCount;
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // If insert is not possible just try tp update the client
            updateClient(name, animeScope, animeStatus, seasonCount);
        }
    }

    private void updateClient(String name, String animeScope, String animeStatus, int seasonCount) {
        String sqlName = "update Anime set animeScope = '" + animeScope + "', animeStatus = '" + animeStatus + "', seasonCount = " + seasonCount + " where name = '" + name + "'";
        try {
            stmt.executeUpdate(sqlName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAnimeByName(String name) {
        String sql = "select count(*) from Anime where name = '" + name + "'";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int result = rs.getInt(1);
            rs.close();

            if (result == 1)
                return true;
            else if (result != 0) {
                System.out.println("Invalid Database! row:[name]");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Anime> readAllObjects() {
        List<Anime> animes = new ArrayList<>();
        String sql = "select * from Clients";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Anime anime = new Anime(rs.getString(1));
                anime.setAnimeScope(AnimeScope.valueOf(rs.getString(2)));
                anime.setAnimeStatus(AnimeStatus.valueOf(rs.getString(3)));
                anime.setSeasonCount(rs.getInt(4));
                animes.add(anime);
            }
            rs.close();
            return animes;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeDB() {
        try {
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

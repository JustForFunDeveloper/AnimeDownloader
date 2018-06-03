package tapsi.logic.handler;

import tapsi.exception.MyException;
import tapsi.logic.container.Anime;
import tapsi.logic.container.AnimeScope;
import tapsi.logic.container.AnimeStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link DBHandler} class handles all database related functions.<br />
 * At the moment the db saves only the following information for the application: <br />
 * <br />
 * <strong>Table Anime:</strong> Name, {@link AnimeScope}, {@link AnimeStatus}<br />
 * <strong>Table Paths:</strong> One FeedPath and multiple LocalPaths<br />
 * <strong>Table Entries:</strong> Name, Episode, Date (Date since the Download happened through the application)<br />
 */
public class DBHandler {

    private String dbUrl = "jdbc:sqlite:my.db";
    private Connection c = null;
    private Statement stmt = null;

    protected DBHandler() throws MyException {

        // Connect to database or create if no existent
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(dbUrl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new MyException("ClassNotFoundException");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("SQLException");
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
            throw new MyException("SQLException");
        }

        try {
            stmt = c.createStatement();
            String sql = "create table if not exists Paths " +
                    "(id INTEGER PRIMARY KEY UNIQUE, " +
                    "path TEXT NOT NULL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("SQLException");
        }

        try {
            stmt = c.createStatement();
            String sql = "create table if not exists Entries " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                    "name TEXT NOT NULL, episode INTEGER NOT NULL, date TEXT NOT NULL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("SQLException");
        }
    }

    protected void insertAnime(String name, String animeScope, String animeStatus, int seasonCount) {

        boolean checkName = checkAnimeByName(name);

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
            updateAnime(name, animeScope, animeStatus, seasonCount);
        }
    }

    protected void deleteAnime(String name) {

        String sql = "delete from Anime where " + " name = '" + name + "'";
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void updateAnime(String name, String animeScope, String animeStatus, int seasonCount) {
        String sqlName = "update Anime set animeScope = '" + animeScope + "', animeStatus = '" + animeStatus + "', seasonCount = " + seasonCount + " where name = '" + name + "'";
        try {
            stmt.executeUpdate(sqlName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void insertPath(Integer id, String path) {

        boolean checkName = checkPathByID(id);

        if (!checkName) {
            String sql = "insert into Paths(id, path)" +
                    " select " + id + ", '" + path + "'";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // If insert is not possible just try tp update the client
            updatePath(id, path);
        }
    }

    protected void updatePath(Integer id, String path) {
        String sqlName = "update Paths set path = '" + path + "' where id = " + id ;
        try {
            stmt.executeUpdate(sqlName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void insertEntry(String name, Integer episode, String date) throws MyException {

        boolean checkName = checkEntry(name, episode);

        if (!checkName) {
            String sql = "insert into Entries(name, episode, date)" +
                    " select '" + name + "', " + episode + ", '" + date + "'";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MyException("SQLException");
            }
        } else {
            // If insert is not possible just try tp update the client
            updateEntry(name, episode, date);
        }
    }

    protected void updateEntry(String name, Integer episode, String date) throws MyException {
        String sqlName = "update Entries set date = '" + date + "' where name = '" + name + "' AND episode =" + episode ;
        try {
            stmt.executeUpdate(sqlName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("SQLException");
        }
    }

    protected boolean checkAnimeByName(String name) {
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

    protected boolean checkPathByID(Integer number) {
        String sql = "select count(*) from Paths where id = '" + number + "'";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int result = rs.getInt(1);
            rs.close();

            if (result == 1)
                return true;
            else if (result != 0) {
                System.out.println("Invalid Path Database!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean checkEntry(String name, Integer episode) throws MyException {
        String sql = "select count(*) from Entries where name = '" + name + "' AND episode = " + episode;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int result = rs.getInt(1);
            rs.close();

            if (result == 1)
                return true;
            else if (result != 0) {
                System.out.println("Invalid Entry Database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("SQLException");
        }
        return false;
    }

    protected List<Anime> readAllObjects() {
        List<Anime> animes = new ArrayList<>();
        String sql = "select * from Anime";
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

    protected List<String> readAllPaths() {
        List<String> paths = new ArrayList<>();
        String sql = "select * from Paths";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                paths.add(rs.getString(2));
            }
            rs.close();
            return paths;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected List<List<String>> readAllEntries() {
        List<List<String>> entries = new ArrayList<>();
        String sql = "select * from Entries";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                List<String> line = new ArrayList<>();
                line.add(rs.getString(2));
                line.add(String.valueOf(rs.getInt(3)));
                line.add(rs.getString(4));
                entries.add(line);
            }
            rs.close();
            return entries;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void deleteTable(String name) throws MyException{

        String sql = "drop table " + name;
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("Couldn't drop table: " + name);
        }
    }

    protected void deleteAllEntriesFromTable(String name) throws MyException{

        String sql = "delete from " + name;
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("Couldn't empty table: " + name);
        }
    }

    protected void closeDB() throws MyException{
        try {
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MyException("Couldn't close DB");
        }
    }
}

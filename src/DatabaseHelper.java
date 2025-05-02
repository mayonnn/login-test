import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;
import java.security.MessageDigest;

public class DatabaseHelper {
    private String url;

    public DatabaseHelper(String dbPath){
        this.url = "jdbc:sqlite:" + dbPath;
    }

    public void setupDB(){
        String createTable = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR,
                password_hash VARCHAR
            );
        """;
        try(var conn = DriverManager.getConnection(url)){
            var stmt = conn.createStatement();

            if (conn != null){
                stmt.execute(createTable);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean addUser(String username, String password){
        String insertUser = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try(var conn = DriverManager.getConnection(url)){
            var stmt = conn.prepareStatement(insertUser);

            stmt.setString(1, username);
            stmt.setString(2, hash(password));

            int i = stmt.executeUpdate();
            if(i > 0){
                return true;
            } else {
                System.out.println("failed to add user");
                return false;
            }
        } catch (SQLException e){
            System.out.println("errir adding user: " + e.getMessage());
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(String username){
        String count = "SELECT COUNT(*) FROM users WHERE username = ?";
        try(var conn = DriverManager.getConnection(url)){
            var stmt = conn.prepareStatement(count);
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()){
                if (rs.next()) {
                    int c = rs.getInt(1);
                    return c > 0;
                }
            }
        } catch (SQLException e){
            System.out.println("errir checking user: " + e.getMessage());
            return false;
        }
        return false;
    }

    public boolean isValidCredentials(String username, String password){
        if(!contains(username)) return false;
        String count = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try(var conn = DriverManager.getConnection(url)){
            var stmt = conn.prepareStatement(count);
            stmt.setString(1, username);
            stmt.setString(2, hash(password));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e){
            System.out.println("errir checking user: " + e.getMessage());
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA256");
        md.update(password.getBytes());
        byte[] digest = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b)); // uppercase hex
        }
        return sb.toString();
    }
}

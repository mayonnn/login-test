import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        FileReader reader = new FileReader("src\\config.json");
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        String path = json.get("dbPath").getAsString();
        DatabaseHelper db = new DatabaseHelper(path);
        db.setupDB();

        Scanner ms = new Scanner(System.in);
        System.out.println("create new user? y/n");
        String createNewUser = ms.nextLine();

        if (createNewUser.equals("y")) {
            boolean validUserName = false;
            while (!validUserName){
                System.out.println("username:");
                String username = ms.nextLine();

                if (db.contains(username)) {
                    System.out.println("username already taken");
                } else {
                    System.out.println("password:");
                    String password = ms.nextLine();
                    db.addUser(username, password);
                    validUserName = true;
                }
            }
        }

        System.out.println("\nlogin:");
        System.out.println("whats ur username:");
        String username = ms.nextLine();
        System.out.println("passwrd:");
        String password = ms.nextLine();

        if (db.isValidCredentials(username, password)){
            System.out.println("yayy");
        } else {
            System.out.println("passwrd or username wron");
        }


    }
}
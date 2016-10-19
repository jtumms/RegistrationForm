package com.company;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws SQLException{
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        Spark.externalStaticFileLocation("public");
        Spark.init();
        createTables(conn);

        Spark.get(
                "/get-users",
                ((request, response) -> {
                    ArrayList<User> users = selectUser(conn);
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(users);
                }));
        Spark.post(
                "/add-user",
                ((request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    User user = p.parse(body, User.class);
                    insertUser(conn, user.username, user.address, user.email);
                    return "";
                })
        );
//        Spark.put(
//                "/edit-user",
//                ((request, response) -> {
//                    String body = request.body();
//                    JsonParser p = new JsonParser();
//                    User user = p.parse(body, User.class);
//                    updateUser(conn, user.username, user.address, user.email);
//                    return "";
//                })
//        );

    }
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, address VARCHAR, email VARCHAR)");
    }
    public static void insertUser(Connection conn, String username, String address, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, address);
        stmt.setString(3, email);
        stmt.execute();
    }
    public static ArrayList<User> selectUser(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            Integer id = results.getInt("id");
            String username = results.getString("username");
            String address = results.getString("address");
            String email = results.getString("email");
            users.add(new User(id, username, address, email));
        }
        return users;
    }
    public static void updateUser() {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setInt(1, );
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            Integer id = results.getInt("id");
            String username = results.getString("username");
            String address = results.getString("address");
            String email = results.getString("email");
            users.add(new User(id, username, address, email));
        }
        return users;


    }
}

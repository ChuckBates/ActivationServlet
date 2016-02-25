package com.qbit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: cbates
 */
public class DatabaseConnection {
    private Connection connection;
    private Statement statement;
    private static final String USERNAME = "java";
    private static final String PASSWORD = "password";
    private static final String DB_URL = "jdbc:mysql://localhost/";

    public void createDatabase() throws SQLException, ClassNotFoundException {
        connectToDatabase();

        statement = getStatement();

        String createDbSql = "create database CustomerDb;";
        statement.executeUpdate(createDbSql);
        String useDbSql = "use CustomerDb;";
        statement.executeUpdate(useDbSql);
        String createCustomerTableSql = "CREATE TABLE Customers (" +
                "id int(11) unsigned NOT NULL AUTO_INCREMENT, " +
                "name varchar(256) NOT NULL DEFAULT '', " +
                "email varchar(256) NOT NULL DEFAULT '', " +
                "activated bool, " +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;";
        statement.executeUpdate(createCustomerTableSql);
        statement.close();
    }

    public void connectToDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }

    public boolean isDatabaseCreated() throws ClassNotFoundException {
        try {
            getStatement().executeUpdate("use CustomerDb");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insert(String name, String email, boolean activated) throws SQLException, ClassNotFoundException {
        String insertSql = "INSERT INTO CUSTOMERS (name, email, activated) " +
                "VALUES ('" + name + "', '" + email + "', " + (activated ? 1 : 0) + ");";
        getStatement().executeUpdate(insertSql);
    }

    public Statement getStatement() throws SQLException, ClassNotFoundException {
        if (statement == null || statement.isClosed()) {
            return getConnection().createStatement();
        }
        return statement;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || !connection.isValid(200)) {
            connectToDatabase();
        }
        return connection;
    }
}

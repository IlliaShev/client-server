package ua.clientserver.shevchyk.db_source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatasourceFactory {

    private static Connection connection;


    public static Connection getConnection(String fileName) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
        createProductTable();
        return connection;
    }

    public static boolean createProductTable() throws SQLException {
        String queryCreateTable = "CREATE TABLE IF NOT EXISTS products(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, PRODUCT_NAME TEXT NOT NULL," +
                "PRICE REAL NOT NULL, AMOUNT INTEGER NOT NULL, GROUP_NAME TEXT NOT NULL)";
        return connection.createStatement().executeUpdate(queryCreateTable) == 0;
    }

    public static boolean dropTable() throws SQLException {
        String dropTableQuery = "DROP TABLE IF EXISTS PRODUCTS";
        return connection.createStatement().executeUpdate(dropTableQuery) == 0;
    }
}

package reimbapp.utils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private Driver driver;
    private boolean driverReady;//TODO: is this necessary?

    private String url;
    private String username;
    private String password;

    public ConnectionManager(Driver driver, String url, String username, String password){
        this.driver = driver;
        driverReady = false;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void registerDriver() throws SQLException {
        if(!driverReady){
            DriverManager.registerDriver(this.driver);
        }
    }

    public Connection getConnection() throws SQLException {
        if(!driverReady){
            registerDriver();
        }
        return DriverManager.getConnection(url, username, password);
    }

}

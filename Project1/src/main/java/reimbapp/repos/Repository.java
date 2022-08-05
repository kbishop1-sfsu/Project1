package reimbapp.repos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface Repository <T, ID extends Serializable>{

    public Connection getConnection();

    public PreparedStatement init(Connection c, String sql);

    public ResultSet execQuery(Connection c, PreparedStatement p);

    public int execUpdate(Connection c, PreparedStatement p);

    public int insert (Connection c, T object);

    public int delete (Connection c, T object);

    public ID getID (Connection c, T object);

    public T getByID(Connection c, Integer id);

    public List<T> getAll(Connection c);

}


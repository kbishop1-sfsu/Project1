package reimbapp.repos;

import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository implements Repository<Employee, Integer> {
    private ConnectionManager connectionManager;

    public EmployeeRepository(ConnectionManager connManager){
        this.connectionManager = connManager;
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = connectionManager.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return connection;
    }

    @Override
    public PreparedStatement init(Connection c, String sql) {
        PreparedStatement pStmt = null;
        try{
            pStmt = c.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return pStmt;
    }

    @Override
    public ResultSet execQuery(Connection c, PreparedStatement p) {
        ResultSet result = null;
        try{
            result = p.executeQuery();
            p.close();
            c.commit();
        }catch (SQLException e){
            System.out.println("Exception thrown: " + e);
        }
        return result;
    }

    public int execUpdate(Connection c, PreparedStatement p){
        int result = 0;
        try{
            result = p.executeUpdate();
            p.close();
            c.commit();
        }catch(SQLException e){
            System.out.println("Exception thrown: "+ e);
        }
        return result;
    }

    @Override
    public int insert(Connection c, Employee emp) {
        PreparedStatement p = init(c, "insert into employees (firstname, lastname, email, username, password, is_manager) values (?,?,?,?,?,?)");
        int result = 0;

        try {
            p.setString(1, emp.getFirstName());
            p.setString(2, emp.getLastName());
            p.setString(3, emp.getEmail());
            p.setString(4, emp.getUsername());
            p.setString(5, emp.getPassword());
            p.setBoolean(6, emp.getClass().equals(Manager.class));
            result = execUpdate(c, p);

        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public int delete(Connection c, Employee emp) {
        //TODO: should return # rows updated
        PreparedStatement p = init(c, "delete from employees where email = ?");
        int result = 0;
        try {
            p.setString(1, emp.getEmail());
            result = execUpdate(c, p);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return result;
    }

    public int updateInfo(Connection c, Employee emp, String firstname, String lastname, String email, String username){
        PreparedStatement p = init(c, "update employees set firstname=?, lastname=?, email=?, username=? where password=?;");
        int result = 0;
        try{
            p.setString(1, firstname);
            p.setString(2, lastname);
            p.setString(3, email);
            p.setString(4, username);
            p.setString(5, emp.getPassword());
            result = execUpdate(c, p);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return result;
    }

    public int updatePassword(Connection c, Employee emp, String password){
        PreparedStatement p = init(c, "update employees set password=? where email=?");
        int result = 0;
        try{
            p.setString(2, emp.getEmail());
            p.setString(1, password);
            result = execUpdate(c, p);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Integer getID(Connection c, Employee emp) {
        PreparedStatement p = init(c, "select id from employees where email=?");
        int id = 0;
        ResultSet r = null;
        try{
            p.setString(1, emp.getEmail());
            r = p.executeQuery();
            //System.out.println("executing query");
            while(r.next()){
                id = r.getInt("id");
                //System.out.println("getting id");
            }
        }catch(SQLException e){
            System.out.println("Exception thrown: " + e);
        }
        return id;
    }

    @Override
    public Employee getByID(Connection c, Integer id){
        PreparedStatement p = init(c, "select * from employees where id=?");
        Employee emp = null;
        try{
            p.setInt(1, id);
            ResultSet r = p.executeQuery();
            while(r.next()){
                if(r.getBoolean("is_manager")){
                    emp = new Manager();
                }else{
                    emp = new Employee();
                }
                emp.setId(id);
                emp.setFirstName(r.getString("firstname"));
                emp.setLastName(r.getString("lastname"));
                emp.setEmail(r.getString("email"));
                emp.setUsername(r.getString("username"));
                emp.setPassword(r.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return emp;
    }

    public Employee getByUsername(Connection c, String username){
        PreparedStatement p = init(c, "select * from employees where username=?");
        Employee emp = null;
        try{
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            while(r.next()){
                if(r.getBoolean("is_manager")){
                    emp = new Manager();
                }else{
                    emp = new Employee();
                }
                emp.setId(r.getInt("id"));
                emp.setFirstName(r.getString("firstname"));
                emp.setLastName(r.getString("lastname"));
                emp.setEmail(r.getString("email"));
                emp.setUsername(r.getString("username"));
                emp.setPassword(r.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return emp;
    }


    @Override
    public List<Employee> getAll(Connection c) {
        PreparedStatement p = init(c, "select * from employees");
        List <Employee> all = new ArrayList<>();
        try{
            ResultSet r = p.executeQuery();
            while(r.next()){
                Employee emp = null;
                if(r.getBoolean("is_manager")){
                    emp = new Manager();
                }else{
                    emp = new Employee();
                }
                emp.setId(r.getInt("id"));
                emp.setFirstName(r.getString("firstname"));
                emp.setLastName(r.getString("lastname"));
                emp.setEmail(r.getString("email"));
                emp.setUsername(r.getString("username"));
                emp.setPassword(r.getString("password"));
                all.add(emp);
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return all;
    }
}


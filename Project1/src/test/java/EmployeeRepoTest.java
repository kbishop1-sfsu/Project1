import org.apache.logging.log4j.core.util.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.Driver;
import reimbapp.utils.ConnectionManager;
import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.repos.EmployeeRepository;
import reimbapp.repos.RequestRepository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class EmployeeRepoTest {
    private ConnectionManager connectionManager;
    private EmployeeRepository employeeRepo;
    private Employee emp1;
    private Employee emp2;

    @Before
    public void init(){
        connectionManager = new ConnectionManager(new Driver(),"jdbc:h2:mem:test_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
                "sa", "");

        employeeRepo = new EmployeeRepository(connectionManager);
        //emp = getEmployee();
        emp1 = new Employee("jennifer", "lopez", "jlopez@sfsu.edu", "jlopez", "ducksauce");
        emp2 = new Manager("johnny", "depp", "jdepp@gmail.com", "jdepp", "pirates");
    }

    @Before
    public void initDB() throws SQLException {
        //create connection
        Connection c = connectionManager.getConnection();

        //create employee table
        String sql = "create table if not exists employees(id serial primary key, firstname varchar(20) not null, lastname varchar(20) not null, email varchar(30) not null, username varchar(20) not null, password varchar(20) not null, is_manager boolean default false, unique(email), unique(username))";
        Statement s = c.createStatement();
        s.execute(sql);
    }

    public void insertEmployees() throws SQLException{
        Connection c = connectionManager.getConnection();
        //insert 2 employees
        String sql = "insert into employees(firstname, lastname, email, username, password, is_manager) values ('jenny', 'lopez', 'jlopez@sfsu.edu', 'jlopez', 'ducksauce', false)";
        Statement s = c.createStatement();
        s.execute(sql);

        sql = "insert into employees(firstname, lastname, email, username, password, is_manager) values ('johnny', 'depp', 'jdepp@gmail.com', 'jdepp', 'pirates', true)";
        s.execute(sql);
    }



    @Test
    public void testDBConnection() throws SQLException{
        Connection c = employeeRepo.getConnection();
        //insertEmployees();
    }

    @Test
    public void testGetID() throws SQLException{
        //insertEmployees();
        int id = employeeRepo.getID(employeeRepo.getConnection(), emp2);
        Assert.assertTrue("Employee id was not found", id>0);
    }

    @Test
    public void testGetByID() throws SQLException{
        //insertEmployees();
        Employee found = employeeRepo.getByID(employeeRepo.getConnection(), 2);
        Assert.assertTrue("Employee was not found by ID", found!=null);
    }

    @Test
    public void testGetAll() throws SQLException{
        //insertEmployees();
        employeeRepo.insert(employeeRepo.getConnection(), emp2);
        List<Employee> all = employeeRepo.getAll(employeeRepo.getConnection());
        //all.stream().forEach(r-> System.out.println(r));
        Assert.assertFalse("The list is empty", all.isEmpty());
    }

    @Test
    public void testInsertEmployee() throws SQLException{
        //insertEmployees();
        Employee emp3 = new Employee("samuel", "jackson", "sjackson@gmail.com", "sjackson", "popcorn");
        int result = employeeRepo.insert(employeeRepo.getConnection(), emp3);
        Assert.assertTrue("Employee table was not updated", result>0);
        Employee found = employeeRepo.getByID(employeeRepo.getConnection(), employeeRepo.getID(employeeRepo.getConnection(), emp3));
        Assert.assertTrue("Employee was not inserted", found!=null);
    }

    @Test
    public void testDeleteEmployee() throws SQLException{
        insertEmployees();
        //employeeRepo.insert(employeeRepo.getConnection(), emp);
        int result = employeeRepo.delete(employeeRepo.getConnection(), emp1);
        Assert.assertTrue("Employee table was not updated", result>0);
        Employee found = employeeRepo.getByID(employeeRepo.getConnection(), emp1.getId());
        Assert.assertTrue("Employee was not deleted", found==null);

    }

    @Test
    public void testUpdateEmployeeInfo() throws SQLException{
        //insertEmployees();
        //int empId = emp2.getId();
        //Assert.assertTrue("Original employee does not exist", empId>0);
        Employee origEmp = employeeRepo.getByID(employeeRepo.getConnection(), 2);
        Assert.assertFalse("original employee does not exist", origEmp==null);
        int result = employeeRepo.updateInfo(employeeRepo.getConnection(), origEmp, "jessica", "simpson", "jsimpson@gmail.com", "jsimpson");
        Assert.assertTrue("Employee table was not updated", result>0);
        Employee found = employeeRepo.getByID(employeeRepo.getConnection(), 2);
        Assert.assertFalse("Updated employee does not exist", found==null);
        Assert.assertFalse("Employee info was not updated", origEmp.equals(found));
    }

    @Test
    public void testUpdateEmployeePassword() throws SQLException{
        //insertEmployees();
        Employee origEmp = employeeRepo.getByID(employeeRepo.getConnection(), 2);
        Assert.assertFalse("original employee does not exist", origEmp==null);
        int result = employeeRepo.updatePassword(employeeRepo.getConnection(), origEmp, "chocolate");
        Assert.assertTrue("Employee table was not updated", result>0);
        Employee found = employeeRepo.getByID(employeeRepo.getConnection(), 2);
        Assert.assertFalse("Updated employee does not exist", found==null);
        Assert.assertFalse("Employee password was not updated", origEmp.equals(found));
    }





}

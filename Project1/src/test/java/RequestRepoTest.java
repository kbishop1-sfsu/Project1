import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.models.ReimbRequest;
import reimbapp.repos.EmployeeRepository;
import reimbapp.utils.ConnectionManager;
import reimbapp.repos.RequestRepository;

import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

import org.h2.Driver;


public class RequestRepoTest {
    private ConnectionManager connectionManager;
    private RequestRepository requestRepo;
    private ReimbRequest request1;
    private ReimbRequest request2;
    private EmployeeRepository employeeRepository;
    private Employee employee;
    private Employee manager;

    private SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Before
    public void init() throws ParseException {
        connectionManager = new ConnectionManager(new Driver(),"jdbc:h2:mem:test_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
                "sa", "");

        employeeRepository = new EmployeeRepository(connectionManager);
        employee = new Employee("jennifer", "lopez", "jlopez@gmail.com", "jlopez", "musically");
        employee.setId(1);
        manager = new Manager("samuel", "jackson", "sjackson@gmail.com", "sjackson", "popcorn");
        employee.setId(2);

        requestRepo = new RequestRepository(connectionManager);
        request1 = new ReimbRequest(employee, (float)35.76, formatter.parse("2022-07-24 00:00:00.000"), "pending", null, null, "for dinner on saturday");
        request2 = new ReimbRequest(employee, (float)245.69, formatter.parse("2021-03-13 00:00:00.000"), "pending", null, null, "for computer repairs");

    }

    @Before
    public void initDB() throws SQLException{
        Connection c = connectionManager.getConnection();
        String sql = "create table if not exists requests(id serial primary key, emp_id int not null, amount float default 0.0, created timestamp not null, status varchar(20) not null, resolved timestamp, man_id int, descr varchar(100) not null)";
        Statement s = c.createStatement();
        s.execute(sql);

        sql = "create table if not exists employees(id serial primary key, firstname varchar(20) not null, lastname varchar(20) not null, email varchar(30) not null, username varchar(20) not null, password varchar(20) not null, is_manager boolean default false, unique(email), unique(username))";
        s.execute(sql);
    }

    public void insertRequests() throws SQLException{
        Connection c = connectionManager.getConnection();
        //insert 2 requests
        String sql = "insert into requests (emp_id, amount, created, status, resolved, man_id, descr) values (1, 35.76, '2022-07-24 00:00:00.000', 'pending', null, null, 'for dinner on saturday')";
        Statement s = c.createStatement();
        s.execute(sql);

        sql = "insert into requests (emp_id, amount, created, status, resolved, man_id, descr) values (1, 245.69, '2021-03-13 00:00:00.000', 'pending', null, null, 'for computer repairs')";
        s.execute(sql);

        employeeRepository.insert(c, employee);
        employeeRepository.insert(c, manager);
    }

    @Test
    public void testDBConnection() throws SQLException{
        Connection c = connectionManager.getConnection();
    }

    @Test
    public void testGetID() throws SQLException{
        insertRequests();
        int id = requestRepo.getID(requestRepo.getConnection(), request1);
        Assert.assertTrue("Request id was not found", id>0);
    }

    @Test
    public void testGetByID() throws SQLException{
        insertRequests();
        ReimbRequest found = requestRepo.getByID(requestRepo.getConnection(), 1);
        Assert.assertTrue("Request was not found by ID", found!=null);
    }

    @Test
    public void testGetAll() throws SQLException{
        insertRequests();
        //requestRepo.insert(requestRepo.getConnection(), request1);
        List<ReimbRequest> all = requestRepo.getAll(requestRepo.getConnection());
        Assert.assertFalse("The list is empty", all.isEmpty());
    }

    @Test
    public void testInsertRequest() throws SQLException{
        //insertEmployees();
        //Employee emp3 = new Employee("samuel", "jackson", "sjackson@gmail.com", "sjackson", "popcorn");
        int result = requestRepo.insert(requestRepo.getConnection(), request1);
        Assert.assertTrue("Request table was not updated", result>0);
        ReimbRequest found = requestRepo.getByID(requestRepo.getConnection(), requestRepo.getID(requestRepo.getConnection(), request1));
        Assert.assertTrue("Request was not inserted", found!=null);
    }

    @Test
    public void testDeleteRequest() throws SQLException{
        //insertRequests();
        requestRepo.insert(requestRepo.getConnection(), request2);
        int result = requestRepo.delete(requestRepo.getConnection(), request2);
        Assert.assertTrue("Request table was not updated", result>0);
        ReimbRequest found = requestRepo.getByID(requestRepo.getConnection(), request2.getId());
        Assert.assertTrue("Request was not deleted", found==null);

    }

    @Test
    public void testUpdateRequest() throws SQLException, ParseException {
        //requestRepo.insert(requestRepo.getConnection(), request1);
        //insertRequests();
        ReimbRequest origReq = requestRepo.getByID(requestRepo.getConnection(), 1);
        Assert.assertFalse("original request does not exist", origReq==null);
        int result = requestRepo.update(requestRepo.getConnection(), origReq, "denied", (Manager) manager);
        Assert.assertTrue("Request table was not updated", result>0);
        ReimbRequest found = requestRepo.getByID(requestRepo.getConnection(), 1);
        Assert.assertFalse("Updated request does not exist", found==null);
        Assert.assertFalse("Request was not resolved", origReq.equals(found));
    }
}

package reimbapp.repos;

import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.models.ReimbRequest;
import reimbapp.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestRepository implements Repository <ReimbRequest, Integer> {
    private ConnectionManager connectionManager;

    public RequestRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
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

    @Override
    public int execUpdate(Connection c, PreparedStatement p) {
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
    public int insert(Connection c, ReimbRequest req) {
        PreparedStatement p = init(c, "insert into requests (emp_id, amount, created, status, resolved, man_id, descr) values (?,?,?,?,?,?,?)");
        int result = 0;
        try {
            p.setInt(1, req.getEmployee().getId());
            p.setFloat(2, req.getAmount());
            //p.setDate(3, (Date) req.getStart());
            p.setDate(3, new java.sql.Date(req.getStart().getTime()));
            p.setString(4, req.getStatus());
            p.setDate(5, null);
            p.setInt(6, 0);
            p.setString(7, req.getDescr());
            result = execUpdate(c, p);

        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public int delete(Connection c, ReimbRequest req) {
        //PreparedStatement p = init(c, "delete from requests where emp_id=? and amount=? and created=? and status=? and descr=?");
        PreparedStatement p = init(c, "delete from requests where emp_id=? and amount=? and status=? and descr=?");
        int result = 0;
        try {
            p.setInt(1, req.getEmployee().getId());
            p.setFloat(2, req.getAmount());
            //p.setDate(3, (Date)req.getStart());
            //p.setDate(3, new java.sql.Date(req.getStart().getTime()));
            p.setString(3, req.getStatus());
            p.setString(4, req.getDescr());
            result = execUpdate(c, p);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return result;
    }


    public int update(Connection c, ReimbRequest req, String status, Manager m){
        PreparedStatement p = init(c, "update requests set status=?, resolved=current_timestamp, man_id=? where emp_id=? and amount=? and descr=?");
        int result = 0;
        try {
            p.setString(1, status);
            p.setInt(2, m.getId());
            if(req.getEmployee()==null){
                p.setInt(3, 0);
            }else{
                p.setInt(3, req.getEmployee().getId());
            }
            p.setFloat(4, req.getAmount());
            p.setString(5, req.getDescr());
            result = p.executeUpdate();

        }catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
            //throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Integer getID(Connection c, ReimbRequest req) {
        //PreparedStatement p = init(c, "select id from requests where emp_id=? and amount=? and created=? and status=? and descr=?");
        PreparedStatement p = init(c, "select id from requests where emp_id=? and amount=? and status=? and descr=?");
        int id = 0;
        ResultSet r = null;
        try{
            p.setInt(1, req.getEmployee().getId());
            p.setFloat(2, req.getAmount());
            //p.setDate(3, (Date)req.getStart());
            //p.setDate(3, new java.sql.Date(req.getStart().getTime()));
            p.setString(3, req.getStatus());
            p.setString(4, req.getDescr());
            r = p.executeQuery();
            while(r.next()){
                id = r.getInt("id");
            }
        }catch(SQLException e){
            System.out.println("Exception thrown: " + e);
        }
        return id;
    }

    @Override
    public ReimbRequest getByID(Connection c, Integer id){
        PreparedStatement p = init(c, "select * from requests where id=?");
        EmployeeRepository empRepo = new EmployeeRepository(connectionManager);
        ReimbRequest req = null;
        try{
            p.setInt(1, id);
            ResultSet r = p.executeQuery();
            while(r.next()){
                int emp_id = r.getInt("emp_id");
                Employee emp = empRepo.getByID(c, emp_id);//TODO
                int man_id = r.getInt("man_id");
                Employee man = empRepo.getByID(c, man_id);//TODO
                req = new ReimbRequest(
                        emp,
                        r.getFloat("amount"),
                        r.getDate("created"),
                        r.getString("status"),
                        r.getDate("resolved"),
                        (Manager) man,
                        r.getString("descr")
                );
                req.setId(r.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return req;
    }

    public List<ReimbRequest> getByStatus(Connection c, String status){
        PreparedStatement p = init(c, "select * from requests where status=?");
        List <ReimbRequest> requests = new ArrayList<>();
        EmployeeRepository empRepo = new EmployeeRepository(connectionManager);
        ReimbRequest req = null;
        try{
            p.setString(1, status);
            ResultSet r = p.executeQuery();
            while(r.next()){
                int emp_id = r.getInt("emp_id");
                Employee emp = empRepo.getByID(c, emp_id);//TODO
                int man_id = r.getInt("man_id");
                Employee man = empRepo.getByID(c, man_id);//TODO
                req = new ReimbRequest(
                        emp,
                        r.getFloat("amount"),
                        r.getDate("created"),
                        r.getString("status"),
                        r.getDate("resolved"),
                        (Manager) man,
                        r.getString("descr")
                );
                req.setId(r.getInt("id"));
                requests.add(req);
                requests.add(req);
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return requests;
    }

    public List<ReimbRequest> getByEmployee(Connection c, Employee employee){
        PreparedStatement p = init(c, "select * from requests where emp_id=?");
        List<ReimbRequest> requests = new ArrayList<>();
        EmployeeRepository empRepo = new EmployeeRepository(connectionManager);
        ReimbRequest req = null;
        try{
            p.setInt(1, employee.getId());
            ResultSet r = p.executeQuery();
            while(r.next()){
                int emp_id = r.getInt("emp_id");
                Employee emp = empRepo.getByID(c, emp_id);//TODO
                int man_id = r.getInt("man_id");
                Employee man = empRepo.getByID(c, man_id);//TODO
                req = new ReimbRequest(
                        emp,
                        r.getFloat("amount"),
                        r.getDate("created"),
                        r.getString("status"),
                        r.getDate("resolved"),
                        (Manager) man,
                        r.getString("descr")
                );
                req.setId(r.getInt("id"));
                requests.add(req);
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return requests;
    }

    @Override
    public List<ReimbRequest> getAll(Connection c) {
        PreparedStatement p = init(c, "select * from requests");
        EmployeeRepository empRepo = new EmployeeRepository(connectionManager);
        List <ReimbRequest> all = new ArrayList<>();
        try{
            ResultSet r = p.executeQuery();
            while(r.next()){
                int emp_id = r.getInt("emp_id");
                Employee emp = empRepo.getByID(c, emp_id);//TODO
                int man_id = r.getInt("man_id");
                Employee man = empRepo.getByID(c, man_id);//TODO
                ReimbRequest req = new ReimbRequest(
                        emp,
                        r.getFloat("amount"),
                        r.getDate("created"),
                        r.getString("status"),
                        r.getDate("resolved"),
                        (Manager) man,
                        r.getString("descr")
                );
                req.setId(r.getInt("id"));
                all.add(req);
            }
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e);
        }
        return all;
    }
}

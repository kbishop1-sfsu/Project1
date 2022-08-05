package reimbapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.Driver;
import reimbapp.models.Employee;
import reimbapp.repos.EmployeeRepository;
import reimbapp.utils.ConnectionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

//http://localhost:8080/Project1/employee

@WebServlet(name="EmployeeServlet", initParams = {})
public class EmployeeServlet extends HttpServlet {

    private ObjectMapper om;
    private EmployeeRepository empRepo;
    private static Logger logger = (Logger) LogManager.getLogger(EmployeeServlet.class.getName());

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing employee servlet");
        om = new ObjectMapper();
        //empRepo = (EmployeeRepository) getServletContext().getAttribute("employeeRepo");
        empRepo = new EmployeeRepository(new ConnectionManager(new Driver(),"jdbc:postgresql://java-enterprise-22075.cgvdqjwau9al.us-west-1.rds.amazonaws.com:5432/my_database",
                "postgres", "deviantwriter14"));
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("Servicing employee servlet");
        super.service(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = 0;
        try{
            //String [] url =
            id = Integer.parseInt(req.getRequestURI().split("/")[3]);
            //System.out.println(Integer.parseInt(req.getRequestURI().split("/")[3]));
        }catch (Exception e){
            System.out.println("Exception caught: " + e);
        }

        if(id == 0){
            List<Employee> allEmployees = empRepo.getAll(empRepo.getConnection());
            resp.setContentType("application/json");
            resp.getWriter().write(om.writeValueAsString(allEmployees));
            resp.setStatus(200);
            return;
        } else {
            Employee employee = empRepo.getByID(empRepo.getConnection(), id);
            if(employee == null){
                resp.setStatus(404);
                return;
            } else {
                resp.setContentType("application/json");
                resp.getWriter().write(om.writeValueAsString(employee));
                resp.setStatus(200);
            }
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get employee id
        int id = 0;
        try{
            id = Integer.parseInt(req.getRequestURI().split("/")[3]);
        }catch (Exception e){
            System.out.println("Exception caught: " + e);
        }

        String toDo = null;
        try {
            toDo =req.getRequestURI().split("/")[4];
        }catch (Exception e){
            System.out.println("Exception caught: " + e);
        }

        if(id == 0){
            //if employee id is not provided
            resp.setStatus(405);
            return;
        } else{
            Employee employee = empRepo.getByID(empRepo.getConnection(), id);
            //if employee does not exist
            if(employee == null){
                resp.setStatus(404);
                return;
            } else {
                if(toDo == null){
                    resp.setStatus(400);
                    return;
                } else if(toDo.equals("reset")){
                    //get string password
                    String password = om.readValue(req.getInputStream(), String.class);
                    if(password==null){
                        resp.setStatus(400);
                        return;
                    }else{
                        //reset password
                        empRepo.updatePassword(empRepo.getConnection(), employee, password);
                        logger.info("Employee " + employee + " has reset password");
                        resp.setContentType("text/html");//setting the content type
                        PrintWriter pw=resp.getWriter();//get the stream to write the data
                        pw.write("Employee " + employee + " has reset password");
                        resp.setStatus(200);
                    }

                } else if(toDo.equals("update")){
                    //get employee obj
                    Employee updatedEmp = om.readValue(req.getInputStream(), Employee.class);
                    if(updatedEmp==null){
                        resp.setStatus(400);
                        return;
                    }else{
                        //update employee
                        empRepo.updateInfo(empRepo.getConnection(), employee, updatedEmp.getFirstName(), updatedEmp.getLastName(), updatedEmp.getEmail(), updatedEmp.getPassword());
                        logger.info("Employee " + employee + " has updated information");
                        resp.setContentType("text/html");//setting the content type
                        PrintWriter pw=resp.getWriter();//get the stream to write the data
                        pw.write("Employee " + employee + " has updated information");
                        resp.setStatus(200);
                    }
                }else{
                    resp.setStatus(405);
                }

            }
        }

    }


    @Override
    public void destroy() {
        //super.destroy();
        System.out.println("Destroying employee servlet");
    }


}

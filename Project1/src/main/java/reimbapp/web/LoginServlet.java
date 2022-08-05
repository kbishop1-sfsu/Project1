package reimbapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.Driver;
import reimbapp.models.Employee;
import reimbapp.models.ReimbRequest;
import reimbapp.models.User;
import reimbapp.repos.EmployeeRepository;
import reimbapp.repos.RequestRepository;
import reimbapp.utils.ConnectionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="LoginServlet", initParams = {})
public class LoginServlet extends HttpServlet {
    private ObjectMapper om;
    private EmployeeRepository empRepo;
    //private Employee user;
    private static Logger logger = (Logger) LogManager.getLogger(EmployeeServlet.class.getName());

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing login servlet");
        om = new ObjectMapper();
        empRepo = new EmployeeRepository(new ConnectionManager(new Driver(),"jdbc:postgresql://java-enterprise-22075.cgvdqjwau9al.us-west-1.rds.amazonaws.com:5432/my_database",
                "postgres", "deviantwriter14"));
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("Servicing login servlet");
        super.service(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = om.readValue(req.getInputStream(), User.class);
        PrintWriter pw=resp.getWriter();//get the stream to write the data
        if(user == null){
            resp.setStatus(400);
            return;
        } else{
            Employee employee = null;
            List<Employee> employees = empRepo.getAll(empRepo.getConnection());
            for(Employee e: employees){
                if(e.getUsername().equals(user.getUsername())){
                    employee = e;
                }
            }
            if(employee==null){
                resp.setStatus(404);
                return;
            } else {
                if(employee.getPassword().equals(user.getPassword())){
                    logger.info(employee + " has logged in.");
                    resp.setContentType("text/html");//setting the content type
                    pw.write(employee + " has logged in.");
                    resp.setStatus(200);
                    return;
                } else {
                    logger.info("Incorrect password. Potential unathorized user.");
                    resp.setContentType("text/html");//setting the content type
                    pw.write("Incorrect password");
                    //resp.setStatus(400);

                }
            }
        }

    }

    @Override
    public void destroy() {
        //super.destroy();
        System.out.println("Destroying login servlet");
    }

}

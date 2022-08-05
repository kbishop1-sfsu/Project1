package reimbapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.Driver;
import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.models.ReimbRequest;
import reimbapp.repos.EmployeeRepository;
import reimbapp.repos.RequestRepository;
import reimbapp.services.ManagerService;
import reimbapp.utils.ConnectionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//import java.sql.Date;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name="RequestServlet", initParams = {})
public class RequestServlet extends HttpServlet {
    private ObjectMapper om;
    private RequestRepository reqRepo;
    private EmployeeRepository empRepo;
    private Manager manager;
    private ManagerService manService;
    private static Logger logger = (Logger) LogManager.getLogger(EmployeeServlet.class.getName());

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing request servlet");
        om = new ObjectMapper();
        reqRepo = new RequestRepository(new ConnectionManager(new Driver(),"jdbc:postgresql://java-enterprise-22075.cgvdqjwau9al.us-west-1.rds.amazonaws.com:5432/my_database",
                "postgres", "deviantwriter14"));
        empRepo = new EmployeeRepository(new ConnectionManager(new Driver(),"jdbc:postgresql://java-enterprise-22075.cgvdqjwau9al.us-west-1.rds.amazonaws.com:5432/my_database",
                "postgres", "deviantwriter14"));
        manager = new Manager();
        manService = new ManagerService();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("Servicing request servlet");
        super.service(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = 0;
        String status = null;
        try{
            id = Integer.parseInt(req.getRequestURI().split("/")[3]);
        }catch (Exception e){
            System.out.println("Exception caught: " + e);
        }
        try{
            status = req.getRequestURI().split("/")[3];
        }catch (Exception e){
            System.out.println("Exception caught: " + e);
        }

        if(id == 0) {

            if (status == null) {
                List<ReimbRequest> allRequests = reqRepo.getAll(reqRepo.getConnection());
                resp.setContentType("application/json");
                resp.getWriter().write(om.writeValueAsString(allRequests));
                resp.setStatus(200);
                return;
            } else if (status.equals("pending")) {
                //do work
                List<ReimbRequest> pending = manService.viewPendingRequests(manager, reqRepo.getAll(reqRepo.getConnection()));
                resp.setContentType("application/json");
                resp.getWriter().write(om.writeValueAsString(pending));
                resp.setStatus(200);
                return;
            } else if (status.equals("resolved")) {
                List<ReimbRequest> resolved = manService.viewResolvedRequests(manager, reqRepo.getAll(reqRepo.getConnection()));
                resp.setContentType("application/json");
                resp.getWriter().write(om.writeValueAsString(resolved));
                resp.setStatus(200);
                return;

            } else {
                List <String> usernames = empRepo.getAll(reqRepo.getConnection()).stream().map(r->r.getUsername()).collect(Collectors.toList());
                if(usernames.contains(status)){
                    List <ReimbRequest> empRequests = manService.viewRequestsByEmployee(manager, empRepo.getByUsername(empRepo.getConnection(), status), reqRepo.getAll(reqRepo.getConnection()));
                    resp.setContentType("application/json");
                    resp.getWriter().write(om.writeValueAsString(empRequests));
                    resp.setStatus(200);
                    return;
                }else{
                    resp.setStatus(405);

                }
            }

        } else {
            ReimbRequest request = reqRepo.getByID(reqRepo.getConnection(), id);
            if(request == null){
                resp.setStatus(404);
                return;
            } else {
                resp.setContentType("application/json");
                resp.getWriter().write(om.writeValueAsString(request));
                resp.setStatus(200);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get request info
        ReimbRequest request = om.readValue(req.getInputStream(), ReimbRequest.class);
        if(request == null){
            resp.setStatus(400);
            return;
        }else{
            //put request into repo
            reqRepo.insert(reqRepo.getConnection(), request);
            logger.info("Request " + request + " has been created");
            //return confirmation
            resp.setContentType("text/html");//setting the content type
            PrintWriter pw=resp.getWriter();//get the stream to write the data
            pw.write("Request " + request + " has been created");
            resp.setStatus(201);
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //ReimbRequest request = om.readValue(req.getInputStream(), ReimbRequest.class);
        int id = 0;
        try{
            id = Integer.parseInt(req.getRequestURI().split("/")[3]);
        }catch (Exception e){
            System.out.println("Exception caught: " + e);
        }

        if(id==0){
            resp.setStatus(405);
            return;
        }else{
            ReimbRequest request = reqRepo.getByID(reqRepo.getConnection(), id);
            if(request==null){
                resp.setStatus(404);
                return;
            } else {
                String status = om.readValue(req.getInputStream(), String.class);
                if(status==null){
                    resp.setStatus(400);
                    return;
                } else {
                    //update request in repo
                    reqRepo.update(reqRepo.getConnection(), request, status, manager);
                    logger.info("Request " + request + " has been resolved");
                    //return confirmation
                    resp.setContentType("text/html");//setting the content type
                    PrintWriter pw=resp.getWriter();//get the stream to write the data
                    pw.write("Request " + request + " has been resolved");
                    resp.setStatus(200);
                }
            }

        }

    }

    @Override
    public void destroy() {
        //super.destroy();
        System.out.println("Destroying request servlet");
    }
}

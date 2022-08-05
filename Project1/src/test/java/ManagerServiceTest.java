import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.models.ReimbRequest;
import reimbapp.repos.EmployeeRepository;
import reimbapp.repos.RequestRepository;
import reimbapp.services.EmployeeService;
import reimbapp.services.ManagerService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ManagerServiceTest {
    private Manager manager;
    private Employee emp;
    private ManagerService manService;
    private EmployeeRepository manRepo;
    private RequestRepository reqRepo;

    @Before
    public void init(){
        manService = new ManagerService();
        manRepo = Mockito.mock(EmployeeRepository.class);
        reqRepo = Mockito.mock(RequestRepository.class);
        manager = (Manager) manRepo.getByID(manRepo.getConnection(), 2);
        emp = manRepo.getByID(manRepo.getConnection(), 1);//July 24, 2022
    }

    @Test
    public void testReviewRequest(){
        //check that updated request match input info
        ReimbRequest pendingReq = new ReimbRequest(emp, (float)35.76, "for dinner on saturday");
        Mockito.when(reqRepo.getByID(reqRepo.getConnection(), 3)).thenReturn(pendingReq);
        ReimbRequest resolvedReq = manService.reviewRequest(manager, "denied", pendingReq);
        Assert.assertEquals("The status of the request was not changed", pendingReq.getStatus(), resolvedReq.getStatus());
        Assert.assertEquals("The resolved date was not changed", pendingReq.getEnd(), resolvedReq.getEnd());
        Assert.assertEquals("The manager did not resolve the request", pendingReq.getManager(), manager);

    }

    @Test
    public void testViewRequestsByEmployee(){
        //check that all requests are from employee
        List<ReimbRequest> requests = reqRepo.getAll(reqRepo.getConnection());
        Mockito.when(reqRepo.getAll(reqRepo.getConnection())).thenReturn(requests);
        List <ReimbRequest> empReq = manService.viewRequestsByEmployee(manager, emp, requests);
        boolean flag = true;
        for(ReimbRequest r: empReq){
            if(r.getEmployee().equals(emp)==false){
                flag = false;
            }
        }
        Assert.assertTrue("The requests collected are not from employee", flag);
    }

    @Test
    public void testViewPendingRequests(){
        //check that all request are pending
        List<ReimbRequest> requests = reqRepo.getAll(reqRepo.getConnection());
        Mockito.when(reqRepo.getAll(reqRepo.getConnection())).thenReturn(requests);
        List <ReimbRequest> pendingReq = manService.viewPendingRequests(manager, requests);
        boolean flag = true;
        for(ReimbRequest r: pendingReq){
            if(r.getStatus().equals("pending")==false){
                flag = false;
            }
        }
        Assert.assertTrue("The requests collected are not pending", flag);
    }

    @Test
    public void testViewResolvedRequests(){
        //check that all request are resolved
        List<ReimbRequest> requests = reqRepo.getAll(reqRepo.getConnection());
        Mockito.when(reqRepo.getAll(reqRepo.getConnection())).thenReturn(requests);
        List <ReimbRequest> resolvedReq = manService.viewResolvedRequests(manager, requests);
        boolean flag = true;
        for(ReimbRequest r: resolvedReq){
            if(r.getStatus().equals("pending")){
                flag = false;
            }
        }
        Assert.assertTrue("The requests collected are not resolved", flag);
    }


}

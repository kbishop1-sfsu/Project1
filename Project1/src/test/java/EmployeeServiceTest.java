import javafx.scene.control.RadioMenuItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Request;
import org.mockito.Mock;
import org.mockito.Mockito;
import reimbapp.models.Employee;
import reimbapp.models.ReimbRequest;
import reimbapp.repos.EmployeeRepository;
import reimbapp.repos.RequestRepository;
import reimbapp.services.EmployeeService;

import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceTest {

    private Employee emp;
    private EmployeeService empService;
    private EmployeeRepository empRepo;

    private RequestRepository reqRepo;

    @Before
    public void init(){
        empService = new EmployeeService();
        empRepo = Mockito.mock(EmployeeRepository.class);
        reqRepo = Mockito.mock(RequestRepository.class);
        emp = empRepo.getByID(empRepo.getConnection(), 1);
    }

    @Test
    public void testSubmitRequest(){
        //check that request matches inputted/expected details
        //check that the list increase in size
        List<ReimbRequest> requests = new ArrayList<>();
        Employee newEmp = new Employee("jenny", "lopez", "jlopez@sfsu.edu", "jlopez", "ducksauce");
        Mockito.when(empRepo.getByID(empRepo.getConnection(), 1)).thenReturn(newEmp);//TODO is this necessary?
        ReimbRequest request = empService.submitRequest(newEmp, (float) 23.55, "office supplies", requests);
        Assert.assertEquals("The employee submitting request does not match.", newEmp, request.getEmployee());
        Assert.assertTrue("The request amount does not match.", request.getAmount()==(float)23.55);
        Assert.assertTrue("The request created date is not set.", request.getStart()!=null);
        Assert.assertEquals("The request status does not match.", "pending", request.getStatus());
        Assert.assertEquals("The request description does not match.", "office supplies", request.getDescr());
        Assert.assertTrue("The request was not added to the list.", requests.size()>0);

    }

    @Test
    public void testViewPendingRequests(){
        //check that all request are pending
        //check that all requests are from emp
        //TODO:crate list from scratch
        List <ReimbRequest> pending = empService.viewPendingRequests(emp, reqRepo.getAll(reqRepo.getConnection()));
        Mockito.when(reqRepo.getAll(reqRepo.getConnection())).thenReturn(pending);
        boolean flag = true;
        for(ReimbRequest r: pending){
            //if the requests are not from employee
            if(r.getEmployee().equals(emp)==false){
                flag = false;
            }
        }
        Assert.assertTrue("The collected requests are not from the employee", flag);
        for(ReimbRequest r: pending){
            //if the request is not pending
            if(r.getStatus().equals("pending")==false){
                flag = false;
            }
        }
        Assert.assertTrue("The collected requests' status are not pending", flag);
    }

    @Test
    public void testViewResolvedRequests(){
        //check that all requests are from employee
        //check that all requests are resolved
        //TODO:crate list from scratch
        List <ReimbRequest> resolved = empService.viewPendingRequests(emp, reqRepo.getAll(reqRepo.getConnection()));
        Mockito.when(reqRepo.getAll(reqRepo.getConnection())).thenReturn(resolved);
        boolean flag = true;
        for(ReimbRequest r: resolved){
            //if the request requested are not from employee
            if(r.getEmployee().equals(emp)==false ){
                flag = false;
            }
        }
        Assert.assertTrue("The collected requests are not from the employee", flag);
        for(ReimbRequest r: resolved){
            //if the request are not resolved
            if(r.getStatus().equals("pending")==true){
                flag = false;
            }
        }
        Assert.assertTrue("The collected requests' status are not resolved", flag);
    }

    @Test
    public void testUpdateEmployeeInfo(){
        //test that updated info matches inputted info
        Employee newEmp = new Employee("jenny", "lopez", "jlopez@sfsu.edu", "jlopez", "ducksauce");
        Mockito.when(empRepo.getByID(empRepo.getConnection(), 1)).thenReturn(newEmp);
        empService.updateEmployeeInfo(newEmp, "jennifer", "aniston", "janiston@gmail.com", "janiston");
        Assert.assertEquals("The firstname was not changed.", newEmp.getFirstName(), "jennifer");
        Assert.assertEquals("The lastname was not changed.", newEmp.getLastName(), "aniston");
        Assert.assertEquals("The email was not changed.", newEmp.getEmail(), "janiston@gmail.com");
        Assert.assertEquals("The username was not changed", newEmp.getUsername(), "janiston");

    }

    @Test
    public void testResetPassword(){
        //check that the updated password  matches inputted info
        Employee newEmp = new Employee("jenny", "lopez", "jlopez@sfsu.edu", "jlopez", "ducksauce");
        Mockito.when(empRepo.getByID(empRepo.getConnection(), 1)).thenReturn(newEmp);
        empService.resetPassword(newEmp, "applejuice");
        Assert.assertEquals("The password was not changed.", newEmp.getPassword(), "applejuice");

    }


}

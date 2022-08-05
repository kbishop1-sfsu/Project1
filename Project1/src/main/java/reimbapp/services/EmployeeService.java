package reimbapp.services;

import reimbapp.models.Employee;
import reimbapp.models.ReimbRequest;

import java.util.List;
import java.util.stream.Collectors;


public class EmployeeService {
    public ReimbRequest submitRequest(Employee employee, float amount, String descr, List<ReimbRequest> pendingRequests){
        ReimbRequest req = new ReimbRequest(employee, amount, descr);
        pendingRequests.add(req);
        return req;
    }

    public List <ReimbRequest> viewPendingRequests(Employee employee, List<ReimbRequest> requests){
        List <ReimbRequest> employeeRequests = requests.stream().filter(r->r.getStatus().equals("pending") && r.getEmployee().equals(employee)).collect(Collectors.toList());
        employeeRequests.stream().forEach(e-> System.out.println(e));
        return employeeRequests;
    }

    public List <ReimbRequest> viewResolvedRequests(Employee employee, List <ReimbRequest> requests){
        List <ReimbRequest> employeeRequests = requests.stream().filter(r->r.getStatus().equals("pending")==false && r.getEmployee().equals(employee)).collect(Collectors.toList());
        employeeRequests.stream().forEach(e-> System.out.println(e));
        return  employeeRequests;
    }

    public Employee updateEmployeeInfo(Employee employee, String firstName, String lastName, String email, String username){
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setUsername(username);
        return employee;
    }

    public Employee resetPassword(Employee employee, String password){
        employee.setPassword(password);
        return employee;
    }

    public void viewEmployeeInfo(Employee employee){
        System.out.println(employee.toString());
    }


}

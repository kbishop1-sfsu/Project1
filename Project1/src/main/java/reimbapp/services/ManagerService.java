package reimbapp.services;

import reimbapp.models.Employee;
import reimbapp.models.Manager;
import reimbapp.models.ReimbRequest;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerService {
    public ReimbRequest reviewRequest(Manager manager, String status, ReimbRequest pendingRequest){
        System.out.println(pendingRequest);
        pendingRequest.setStatus(status);
        pendingRequest.setEnd(new Date());
        pendingRequest.setManager(manager);
        //TODO: send email to employee about resolved request
        return pendingRequest;
    }

    public List<ReimbRequest> viewRequestsByEmployee(Manager manager, Employee employee, List<ReimbRequest> requests){
        List <ReimbRequest> employeeRequests = requests.stream().filter(r->r.getEmployee().equals(employee)).collect(Collectors.toList());
        employeeRequests.stream().forEach(e-> System.out.println(e));
        return employeeRequests;
    }

    public List<ReimbRequest> viewPendingRequests(Manager manager, List<ReimbRequest> requests){
        List<ReimbRequest> pendingRequests = requests.stream().filter(r->r.getStatus().equals("pending")).collect(Collectors.toList());
        pendingRequests.stream().forEach(r-> System.out.println(r));
        return pendingRequests;
    }

    public List<ReimbRequest> viewResolvedRequests(Manager manager, List<ReimbRequest> requests){
        List<ReimbRequest> resolvedRequests = requests.stream().filter(r->r.getStatus().equals("pending")==false).collect(Collectors.toList());
        resolvedRequests.stream().forEach(r-> System.out.println(r));
        return resolvedRequests;
    }

    public void viewEmployees(Manager m, List<Employee> employees){
        employees.stream().forEach(e-> System.out.println(e));
    }
}

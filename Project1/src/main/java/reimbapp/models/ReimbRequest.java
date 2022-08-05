package reimbapp.models;

//import java.sql.Date;
import java.util.Date;
import java.util.Objects;

public class ReimbRequest {
    private int id;
    private Employee employee;
    private float amount;
    private Date start;//date request was submitted
    private String status; //pending, denied, approved
    private Date end; //date request was resolved
    private Manager manager;
    private String descr; //description of reimbursement

    public ReimbRequest() {
    }

    public ReimbRequest(Employee employee, float amount, String descr){
        this.employee = employee;
        this.amount = amount;
        start = new Date();
        status = "pending";
        end = null;
        manager = null;
        this.descr = descr;
    }

    public ReimbRequest(Employee employee, float amount, Date start, String status, Date end, Manager manager, String descr) {
        this.employee = employee;
        this.amount = amount;
        this.start = start;
        this.status = status;
        this.end = end;
        this.manager = manager;
        this.descr = descr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public String toString() {
        return "Reimbursement Request{" +
                "employee=" + employee +
                ", amount=" + amount +
                ", start=" + start +
                ", status='" + status + '\'' +
                ", end=" + end +
                ", descr='" + descr + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReimbRequest that = (ReimbRequest) o;
        return Float.compare(that.amount, amount) == 0 && employee.equals(that.employee) && start.equals(that.start) && status.equals(that.status) && Objects.equals(end, that.end) && descr.equals(that.descr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, amount, start, status, end, descr);
    }
}

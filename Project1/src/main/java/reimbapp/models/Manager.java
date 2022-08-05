package reimbapp.models;

public class Manager extends Employee{

    public Manager() {
    }

    public Manager(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id='" + super.getId() + '\'' +
                ", firstName='" + super.getFirstName() + '\'' +
                ", lastName='" + super.getLastName() + '\'' +
                ", email='" + super.getEmail() + '\'' +
                ", username='" + super.getUsername() + '\'' +
                '}';
    }
}


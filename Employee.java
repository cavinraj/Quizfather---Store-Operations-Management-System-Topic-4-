public class Employee{
    //this class is the blueprint of every employee from the CSV file

    private String employee_id;
    private String password;
    private String employee_name;
    private String role;

    public Employee(String employee_id,String employee_name,String role,String password){
        this.employee_id = employee_id;
        this.employee_name = employee_name;
        this.role = role;
        this.password = password;
    }

    //This is just done for fun and exploration IN CASE i wish to do a change password feature
    public void set_password(String password){
        if (password != null) {
            this.password = password;
        }
    }

    public String get_employee_id(){return employee_id;}
    public String get_employee_name(){return employee_name;}
    public String get_role(){return role;}
    public String get_password(){return password;}

}
package src;

public class TestDrive {
    public static void main(String[] args) {
        /*ArrayList<Object> employees = FileReader.transfer_data("data/employee.csv");
        System.out.println(employees.isEmpty());//the output i got is false
        System.out.println(employees.size());//the output i got is 12 which is the correct size of 12 employees in csv
        System.out.println("Total Employees Loaded : " + employees.size());
        System.out.println("-----------------------------------------------------");
        for (Object employee : employees) {
            System.out.printf("ID: %s | Name: %s | Role: %s | Password: %s\n",((Employee) employee).get_employee_id(),((Employee) employee).get_employee_name(),((Employee) employee).get_role(),((Employee) employee).get_password());
            System.out.println("-----------------------------------------------------");
        }*/

        new LoginPage();
    }

}

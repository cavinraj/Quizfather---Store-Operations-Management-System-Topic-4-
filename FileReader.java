import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
    static ArrayList<Employee> employees;

    static ArrayList<Employee> transfer_data(String filename){
        employees = new ArrayList<>();

        try (Scanner scanner = new Scanner(new FileInputStream(filename))) {
            scanner.nextLine(); //This is to eliminate the first line of the csv from being read
            while (scanner.hasNextLine()) {
                String current_line = scanner.nextLine();
                String[] infos = current_line.split(",");
                if (infos.length == 4) {
                    String employee_id = infos[0];
                    String employee_name = infos[1];
                    String role = infos[2];
                    String password = infos[3];
                    Employee employee = new Employee(employee_id, employee_name, role, password);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }

        return employees;
    }
}

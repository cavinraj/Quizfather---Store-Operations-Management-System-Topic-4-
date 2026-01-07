import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Employee> employees =
                DataLoader.loadEmployees("employee.csv");

        List<Outlet> outlets =
                DataLoader.loadOutlets("outlet.csv");

        List<Model> models =
                DataLoader.loadModels("model.csv");

        System.out.println("System started successfully.");
        System.out.println("Employees loaded: " + employees.size());
        System.out.println("Outlets loaded: " + outlets.size());
        System.out.println("Models loaded: " + models.size());
    }
}
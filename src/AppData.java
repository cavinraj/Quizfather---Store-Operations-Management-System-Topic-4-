package src;
import java.util.*;
public class AppData {
    // Shared data across the whole system
    public static List<Employee> employees;
    public static List<Outlet> outlets;
    public static List<Model> models;

    // Load data ONCE when system starts
    public static void init() {
        employees = DataLoader.loadEmployees("employee.csv");
        outlets   = DataLoader.loadOutlets("outlet.csv");
        models    = DataLoader.loadModels("model.csv");
    }
}

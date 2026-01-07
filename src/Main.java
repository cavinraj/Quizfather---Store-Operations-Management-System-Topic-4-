package src;
public class Main {
    public static void main(String[] args) {

        // DATA LOAD STATE
        AppData.init();

        System.out.println("System started successfully.");
        System.out.println("Employees loaded: " + AppData.employees.size());
        System.out.println("Outlets loaded: " + AppData.outlets.size());
        System.out.println("Models loaded: " + AppData.models.size());

        // Start login page (dashboard comes AFTER login)
        new LoginPage();
    }
}

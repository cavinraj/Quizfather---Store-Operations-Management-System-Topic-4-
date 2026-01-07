import java.io.*;
import java.util.*;

public class DataLoader {

    public static List<Employee> loadEmployees(String filePath) {
        List<Employee> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                list.add(new Employee(d[0], d[1], d[2], d[3]));
            }
        } catch (IOException e) {
            System.out.println("Cannot load employee.csv");
        }
        return list;
    }

    public static List<Outlet> loadOutlets(String filePath) {
        List<Outlet> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                list.add(new Outlet(d[0], d[1]));
            }
        } catch (IOException e) {
            System.out.println("Cannot load outlet.csv");
        }
        return list;
    }

    public static List<Model> loadModels(String filePath) {
        List<Model> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            String[] outlets = header.split(",");

            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                Model m = new Model(d[0], Double.parseDouble(d[1]));

                for (int i = 2; i < d.length; i++) {
                    m.setStock(outlets[i], Integer.parseInt(d[i]));
                }
                list.add(m);
            }
        } catch (IOException e) {
            System.out.println("Cannot load model.csv");
        }
        return list;
    }
}
public class Outlet {
    //this class is the blueprint of every outlet from the CSV file.

    private String outlet_id;
    private String outlet_name;

    public Outlet(String outlet_id,String outlet_name){
        this.outlet_id = outlet_id;
        this.outlet_name = outlet_name;
    }

    public String get_outlet_id(){return outlet_id;}
    public String get_outlet_name(){return outlet_name;}
}

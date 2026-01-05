public class Session {// temporary global memory class to hold user_id when app runs (important note : this class i made it as to receive only one user at atime)
    public static Employee current_user = null;//set to static and public because we want the entire app to share this attribute.
    //current user is null because by defaults nobody log in
}

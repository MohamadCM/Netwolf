import java.util.Vector;

public class netwolf {
    public static void main(String[] args) {
        Discovery d = new Discovery("cluster-list.txt", 9000);
        Vector<String[]> list = d.list();
        for (int i = 0; i < list.size(); i++) {
            String [] record = list.get(i);
            String name = record[0];
            String address = record[1];
            System.out.println("Name: " + name + " | Address: " + address);
        }
    }
}

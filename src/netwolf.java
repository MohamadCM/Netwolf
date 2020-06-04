import java.util.Vector;

public class netwolf {
    private static String clusterFileName = "cluster-list.txt";
    private static int discoveryPort = 9000;
    private static int seconds = 1;
    private static String currentNodeName = "N1";

    private static int requestPort = 9001;
    public static void main(String[] args) {
        GUI gui = new GUI();
/*        Discovery discovery = new Discovery(clusterFileName, discoveryPort, seconds, currentNodeName);
        Vector<String[]> list = discovery.list();
        for (int i = 0; i < list.size(); i++) {
            String [] record = list.get(i);
            String name = record[0];
            String address = record[1];
            System.out.println("Name: " + name + " | Address: " + address);
           }
        RequestFile requestFile = new RequestFile(requestPort, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Can't sleep in main thread");
        }
        requestFile.sendRequest("test.txt", "1010", list);*/
    }
}

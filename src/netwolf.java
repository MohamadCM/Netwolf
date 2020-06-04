import java.util.Vector;

/**
 * Main class
 * @author Mohamad Chaman-Motlagh
 * @version 1
 */
public class netwolf {
    private static String clusterFileName = "cluster-list.txt";
    private static int discoveryPort = 9000;
    private static int discoveryIntervalSeconds = 10;
    private static String currentNodeName = "N1";

    private static int requestPort = 9001;

    private static FileTransmission fileTransmission;
    private static int maximumServices = 5;
    private static String directory = "./directory";

    private static int waitForFileTimeOutSeconds;

    public static void main(String[] args) {
       // GUI gui = new GUI();
        fileTransmission = new FileTransmission(directory, maximumServices, waitForFileTimeOutSeconds);

        Discovery discovery = new Discovery(clusterFileName, discoveryPort, discoveryIntervalSeconds, currentNodeName);
        Vector<String[]> list = discovery.list();
        for (int i = 0; i < list.size(); i++) {
            String [] record = list.get(i);
            String name = record[0];
            String address = record[1];
            System.out.println("Name: " + name + " | Address: " + address);
           }
        RequestFile requestFile = new RequestFile(requestPort, null);

/*        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Can't sleep in main thread");
        }
        {
            requestFile.sendRequest("file.txt", "1010", list);
            fileTransmission.receiveFile("file.txt", "1010");
        }*/
    }
    public static void answerRequest(String fileName, String TCPPort, String remoteAddress){
        fileTransmission.sendFile(fileName, TCPPort, remoteAddress);
    }

    /**
     * @return default directory
     */
    public static String getDirectory(){
        return directory;
    }
}

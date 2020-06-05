import java.util.Vector;

/**
 * Main class
 * @author Mohamad Chaman-Motlagh
 * @version 1
 *
 * Use args as following manner:
 * -l for cluster-file name
 * -d for directory name
 * -n for node name
 * -p for UDP port
 * -t for file request timeout in seconds
 * -i for discovery message interval time in seconds
 * -m for maximum number of service done simultaneously
 * -g to use GUI
 */
public class netwolf {
    private static String clusterFileName = "cluster-list.txt";
    private static int port = 9000;
    private static Discovery discovery;
    private static int discoveryIntervalSeconds = 10 * 1000;
    private static String currentNodeName = "N1";


    private static RequestFile requestFile;

    private static FileTransmission fileTransmission;
    private static int maximumServices = 5;
    private static String directory = "directory/";

    private static int waitForFileTimeOutSeconds = 5 * 1000;

    public static void main(String[] args) {
        Boolean g = false;
        for (int i = 0; i < args.length; i++) {
            String tmp = args[i];
            if(tmp.equalsIgnoreCase("-l") && (i + 1 < args.length))
                clusterFileName = args[i + 1];
            if(tmp.equalsIgnoreCase("-d") && (i + 1 < args.length))
                directory = args[i + 1];
            if(tmp.equalsIgnoreCase("-p")  && (i + 1 < args.length))
                port = Integer.parseInt(args[i + 1]);
            if(tmp.equalsIgnoreCase("-t") && (i + 1 < args.length))
                waitForFileTimeOutSeconds = Integer.parseInt(args[i + 1]) * 1000;
            if(tmp.equalsIgnoreCase("-i") && (i + 1 < args.length))
                discoveryIntervalSeconds = Integer.parseInt(args[i + 1]) * 1000;
            if(tmp.equalsIgnoreCase("-m") && (i + 1 < args.length))
                maximumServices = Integer.parseInt(args[i + 1]);
            if(tmp.equalsIgnoreCase("-n") && (i + 1 < args.length))
                currentNodeName = args[i + 1];
            if(tmp.equalsIgnoreCase("-g"))
                g = true;
        }
            fileTransmission = new FileTransmission(directory, maximumServices, waitForFileTimeOutSeconds);

            discovery = new Discovery(clusterFileName, port, discoveryIntervalSeconds, currentNodeName);

            requestFile = new RequestFile(port);

            UDP udp = new UDP(port, discovery, requestFile);

            if(g) {
                GUI gui = new GUI(discovery, requestFile, fileTransmission);
            }
            Thread CLI = new CommandLineInterface(discovery, requestFile, fileTransmission);
            CLI.start();




    }
    public synchronized static void printList(){
        Vector<String[]> list = getList();
        for (int i = 0; i < list.size(); i++) {
            String [] record = list.get(i);
            String name = record[0];
            String address = record[1];
            System.out.println(name + " " + address);
        }
    }
    public synchronized static Vector<String[]> getList(){
        return discovery.list();
    }
    public static void sendRequest(String fileName, Vector<String[]> list){
        int TCPPort = (int)((Math.random()) * 60000) + 1024;
        requestFile.sendRequest(fileName, String.valueOf(TCPPort), list);
        fileTransmission.receiveFile(fileName, String.valueOf(TCPPort), list);
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

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

/**
 * This class is used to send and receive discovery message through network
 * @author Mohamad Chaman-Motlagh
 * @version 1
 */
public class Discovery {
    private String filename;
    private Vector<String[]> namesAndAddresses;
    private int port;
    private DatagramSocket ds;
    private int time;
    /**
     * @param filename is cluster-file
     * @param port is send and receive port
     * @param time is interval time between sending each discovery message
     * Creating an Object will start sending and receiving Discovery messages
     */
    public Discovery(String filename, int port, int time, String currentNodeName){
        this.filename = filename;
        this.port = port;
        this.ds = ds;
        this.time = time;
        namesAndAddresses = new Vector<String[]>();

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            Utility.setIP(socket.getLocalAddress().getHostAddress()); // Set machine's local IP
            Utility.setNodeName(currentNodeName); // Set machine's local node-name
            System.out.println("Listening on " + Utility.getIP() + ":" + port + " UDP");
            socket.close();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        Thread sendDiscovery = new SendDiscovery();
        sendDiscovery.start();
    }
    // Read file and updates namesAndAddresses Vector
    private synchronized void readFile(){

        namesAndAddresses.clear();
        File file = new File(filename);

        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String st;
            while ((st = bufferedReader.readLine()) != null) {
                if(st != null && st.length() >= 8) {
                    String[] split = st.split(" ");
                    namesAndAddresses.add(split);
                }
            }
            bufferedReader.close();
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private synchronized void writeToFile(Vector<String[]> received){
        try{

            //Specify the file name and path here
            File file = new File(filename);

            /* This logic is to create the file if the
             * file is not already present
             */
            if(!file.exists()){
                file.createNewFile();
            }

            //Here true is to append the content to file
            FileWriter fw = new FileWriter(file,true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < received.size(); i++) {
                String[] nameAndAddress = received.get(i);
                String content = "\n" + nameAndAddress[0] + " " + nameAndAddress[1];
                boolean contains = false;
                for (int j = 0; j < namesAndAddresses.size(); j++) {
                    String[] tmp = namesAndAddresses.get(j);
                    if( (tmp[0].equals(nameAndAddress[0]) && tmp[1].equals(nameAndAddress[1])) )
                        contains = true;
                }
                if (nameAndAddress[1].equals(Utility.getIP())) // Current machine's IP should't get inside cluster-list file
                    contains = true;
                if(!contains) {
                    bw.write(content);
                    namesAndAddresses.add(nameAndAddress);
                }
            }
            //Closing BufferedWriter Stream
            bw.close();
            fw.close();


        }catch(IOException ioe){
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }
    }

    /**
     * @return Vector[String] containing name and addresses of the Cluster nodes
     */
    public Vector<String[]> list(){
        readFile();
        return namesAndAddresses;
    }
    //This class is used to send discovery messages
    private class SendDiscovery extends Thread{
        @Override
        public void run() {
            super.run();

            // Create the socket object for
            // carrying the data.
            DatagramSocket ds = null;
            InetAddress ip = null;
            try {
                ds = new DatagramSocket();
            } catch (SocketException e) {
                System.out.println("Connection failed.");
            }

            byte buffer[] = null;

                while (true) {
                    readFile();


                    // Create Sent string
                    String sentData = null;
                    StringBuilder inp = new StringBuilder();
                    for (int i = 0; i < namesAndAddresses.size(); i++) {
                        String[] tmp = namesAndAddresses.get(i);
                        inp.append(tmp[0]).append(" ").append(tmp[1]).append(",");
                    }
                    inp.append(Utility.getNodeName()).append(" ").append(Utility.getIP());
                    sentData = "discovery;" + inp.toString();

                    // Convert the String input into the byte array.
                    buffer = sentData.getBytes();

                    for (int i = 0; i < namesAndAddresses.size(); i++) {
                        try {
                            ip = InetAddress.getByName((namesAndAddresses.get(i))[1]);

                            // Create the datagramPacket for sending
                            // the data.
                            DatagramPacket DpSend =
                                    new DatagramPacket(buffer, buffer.length, ip, port);

                            // Invoke the send call to actually send
                            // the data.
                            ds.send(DpSend);
                            System.out.println("\u001B[33m" + "Discovery message sent" + "\u001B[0m");
                        } catch (UnknownHostException e) {
                            System.out.println("Unable to send discovery message; Unknown host");
                        } catch (IOException e) {
                            System.out.println("Unable to send discovery message, IOException");
                        }
                    }
                    if (sentData.equals("bye"))
                        break;
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        System.out.println("Sleep interrupted!");
                    }
                }
        }
    }

    /**
     * Start processing discovery message
     * @param received is raw discovery message
     */
    public void getDiscovery(String received){
        Thread get = new GetDiscovery(received);
        get.start();
    }
    // This class is used to process discovery messages
    private class GetDiscovery extends Thread{
        private String received;
        public GetDiscovery(String received){
            this.received = received;
        }
        @Override
        public void run(){
            super.run();

                System.out.println("\u001B[33m" + "Discovery message received!" + "\u001B[0m");
                Vector<String[]> tmp = new Vector<>();
                String[] input = received.split(",");

                for (int i = 0; i < input.length; i++) {
                    tmp.add(input[i].split(" "));
                }

                writeToFile(tmp);
        }
    }
}

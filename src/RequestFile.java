import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * This class is used to send or receive requests for files
 * @author Mohamad Chaman-Motlagh
 * @version 1
 */
public class RequestFile {
    private int port;
    private DatagramSocket ds;
    private Thread sendRequest;
    private final Object lock = new Object();
    private Queue<String> ports;
    private Queue<String> fileNames;
    private Queue<Vector<String[]>> addresses;
    /**
     * @param port which port to Listen to
     */
    public RequestFile(int port){
        this.port = port;
        this.ds = ds;
        ports = new LinkedList<String>();
        fileNames = new LinkedList<String>();
        addresses = new LinkedList<Vector<String[]>>();


        sendRequest = new SendRequest();
        sendRequest.start();
    }
    public void getRequest(String received){
        Thread get = new GetRequest(received);
        get.start();
    }
    // This class is used to get files requests messages
    private class GetRequest extends Thread{
        private String received;
        public GetRequest(String received){
            this.received = received;
        }
        @Override
        public void run(){


                String data[] = received.split(" ");
                String filename = data[0];
                String TCPPort = data[1];
                String remoteAddress = data[2];
                remoteAddress = remoteAddress.substring(1, remoteAddress.length());

                System.out.println("\u001B[34m" +
                        "Request received!\n" +
                        "File name: " + filename + "\n" +
                        "TCP port: " + TCPPort + "\n" +
                        "Source: " + remoteAddress.toString() + "\n" +
                        "\u001B[0m");
                netwolf.answerRequest(filename, TCPPort, remoteAddress);

        }
    }
    public void sendRequest(String fileName, String TCPPort, Vector<String[]> namesAndAddresses){
        synchronized (lock) {
            fileNames.add(fileName);
            ports.add(TCPPort);
            addresses.add(namesAndAddresses);
            lock.notify();
        }
    }
    //This class is used to send file requests
    private class SendRequest extends Thread{
        String TCPPort;
        String fileName;
        Vector<String[]> namesAndAddresses;
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
            synchronized (lock) {
                while (true) {
                    if (fileNames.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            System.out.println("Sending request failed.");
                        }
                    }

                    this.TCPPort = ports.remove();
                    this.fileName = fileNames.remove();
                    this.namesAndAddresses = addresses.remove();
                    byte buffer[] = null;

                    // Create Sent string
                    String sentData = null;
                    StringBuilder inp = new StringBuilder();
                    inp.append(fileName).append(" ").append(TCPPort);
                    sentData = "request;" + inp.toString();

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
                        } catch (UnknownHostException e) {
                            System.out.println("Unable to send request message; Unknown host");
                        } catch (IOException e) {
                            System.out.println("Unable to send request message, IOException");
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

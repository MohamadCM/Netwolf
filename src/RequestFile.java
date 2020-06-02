import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * This class is used to send or receive requests for files
 */
public class RequestFile {
    private int port;
    private String directoryAddress;

    Thread sendRequest;
    private Queue<String> ports;
    private Queue<String> fileNames;
    private Queue<Vector<String[]>> addresses;
    /**
     * @param port which port to Listen to
     * @param directoryAddress is the location of files directory on local machine
     */
    public RequestFile(int port, String directoryAddress){
        this.port = port;
        this.directoryAddress = directoryAddress;
        ports = new LinkedList<String>();
        fileNames = new LinkedList<String>();
        addresses = new LinkedList<Vector<String[]>>();

        Thread getRequest = new GetRequest();
        getRequest.start();

        sendRequest = new SendRequest();
        sendRequest.start();
    }

    // This class is used to get files requests messages
    private class GetRequest extends Thread{
        @Override
        public void run(){

            DatagramSocket ds = null; // Creating Socket to initiate connection
            try {
                ds = new DatagramSocket(port);
            } catch (SocketException e) {
                System.out.println("Connection failed");
            }
            byte[] received = new byte[65535];

            DatagramPacket DpReceive = null;
            while (true) // Receiving discovery message
            {

                // create a DatgramPacket to receive the data.
                DpReceive = new DatagramPacket(received, received.length);

                // receive the data in byte buffer.
                try {
                    ds.receive(DpReceive);
                    System.out.println("Data received");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String data[] = (Utility.convertToString(received)).split(" ");
                String filename = data[0];
                String TCPPort = data[1];

                // Exit the server if each client sends "bye"
                if (Utility.convertToString(received).toString().equals("bye"))
                {
                    System.out.println("Client sent bye.....EXITING");
                    break;
                }

                // Clear the buffer after every message.
                received = new byte[65535];
            }
        }
    }
    public void sendRequest(String fileName, String TCPPort, Vector<String[]> namesAndAddresses){
        fileNames.add(fileName);
        ports.add(TCPPort);
        addresses.add(namesAndAddresses);
        sendRequest.notify();
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
                System.out.println("Connection failed");
            }
            while (true) {
                if(fileName.isEmpty()){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Sending request");
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
                sentData = inp.toString();

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
                        System.out.println("Unable to send discovery message; Unknown host");
                    } catch (IOException e) {
                        System.out.println("Unable to send discovery message, IOException");
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

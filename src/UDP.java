import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP extends Thread{
    private int port;
    private Discovery discovery;
    private RequestFile requestFile;
    private static DatagramSocket ds;

    public UDP(int port, Discovery discovery, RequestFile requestFile){
        this.port = port;
        this.discovery = discovery;
        this.requestFile = requestFile;

        try {
            ds = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("UDP connection failed, \n" +
                    "Exiting");
            System.exit(0);
        }

    }
    @Override
    public void run() {
        while (true) {
            byte[] received = new byte[65535];

            DatagramPacket DpReceive = null;
            while (true) // Receiving discovery message
            {

                // create a DatgramPacket to receive the data.
                DpReceive = new DatagramPacket(received, received.length);

                // receive the data in byte buffer.
                try {
                    ds.receive(DpReceive);

                    String[] input = (Utility.convertToString(received)).split(";");
                    if (input[0].equals("discovery"))
                        discovery.getDiscovery(input[1]);
                    else if (input[0].equals("request"))
                        requestFile.getRequest(input[1] + " " + DpReceive.getAddress().toString());
                    //request
                } catch (IOException e) {
                    System.out.println("Failed to receive discovery message.");
                }
                // Clear the buffer after every message.
                received = new byte[65535];
            }
        }
    }
}

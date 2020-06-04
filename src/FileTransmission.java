import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is used to send or receive files
 * @author Mohamad Chaman-Motlagh
 * @version 1
 */
public class FileTransmission {
    private String directory;
    private int maximumServices;
    private Queue<String[]> requests;
    private int ACKTimeOut = 5000;

    private int waifForFileTimeOut;
    public FileTransmission(String directory, int maximumServices, int waifForFileTimeOut){
        this.directory = directory;
        this.maximumServices = maximumServices;
        this.waifForFileTimeOut = waifForFileTimeOut;
        requests = new LinkedList<String[]>();
    }
    public synchronized void sendFile(String fileName, String TCPPort, String remoteAddress){
        if(requests.size() < maximumServices) {
            String[] req = new String[3];
            req[0] = fileName;
            req[1] = TCPPort;
            req[2] = remoteAddress;
            requests.add(req);
            Thread send = new fileSender();
            send.start();
        }
    }
    public synchronized void receiveFile(String fileName, String TCPPort){
        Thread receive = new fileReceiver(fileName, TCPPort);
        receive.start();
    }
    private class fileSender extends Thread{
        @Override
        public void run(){
                String[] req;
                synchronized (requests){
                    req = requests.remove();
                }
                String fileName = req[0];
                int port = Integer.parseInt(req[1]);
            try {
                InetAddress dest = InetAddress.getByName(req[2]);

                boolean fileFound = Utility.findFile(fileName, new File(netwolf.getDirectory()));
                if(fileFound){
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(dest , port), ACKTimeOut);

                        OutputStream outToServer = socket.getOutputStream();
                        DataOutputStream out = new DataOutputStream(outToServer);
                        out.writeUTF("READY");


                        InputStream inFromServer = socket.getInputStream();
                        DataInputStream in = new DataInputStream(inFromServer);
                        String ACK = in.readUTF();
                        if(ACK.equals("OK")) { // Start sending file
                            System.out.println("\u001B[34m" + "Sending " + fileName + " to "  + socket.getRemoteSocketAddress() + " now" + "\u001B[0m");
                        }
                        socket.close();


                    } catch (IOException e) {
                        System.out.println("We're not the machine that's going to send " + fileName);
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    } private class fileReceiver extends Thread{
        private String fileName;
        private int port;
        public fileReceiver(String fileName, String port){
            this.port = Integer.parseInt(port);
            this.fileName = fileName;
        }
        @Override
        public void run(){
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(port);

                serverSocket.setSoTimeout(waifForFileTimeOut);

                Socket server = serverSocket.accept();

                DataInputStream in = new DataInputStream(server.getInputStream());

                String ack = in.readUTF();
                if(ack.equals("READY")) {
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF("OK");
                    //Receiving file here
                }
                server.close();
            } catch (IOException e) {
                System.out.println("Can't receive file");
            }

        }
    }
}

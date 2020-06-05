import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

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
    public synchronized void receiveFile(String fileName, String TCPPort, Vector<String[]> list){
        Thread receive = new fileReceiver(fileName, TCPPort, list);
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
                System.out.println("listening on " + Utility.getIP() + ":" + port + " TCP");

                File file = Utility.findFile(fileName, new File(netwolf.getDirectory()));
                boolean fileFound = file != null;
                if(fileFound){
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(dest , port), ACKTimeOut);

                        OutputStream outToServer = socket.getOutputStream();
                        DataOutputStream out = new DataOutputStream(outToServer);
                        out.writeUTF("READY," + file.length()); // Send ready and size of the file


                        InputStream inFromServer = socket.getInputStream();
                        DataInputStream in = new DataInputStream(inFromServer);
                        String ACK = in.readUTF();

                        if(ACK.equals("OK")) { // Start sending file
                            System.out.println("\u001B[34m" + "Sending " + fileName + " to "  + socket.getRemoteSocketAddress() + " now" + "\u001B[0m");

                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            FileInputStream fis = new FileInputStream(file);
                            byte[] buffer = new byte[4096];

                            while (fis.read(buffer) > 0) {
                                dos.write(buffer);
                            }

                            fis.close();
                            dos.close();

                        }
                        socket.close();


                    } catch (IOException e) {
                        System.out.println("\u001B[31m" + "We're not the machine that's going to send " + fileName + "\u001B[0m");
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    } private class fileReceiver extends Thread{
        private String fileName;
        private int port;
        private Vector<String[]> namesAndAddresses;
        public fileReceiver(String fileName, String port, Vector<String[]> namesAndAddresses){
            this.port = Integer.parseInt(port);
            this.fileName = fileName;
            this.namesAndAddresses = namesAndAddresses;
        }
        @Override
        public void run(){
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(port);

                serverSocket.setSoTimeout(waifForFileTimeOut);

                Socket server = serverSocket.accept();

                DataInputStream in = new DataInputStream(server.getInputStream());

                String[] ack = (in.readUTF()).split(",");
                if(ack[0].equals("READY")) {
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF("OK");
                    //Receiving file here
                    String address = server.getRemoteSocketAddress().toString();
                    address = address.substring(1, address.length() - 1);
                    address = address.split(":")[0];
                    String nodeName = "";
                    for(String[] st: namesAndAddresses){
                        if(st[1].equals(address))
                            nodeName = st[0];
                    }
                    System.out.println("Getting " + fileName +  " from " + nodeName);

                    DataInputStream dis = new DataInputStream(server.getInputStream());
                    FileOutputStream fos = new FileOutputStream(directory + "/" + fileName);
                    byte[] buffer = new byte[2048];

                    int fileSize = Integer.parseInt(ack[1]); // Send file size in separate msg
                    int read = 0;
                    int totalRead = 0;
                    int remaining = fileSize;
                    while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                        totalRead += read;
                        remaining -= read;
                        System.out.println("Read " + totalRead + " bytes.");
                        fos.write(buffer, 0, read);
                    }
                    System.out.println("\u001B[32m" + fileName + " Received!" + "\u001B[0m");
                    fos.close();
                    dis.close();
                }
                server.close();
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Can't receive file.");
            }

        }
    }
}

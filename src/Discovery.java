import java.io.*;
import java.util.Vector;

/**
 * This class is used to send and receive discovery message through network
 * @author Mohamad Chaman-Motlagh
 * @version 1
 */
public class Discovery {
    private boolean locked = false;
    private String filename;
    private Vector<String[]> namesAndAddresses;
    /**
     * @param filename is cluster-file
     * Starts sending and receiving Discovery messages
     */
    public Discovery(String filename){
        this.filename = filename;
        Thread sendDiscovery = new SendDiscovery();
        namesAndAddresses = new Vector<String[]>();

        sendDiscovery.run();
    }
    // Read file and updates namesAndAddresses Vector
    private synchronized void readFiles(){

        namesAndAddresses.clear();
        File file = new File(filename);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));


            String st;
            while ((st = bufferedReader.readLine()) != null) {
                String[] split = st.split(" ");
                namesAndAddresses.add(split);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Vector[String] containing name and addresses of the Cluster nodes
     */
    public Vector<String[]> list(){
        readFiles();
        return namesAndAddresses;
    }
    private class SendDiscovery extends Thread{
        @Override
        public void run() {
            super.run();
            readFiles();


        }
    }
}

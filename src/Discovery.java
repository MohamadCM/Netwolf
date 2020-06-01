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
        namesAndAddresses = new Vector<String[]>();

        Thread sendDiscovery = new SendDiscovery();
        sendDiscovery.run();
        Thread getDiscovery = new GetDiscovery();
        getDiscovery.run();
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
                String[] split = st.split(" ");
                namesAndAddresses.add(split);
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
                    if(tmp[0].equals(nameAndAddress[0]) && tmp[1].equals(nameAndAddress[1]))
                        contains = true;
                }
                if(!contains) {
                    bw.write(content);
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
            readFile();


        }
    }
    // This class is used to get discovery messages
    private class GetDiscovery extends Thread{
        @Override
        public void run(){
            super.run();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            Vector<String[]> tmp = new Vector<>();
            String[] st = new String[2];
            st[0] = "N3";
            st[1] = "192.168.1.3";
            tmp.add(st);
            writeToFile(tmp);
        }
    }
}

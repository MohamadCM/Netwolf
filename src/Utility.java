import java.io.File;

/**
 * Utility class
 * @author Mohamad Chaman-Motlagh
 * @version 1
 */
public class Utility {
     // IP of the current machine

    private static String IP;
    private static String nodeName;

    /**
     * A utility method to convert the byte array
     * data into a string representation.
     * @param input is the input byte
     * @return input converted to String
     */
    public static String convertToString(byte[] input)
    {
        if (input == null)
            return null;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (input[i] != 0)
        {
            result.append((char) input[i]);
            i++;
        }
        return result.toString();
    }

    /**
     * Finds file in given directory
     * @param name is name of the file
     * @param directory is directory for search
     * @return file
     */
    public static File findFile(String name, File directory)
    {
        File[] list = directory.listFiles();
        File result = null;
        boolean found = false;
        if(list!=null)
            for (File file : list)
            {
                if (!file.isDirectory())
                {
                    String filePath = file.toString().replace('\\', '/');
                    String[] tmp = filePath.split("/");
                    found = tmp[tmp.length - 1].equals(name);
                    if (found) {
                        result = file;
                        break;
                    }
                }
            }
        return result;
    }

    /**
     * @return current machine's IP
     */
    public static String getIP() {
        return IP;
    }

    /**
     * @param IP is current machine's IP
     */
    public static void setIP(String IP) {
        Utility.IP = IP;
    }

    /**
     * @return current machine's node name
     */
    public static String getNodeName() {
        return nodeName;
    }
    /**
     * @param nodeName is current machine's node-name
     */
    public static void setNodeName(String nodeName) {
        Utility.nodeName = nodeName;
    }
}

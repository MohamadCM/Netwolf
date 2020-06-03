public class Utility {
     // IP of the current machine

    private static String IP;

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
}

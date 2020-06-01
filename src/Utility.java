public class Utility {
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
}

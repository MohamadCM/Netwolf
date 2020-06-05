import java.util.Scanner;

public class CommandLineInterface extends Thread {
    private Discovery discovery;
    private RequestFile requestFile;
    private FileTransmission fileTransmission;
    public CommandLineInterface(Discovery discovery, RequestFile requestFile, FileTransmission fileTransmission){
        this.discovery = discovery;
        this.requestFile = requestFile;
        this.fileTransmission = fileTransmission;
    }
    @Override
    public void run() {
        super.run();
        System.out.println("Use exit or quit to finish the program!");
        while (true){
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] splitted = input.split(" ");
            if(splitted[0].equalsIgnoreCase("List"))
                netwolf.printList();
            else if(splitted[0].equalsIgnoreCase("get") && splitted.length > 1)
                netwolf.sendRequest(splitted[1], netwolf.getList());
            else if(splitted[0].equalsIgnoreCase("exit") || splitted[0].equalsIgnoreCase("quit") )
                System.exit(0);
            else
                System.out.println("\u001B[31m" + "Wrong input" + "\u001B[0m");
        }
    }
}

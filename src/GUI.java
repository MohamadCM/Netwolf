import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

public class GUI extends JFrame {
    private Discovery discovery;
    private RequestFile requestFile;
    private FileTransmission fileTransmission;
    public static JTextArea transitionLog;

    public GUI(Discovery discovery, RequestFile requestFile, FileTransmission fileTransmission, String clusterFileName, String directory, int port,
               int waitForFileTimeOutSeconds, int discoveryIntervalSeconds, int maximumServices, String currentNodeName){

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Can't show GUI.");
            System.exit(0);
        }
        transitionLog = new JTextArea("Transition log\n");
        transitionLog.setToolTipText("Console output appears here!");
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(transitionLog);
        //what comes here to auto scroll? so the bottom of the jtextarea will be seen? (currently I have to manually scrolldown to see new data)
        scrollPanel.add(scroll, BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret)transitionLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.discovery = discovery;
        this.requestFile = requestFile;
        this.fileTransmission = fileTransmission;

        setTitle("Netwolf");
        setSize(480, 720);
        //this.pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        int border = 10;
        getRootPane().setBorder(BorderFactory.createMatteBorder(border, border, border, border, Color.DARK_GRAY));


        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JPanel clusterFilePane = new JPanel();
        clusterFilePane.setLayout(new BoxLayout(clusterFilePane, BoxLayout.X_AXIS));
        JLabel clusterFileLabel = new JLabel("Cluster File name:    ");
        JTextField clusterFileTextField = new JTextField();
        clusterFileTextField.setEditable(false);
        clusterFileTextField.setText(clusterFileName);
        clusterFilePane.add(clusterFileLabel);
        clusterFilePane.add(clusterFileTextField);
        southPanel.add(clusterFilePane);

        southPanel.add(new JLabel(" "));

        JPanel directoryPane = new JPanel();
        directoryPane.setLayout(new BoxLayout(directoryPane, BoxLayout.X_AXIS));
        JLabel directoryLabel = new JLabel("Service directory:     ");
        JTextField directoryTextField = new JTextField();
        directoryTextField.setEditable(false);
        directoryTextField.setText(directory);
        directoryPane.add(directoryLabel);
        directoryPane.add(directoryTextField);
        southPanel.add(directoryPane);

        southPanel.add(new JLabel(" "));



        JPanel portPane = new JPanel();
        portPane.setLayout(new BoxLayout(portPane, BoxLayout.X_AXIS));
        JLabel disLabel = new JLabel("Discovery Interval time: ");
        JTextField disPort = new JTextField();
        disPort.setEditable(false);
        disPort.setText(String.valueOf(discoveryIntervalSeconds / 1000));
        JLabel timeoutLabel = new JLabel("File request timeout: ");
        JTextField timeout = new JTextField();
        timeout.setEditable(false);
        timeout.setText(String.valueOf(waitForFileTimeOutSeconds / 1000));
        portPane.add(disLabel);
        portPane.add(disPort);
        portPane.add(new JLabel("                   "));
        portPane.add(timeoutLabel);
        portPane.add(timeout);
        southPanel.add(portPane);

        JPanel service = new JPanel();
        service.setLayout(new BoxLayout(service, BoxLayout.X_AXIS));
        JLabel maximumServiceLabel = new JLabel("Maximum simultaneous services: ");
        JTextField maximumService = new JTextField();
        maximumService.setEditable(false);
        maximumService.setText(String.valueOf(maximumServices));
        service.add(maximumServiceLabel);
        service.add(maximumService);
        southPanel.add(service);

        JPanel nodePane = new JPanel();
        nodePane.setLayout(new BoxLayout(nodePane, BoxLayout.X_AXIS));
        JLabel nodeNameLabel = new JLabel("Node name:");
        JTextField nodeName = new JTextField();
        nodeName.setEditable(false);
        nodeName.setText(currentNodeName);
        JLabel IPLabel = new JLabel("Local IP: ");
        JTextField IP = new JTextField();
        IP.setEditable(false);
        IP.setText(Utility.getIP());
        JLabel UDPPortLabel = new JLabel("UDP Port: ");
        JTextField UDPPort = new JTextField();
        UDPPort.setEditable(false);
        UDPPort.setText(String.valueOf(port));
        nodePane.add(nodeNameLabel);
        nodePane.add(nodeName);
        nodePane.add(IPLabel);
        nodePane.add(IP);
        nodePane.add(UDPPortLabel);
        nodePane.add(UDPPort);

        southPanel.add(nodePane);
        southPanel.add(new JLabel(" "));


        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        JButton refreshListBtn = new JButton("Refresh List");
        refreshListBtn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
        JTextArea list = new JTextArea("Cluster list");
        updateList(list);
        refreshListBtn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                updateList(list);
            }
            @Override public void mousePressed(MouseEvent mouseEvent) { }
            @Override public void mouseReleased(MouseEvent mouseEvent) { }
            @Override public void mouseEntered(MouseEvent mouseEvent) { }
            @Override public void mouseExited(MouseEvent mouseEvent) { }
        });
        list.setMaximumSize(new Dimension(100, 100));
        list.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
        list.setEditable(false);
        list.setToolTipText("List of names and addresses");
        northPanel.add(refreshListBtn, BorderLayout.WEST);
        northPanel.add(list, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JPanel reqPanel = new JPanel();
        reqPanel.setLayout(new BorderLayout());
        JLabel req = new JLabel("Request Files: ");
        JTextArea reqFile = new JTextArea();
        JButton reqSend = new JButton("Dispatch request");
        reqSend.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                System.out.println("Get " + reqFile.getText());
                netwolf.sendRequest(reqFile.getText(), netwolf.getList());
            }
            @Override public void mousePressed(MouseEvent mouseEvent) { }
            @Override public void mouseReleased(MouseEvent mouseEvent) { }
            @Override public void mouseEntered(MouseEvent mouseEvent) { }
            @Override public void mouseExited(MouseEvent mouseEvent) { }
        });
        reqPanel.add(req, BorderLayout.WEST);
        reqPanel.add(reqFile, BorderLayout.CENTER);
        reqPanel.add(reqSend, BorderLayout.EAST);
        reqPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
        centerPanel.add(reqPanel, BorderLayout.NORTH);

        transitionLog.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
        transitionLog.setEditable(false);
        centerPanel.add(scrollPanel, BorderLayout.CENTER);

        add(southPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

    }

    private void updateList(JTextArea list){
        Vector<String[]> col = netwolf.getList();
        StringBuilder res = new StringBuilder();
        for(String[] nameAdd : col){
            res.append(nameAdd[0]).append(" ").append(nameAdd[1]).append("\n");
        }
        list.setText(res.toString());
    }
}

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    public GUI(){
        setTitle("Netwolf");
        setSize(480, 720);
        //this.pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        int border = 10;
        getRootPane().setBorder(BorderFactory.createMatteBorder(border, border, border, border, Color.DARK_GRAY));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Can't show GUI");
        }


        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JPanel clusterFilePane = new JPanel();
        clusterFilePane.setLayout(new BoxLayout(clusterFilePane, BoxLayout.X_AXIS));
        JLabel clusterFileLabel = new JLabel("Cluster File name:    ");
        JTextField clusterFileTextField = new JTextField();
        clusterFilePane.add(clusterFileLabel);
        clusterFilePane.add(clusterFileTextField);
        northPanel.add(clusterFilePane);

        northPanel.add(new JLabel(" "));

        JPanel directoryPane = new JPanel();
        directoryPane.setLayout(new BoxLayout(directoryPane, BoxLayout.X_AXIS));
        JLabel directoryLabel = new JLabel("Service directory:     ");
        JTextField directoryTextField = new JTextField();
        directoryPane.add(directoryLabel);
        directoryPane.add(directoryTextField);
        northPanel.add(directoryPane);

        northPanel.add(new JLabel(" "));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BorderLayout());
        JButton startBtn = new JButton("Start/ Restart");
        buttonPane.add(startBtn);
        northPanel.add(buttonPane);

        JPanel portPane = new JPanel();
        portPane.setLayout(new BoxLayout(portPane, BoxLayout.X_AXIS));
        JLabel disPortLabel = new JLabel("Discovery port: ");
        JSpinner disPort = new JSpinner();
        JLabel reqPortLabel = new JLabel("Request port: ");
        JSpinner reqPort = new JSpinner();
        portPane.add(disPortLabel);
        portPane.add(disPort);
        portPane.add(new JLabel("                     "));
        portPane.add(reqPortLabel);
        portPane.add(reqPort);
        northPanel.add(portPane);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        JButton refreshListBtn = new JButton("Refresh List");
        refreshListBtn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
        JTextArea list = new JTextArea("Cluster list");
        list.setMaximumSize(new Dimension(100, 100));
        list.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
        list.disable();
        middlePanel.add(refreshListBtn, BorderLayout.WEST);
        middlePanel.add(list, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        JPanel reqPanel = new JPanel();
        reqPanel.setLayout(new BorderLayout());
        JLabel req = new JLabel("Request Files: ");
        JTextArea reqFile = new JTextArea();
        JButton reqSend = new JButton("Dispatch request");
        reqPanel.add(req, BorderLayout.WEST);
        reqPanel.add(reqFile, BorderLayout.CENTER);
        reqPanel.add(reqSend, BorderLayout.EAST);
        reqPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
        southPanel.add(reqPanel, BorderLayout.NORTH);

        JTextArea transitionLog = new JTextArea("Transition log");
        transitionLog.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
        southPanel.add(transitionLog, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        //  this.pack();
        setVisible(true);
        //this.star
    }
}

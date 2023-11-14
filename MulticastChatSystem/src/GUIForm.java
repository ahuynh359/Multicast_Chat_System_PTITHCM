import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUIForm {
    private JTextField tfGroupName;
    private JTextField tfPort;
    private JTextField tfName;
    private JPanel panelParent;
    private JPanel panelGroupPort;
    private JPanel panelName;
    private JLabel lbGroupName;
    private JLabel lbPort;
    private JLabel lbName;
    private JPanel panelMessage;
    private JTextArea taMessage;
    private JLabel lbMessage;
    private JPanel panelCrypto;
    private JPanel panelChat;
    private JTextField tfMessage;
    private JButton btnSend;
    private JButton btnJoinChat;
    private JButton btnLeaveChat;
    private JButton btnExit;
    private JCheckBox cmbEncrypt;
    private JTextField tfKey;
    private JLabel lbKey;
    private JLabel lbSendMessage;

    private MulticastSender multicastSender;
    private MulticastListener multicastListener;
    private ArrayList<String> messageArray;
    private int currentNumMessage;

    public GUIForm() {

        init();
        handleEvent();



    }

    private void init() {
        messageArray = new ArrayList<>();
        messageArray.add("Join chat to begin conversation");
        taMessage.setText("Join chat to begin conversation");

        currentNumMessage = 0;

        //Send
        multicastSender = new MulticastSender();

        //Listen
        String groupName = tfGroupName.getText();
        int port = Integer.parseInt(tfPort.getText());
        String name = tfName.getText();
        multicastListener = new MulticastListener(name, groupName, port, new ArrayList<String>(), currentNumMessage);


        //Every 7s update chat box
        UpdateSystem updateSystem = new UpdateSystem();
        Timer timer = new Timer(700, updateSystem);
        timer.start();



    }

    private void handleEvent() {
        //Btn Join Chat
        btnJoinChat.addActionListener(e -> {
            if (handleErrorBeforeConver()) {

                btnJoinChat.setEnabled(false);

                //Get info from text field
                String groupName = tfGroupName.getText();
                int port = Integer.parseInt(tfPort.getText());
                String name = tfName.getText();

                //Send and receive message
                multicastSender.sendMessage("\n\n" + name + " is connected to chat at " + groupName + "\n", groupName, port);
                multicastListener = new MulticastListener(name, groupName, port, messageArray, currentNumMessage);
                multicastListener.start();

                //Current Number
                currentNumMessage = multicastListener.totalMessages();

                //Set UI
                btnSend.setEnabled(true);
                btnLeaveChat.setEnabled(true);
                togglePanelSendMessage(true);
                togglePanelEncrypt(true);
                togglePanelIp(false);
                togglePanelName(false);


            }

        });

        //Btn Leave Chat
        btnLeaveChat.addActionListener(e -> {
            btnJoinChat.setEnabled(true);

            try {
                multicastListener.stopProgram();
                String mess = tfName.getText() + " is disconnecting from chat! \n";


                multicastSender.sendMessage(mess, tfGroupName.getText(), Integer.parseInt(tfPort.getText()));
                multicastListener.join(1000);
            } catch (Exception err){
                err.printStackTrace();
            }

            //Set UI
            btnSend.setEnabled(false);
            btnLeaveChat.setEnabled(false);
            togglePanelSendMessage(false);
            togglePanelEncrypt(false);
            togglePanelIp(true);
            togglePanelName(true);
        });

        //Btn send message
        btnSend.addActionListener(e -> {
            String mess = tfName.getText() + ": " + tfMessage.getText();
            multicastSender.sendMessage(mess,tfGroupName.getText(),Integer.parseInt(tfPort.getText()));
            tfMessage.setText("");
        });

        //Btn Exit
        btnExit.addActionListener(e -> {
            if(btnLeaveChat.isEnabled()){
                multicastListener.stopProgram();
                try {
                    multicastListener.join(500);
                }catch (Exception err){
                    err.printStackTrace();
                }
                String mess = tfName.getText() + " has exited chart!";
                multicastSender.sendMessage(mess,tfGroupName.getText(),Integer.parseInt(tfPort.getText()));
            }
            System.exit(0);
        });
    }

    private boolean handleErrorBeforeConver() {

        if (tfGroupName.getText().isEmpty()) {
            createErrorPane("Do not leave group name empty");
            return false;
        }

        if (tfPort.getText().isEmpty()) {
            createErrorPane("Do not leave port empty");
            return false;
        }

        if (tfName.getText().isEmpty()) {
            createErrorPane("Do not leave name empty");
            return false;
        }
        return true;

    }

    private void createErrorPane(String errorMessage) {
        JOptionPane.showMessageDialog(
                panelParent,
                errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void togglePanelName(Boolean toggle) {
        lbName.setEnabled(toggle);
        tfName.setEnabled(toggle);
    }

    private void togglePanelIp(Boolean toggle) {
        lbPort.setEnabled(toggle);
        lbGroupName.setEnabled(toggle);
        tfPort.setEnabled(toggle);
        tfGroupName.setEnabled(toggle);
    }

    private void togglePanelSendMessage(Boolean toggle) {
        lbSendMessage.setEnabled(toggle);
        tfMessage.setEnabled(toggle);

    }

    private void togglePanelEncrypt(Boolean toggle) {
        lbKey.setEnabled(toggle);
        cmbEncrypt.setEnabled(toggle);
        tfKey.setEnabled(toggle);
    }

    public void startGUI() {
        JFrame frame = new JFrame("Multicast Chat Room");
        frame.setSize(800, 600);
        frame.add(panelParent);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class UpdateSystem implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {


                messageArray = multicastListener.getMessages();
                int numMessage = multicastListener.totalMessages();
                while (currentNumMessage != numMessage) {
                    String mess = messageArray.get(currentNumMessage++);
                    taMessage.setText(taMessage.getText() + mess + "\n");
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}

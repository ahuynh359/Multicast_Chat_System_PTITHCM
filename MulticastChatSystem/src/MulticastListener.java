import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MulticastListener extends Thread {

    private final byte[] buffer = new byte[256];
    private String userName;
    private final String groupName;

    private final int port;
    private ArrayList<String> messages;
    private volatile Boolean running;

    private volatile int numOfMessage;
    private volatile static Set<InetAddress> participants = new HashSet<>();

    public MulticastListener(String userName, String groupName, int port, ArrayList<String> messages, int numOfMessage) {
        this.userName = userName;
        this.groupName = groupName;
        this.port = port;
        this.messages = messages;
        this.numOfMessage = numOfMessage;
        this.running = true;


    }

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(groupName);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(group);


            participants.add(group);



            messages.add("\n\n" + userName + " is connected to chat at " + groupName + "\n");
            numOfMessage++;
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);



                String message = new String(packet.getData(), 0, packet.getLength());

                messages.add(message);
                numOfMessage++;


            }
            socket.leaveGroup(group);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<String> getMessages() {
        return messages;
    }

    public synchronized int totalMessages() {
        return numOfMessage;
    }

    public synchronized void stopProgram() {
        running = false;
    }

    public synchronized void addMessage(String message) {
        messages.add(message);
        numOfMessage++;
    }

    public synchronized String participants(){
        return String.valueOf(participants.size());
    }
}

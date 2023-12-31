import javax.swing.*;
import java.awt.dnd.DragGestureEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastSender {

    public boolean sendMessage(String message, String groupName, int port) {
        byte[] buffer = message.getBytes();

        try {
            InetAddress group = InetAddress.getByName(groupName);
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            socket.send(packet);
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}

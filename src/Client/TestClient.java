package Client;

import javax.swing.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
    static String port="4907";
    public static void main(String[] args) throws UnknownHostException {
        Object[] possibleValues = { "localhost", "Serveur 2", "Serveur 3" };
        String hostName = (String) JOptionPane.showInputDialog(null, "Choisissez le nom du serveur", "Connexion au serveur", JOptionPane.QUESTION_MESSAGE, null, possibleValues, possibleValues[0]);
        InetAddress ip= InetAddress.getByName(hostName);
        new TestClient().initialize(ip.getHostAddress(), Integer.parseInt(port));
    }

    public void initialize(String ip, int port) {
        try {
            Socket sc=new Socket(ip,port);
            System.out.println("connection en cours avec serveur...");
            Auth frame1=new Auth(sc);
            frame1.setSize(400,80);
            frame1.setLocation(500,300);
            frame1.setVisible(true);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}

package Client;
import Server.RemoteScreen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            RemoteScreen remoteScreen = (RemoteScreen) registry.lookup("RemoteScreen");

            JFrame frame = new JFrame("Remote Screen Viewer");
            JLabel label = new JLabel();
            frame.add(label);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // Continuously capture and display the screen
            while (true) {
                byte[] screenData = remoteScreen.captureScreen();
                BufferedImage screenshot = ImageIO.read(new ByteArrayInputStream(screenData));
                label.setIcon(new ImageIcon(screenshot));

                frame.repaint();

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package Client;

import Server.RemoteScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Viewer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            RemoteScreen remoteScreen = (RemoteScreen) registry.lookup("RemoteScreen");

            // Create a JFrame to display the captured screen and cursor
            JFrame frame = new JFrame("Remote Screen Viewer");
            JLabel screenLabel = new JLabel();
            JLabel cursorLabel = new JLabel();
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(screenLabel, BorderLayout.CENTER);
            panel.add(cursorLabel, BorderLayout.CENTER);
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // Continuously capture and display the screen and cursor
            while (true) {
                // Call the remote method to capture the screen image
                byte[] screenData = remoteScreen.captureScreen();
                // Convert byte array to BufferedImage
                BufferedImage screenImage = ImageIO.read(new ByteArrayInputStream(screenData));
                // Update the JLabel with the new image
                screenLabel.setIcon(new ImageIcon(screenImage));

                // Call the remote method to get the cursor position
                Point cursorPosition = remoteScreen.getCursorPosition();
                cursorLabel.setLocation(cursorPosition);
                frame.repaint();
                
                Thread.sleep(1000); // Adjust the delay as needed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

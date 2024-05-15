package Server;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenReceiver {
    public static void receiveAndDisplayScreen(int serverPort) {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            JFrame frame = new JFrame("Screen Viewer");
            JLabel label = new JLabel();

            frame.add(label);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            while (true) {
                serverSocket.accept();
                BufferedImage screenshot = captureScreen();
                label.setIcon(new ImageIcon(screenshot));
            }
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage captureScreen() throws AWTException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(0, 0, 800, 600);
        return robot.createScreenCapture(screenRect);
    }
}

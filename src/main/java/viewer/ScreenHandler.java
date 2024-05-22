package viewer;

import remote.RemoteScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class ScreenHandler {
    private RemoteScreen remoteScreen;
    private JLabel screenLabel;
    private JProgressBar progressBar;
    private Timer screenUpdateTimer;
    private double scaleX;
    private double scaleY;

    public ScreenHandler(RemoteScreen remoteScreen, JFrame frame) {
        this.remoteScreen = remoteScreen;
        screenLabel = new JLabel();
        progressBar = new JProgressBar();
        initializeScale(frame);
    }

    public JLabel getScreenLabel() {
        return screenLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public Dimension getRemoteScreenSize() {
        try {
            return remoteScreen.getScreenSize();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startScreenUpdateTimer() {
        screenUpdateTimer = new Timer(300, e -> updateScreen());
        screenUpdateTimer.start();
    }

    private void updateScreen() {
        try {
            byte[] screenData = remoteScreen.captureScreen();
            ByteArrayInputStream bais = new ByteArrayInputStream(screenData);
            BufferedImage image = ImageIO.read(bais);

            if (screenLabel.getWidth() > 0 && screenLabel.getHeight() > 0) {
                Image scaledImage = image.getScaledInstance(screenLabel.getWidth(), screenLabel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImage);
                screenLabel.setIcon(icon);
            }
            screenLabel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error capturing remote screen: " + e.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeScale(JFrame frame) {
        Dimension localScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension remoteScreenSize = getRemoteScreenSize();

        if (remoteScreenSize != null) {
            double widthScale = localScreenSize.getWidth() / remoteScreenSize.getWidth();
            double heightScale = localScreenSize.getHeight() / remoteScreenSize.getHeight();
            double scale = Math.min(widthScale, heightScale);
            scaleX = scale;
            scaleY = scale;
        }
    }

    public Point mapLocalToRemoteCursor(Point localCursor, Dimension localScreen, Dimension remoteScreen) {
        double relativeX = (double) localCursor.x / localScreen.width;
        double relativeY = (double) localCursor.y / localScreen.height;
        int remoteX = (int) (relativeX * remoteScreen.width);
        int remoteY = (int) (relativeY * remoteScreen.height);
        return new Point(remoteX, remoteY);
    }
}

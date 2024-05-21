package Client.utils;

import Server.RemoteScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class ScreenUpdateHandler extends SwingWorker<Void, Void> {
    private final RemoteScreen remoteScreen;
    private final JLabel screenLabel;
    private final JProgressBar progressBar;

    public ScreenUpdateHandler(RemoteScreen remoteScreen, JLabel screenLabel, JProgressBar progressBar) {
        this.remoteScreen = remoteScreen;
        this.screenLabel = screenLabel;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground() {
        while (true) {
            try {
                byte[] imageData = remoteScreen.captureScreen();
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
                ImageIcon icon = new ImageIcon(image);
                screenLabel.setIcon(icon);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            try {
                Thread.sleep(1000 / 30); // 30 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        return null;
    }

    @Override
    protected void done() {
        progressBar.setIndeterminate(false);
    }
}

package Sender;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DesktopScreenImpl extends UnicastRemoteObject implements DesktopScreen{
    private Robot robot;
    private Clip clip;
    private TargetDataLine targetDataLine;
    private List<String> messages;

    public DesktopScreenImpl() throws RemoteException, LineUnavailableException {
        try {
            robot = new Robot();
            // Initialize the audio clip
            clip = AudioSystem.getClip();
            this.messages = new ArrayList<>();
            // Get a target data line for the microphone
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
        } catch (AWTException | LineUnavailableException e) {
            throw new RemoteException("Error initializing Robot or audio line", e);
        }
    }

    @Override
    public Dimension getScreenSize() throws RemoteException {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    @Override
    public byte[] captureScreen() throws RemoteException {
        BufferedImage screenImage = robot.createScreenCapture(new Rectangle(getScreenSize()));
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(screenImage, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RemoteException("Error capturing screen", e);
        }
    }
}


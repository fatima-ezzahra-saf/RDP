package remote;

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

public class RemoteScreenImpl extends UnicastRemoteObject implements RemoteScreen {
    private Robot robot;
    private Clip clip;
    private TargetDataLine targetDataLine;
    private List<String> messages;

    public RemoteScreenImpl() throws RemoteException, LineUnavailableException {
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

    @Override
    public void pressKey(int keyCode) throws RemoteException {
        robot.keyPress(keyCode);
    }

    @Override
    public void releaseKey(int keyCode) throws RemoteException {
        robot.keyRelease(keyCode);
    }

    @Override
    public void typeKey(char keyChar) throws RemoteException {
        // Simulate typing by pressing and releasing the corresponding key code
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(keyChar);
        pressKey(keyCode);
        releaseKey(keyCode);
    }

    @Override
    public void pressMouseButton(int button) throws RemoteException {
        int buttonToPress;
        switch (button) {
            case MouseEvent.BUTTON1:
                buttonToPress = InputEvent.BUTTON1_DOWN_MASK;
                break;
            case MouseEvent.BUTTON3:
                buttonToPress = InputEvent.BUTTON3_DOWN_MASK;
                break;
            default:
                throw new RemoteException("Unsupported mouse button: " + button);
        }
        robot.mousePress(buttonToPress);
    }

    @Override
    public void releaseMouseButton(int button) throws RemoteException {
        int buttonToRelease;
        switch (button) {
            case MouseEvent.BUTTON1:
                buttonToRelease = InputEvent.BUTTON1_DOWN_MASK;
                break;
            case MouseEvent.BUTTON3:
                buttonToRelease = InputEvent.BUTTON3_DOWN_MASK;
                break;
            default:
                throw new RemoteException("Unsupported mouse button: " + button);
        }
        robot.mouseRelease(buttonToRelease);
    }



    @Override
    public ArrayList<String> getMessages() throws RemoteException {
        if (messages != null) {
            System.out.println("Returning messages: " + messages); // Debug statement
            return new ArrayList<>(messages);
        } else {
            return new ArrayList<>(); // Return an empty list if messages is null
        }
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        if (messages != null) {
            System.out.println("Adding message: " + message); // Debug statement
            messages.add(message); // Add the message to the list
        } else {
            messages = new ArrayList<>(); // Initialize the list if it's null
            messages.add(message); // Add the message to the newly initialized list
        }
    }


    @Override
    public void clickMouse(int button) throws RemoteException {
        int buttonMask;
        switch (button) {
            case MouseEvent.BUTTON1:
                buttonMask = InputEvent.BUTTON1_DOWN_MASK;
                break;
            case MouseEvent.BUTTON2:
                buttonMask = InputEvent.BUTTON2_DOWN_MASK;
                break;
            case MouseEvent.BUTTON3:
                buttonMask = InputEvent.BUTTON3_DOWN_MASK;
                break;
            default:
                throw new RemoteException("Unsupported mouse button: " + button);
        }

        robot.mousePress(buttonMask);
        robot.mouseRelease(buttonMask);
    }

    @Override
    public void moveCursor(int x, int y) throws RemoteException {
        robot.mouseMove(x, y);
    }

    @Override
    public void dragMouse(int x, int y) throws RemoteException {
        robot.mouseMove(x, y);
    }

    @Override
    public void sendFile(String fileName, byte[] data) throws RemoteException {
        try {
            Files.write(Paths.get(fileName), data);
        } catch (IOException e) {
            throw new RemoteException("Error saving file", e);
        }
    }

    @Override
    public byte[] receiveFile(String fileName) throws RemoteException {
        try {
            return Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            throw new RemoteException("Error reading file", e);
        }
    }

    @Override
    public void startAudioStream() throws RemoteException {
        try {
            // Get the audio input stream from the microphone
            InputStream inputStream = getAudioInputStream(targetDataLine);

            // Specify the audio format of the microphone
            AudioFormat audioFormat = targetDataLine.getFormat();

            // Open the audio input stream
            AudioInputStream audioInputStream = new AudioInputStream(inputStream, audioFormat, AudioSystem.NOT_SPECIFIED);

            // Open the audio clip with the audio input stream
            clip.open(audioInputStream);

            // Start playback
            clip.start();
        } catch (LineUnavailableException | IOException e) {
            throw new RemoteException("Error starting audio stream", e);
        }
    }

    @Override
    public void stopAudioStream() throws RemoteException {
        try {
            // Stop the audio clip playback
            clip.stop();
        } catch (Exception e) {
            throw new RemoteException("Error stopping audio stream", e);
        }
    }

    public static InputStream getAudioInputStream(TargetDataLine targetDataLine) throws IOException {
        long burden = Long.parseLong(System.getProperty("java.specification.version"));
        int bytesRead;
        byte[] buffer = new byte[Math.min(96000, (int) burden * 40)];
        InputStream byteStream;

        bytesRead = targetDataLine.read(buffer, 0, buffer.length);

        if (bytesRead < 0)
            return null;

        byteStream = new ByteArrayInputStream(Arrays.copyOf(buffer, bytesRead));

        return byteStream;
    }
}

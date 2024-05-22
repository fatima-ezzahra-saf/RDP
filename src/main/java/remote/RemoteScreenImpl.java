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

public class RemoteScreenImpl extends UnicastRemoteObject implements RemoteScreen {
    private Robot robot;

    public RemoteScreenImpl() throws RemoteException {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RemoteException("Error initializing Robot", e);
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
}
package Server;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RemoteScreenImpl extends UnicastRemoteObject implements RemoteScreen {
    private final Robot robot;

    protected RemoteScreenImpl() throws RemoteException {
        super();
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            throw new RemoteException("Failed to initialize Robot", e);
        }
    }

    @Override
    public byte[] captureScreen() throws RemoteException {
        try {
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle rectangle = new Rectangle(dimension);
            BufferedImage screenshot = this.robot.createScreenCapture(rectangle);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to capture screen.", e);
        }
    }

    @Override
    public Dimension getScreenSize() throws RemoteException {
        return Toolkit.getDefaultToolkit().getScreenSize().getSize();
    }

    @Override
    public void moveCursor(int x, int y) throws RemoteException {
        this.robot.mouseMove(x, y);
    }

    @Override
    public void clickMouse(int x, int y) throws RemoteException {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    @Override
    public void pressKey(int keyCode) throws RemoteException {
        this.robot.keyPress(keyCode);
    }

    @Override
    public void releaseKey(int keyCode) throws RemoteException {
        this.robot.keyRelease(keyCode);
    }

    @Override
    public void pressMouseButton(int button) throws RemoteException {
        int buttonMask = getMouseButtonMask(button);
        if (buttonMask != -1) {
            this.robot.mousePress(buttonMask);
        }
    }

    @Override
    public void releaseMouseButton(int button) throws RemoteException {
        int buttonMask = getMouseButtonMask(button);
        if (buttonMask != -1) {
            this.robot.mouseRelease(buttonMask);
        }
    }

    @Override
    public void typeKey(int keyCode) throws RemoteException {
        this.robot.keyPress(keyCode);
        this.robot.keyRelease(keyCode);
    }

    @Override
    public void typeText(String text) throws RemoteException {
        for (char c : text.toCharArray()) {
            typeChar(c);
        }
    }

    private void typeChar(char c) {
        try {
            boolean shiftPressed = false;
            if (Character.isUpperCase(c) || c == '!' || c == '@' || c == '#' || c == '$' || c == '%' || c == '^' || c == '&' || c == '*') {
                shiftPressed = true;
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                throw new IllegalArgumentException("Cannot type character " + c);
            }
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            if (shiftPressed) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private int getMouseButtonMask(int button) {
        switch (button) {
            case MouseEvent.BUTTON1: return InputEvent.BUTTON1_DOWN_MASK;
            case MouseEvent.BUTTON2: return InputEvent.BUTTON2_DOWN_MASK;
            case MouseEvent.BUTTON3: return InputEvent.BUTTON3_DOWN_MASK;
            default: return -1;
        }
    }


    @Override
    public void sendFile(String filePath, byte[] fileData) throws RemoteException {
        try {
            File file = new File(filePath); // NullPointerException occurs here if filePath is null
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileData);
            fos.close();
            System.out.println("File received: " + filePath);
        } catch (IOException e) {
            throw new RemoteException("Error sending file: " + e.getMessage());
        }
    }

    @Override
    public byte[] receiveFile(String filePath) throws RemoteException {
        try {
            File file = new File(filePath);
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new RemoteException("Error receiving file: " + e.getMessage());
        }
    }

    @Override
    public String[] listFiles(String directoryPath) throws RemoteException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RemoteException("Invalid directory: " + directoryPath);
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return new String[0];
        }
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
        return fileNames;
    }
}

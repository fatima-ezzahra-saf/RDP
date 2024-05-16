package Server;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
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
            Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
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
    public Point getCursorPosition() throws RemoteException {
        return java.awt.MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void moveCursor(int x, int y) throws RemoteException {
        this.robot.mouseMove(x, y);
    }

    @Override
    public void clickMouse(int button) throws RemoteException {
        switch (button) {
            case MouseEvent.BUTTON1:
                this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                this.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case MouseEvent.BUTTON2:
                this.robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                this.robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                break;
            case MouseEvent.BUTTON3:
                this.robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                this.robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                break;
            default:
                throw new RemoteException("Invalid mouse button: " + button);
        }
    }

    @Override
    public void pressKey(int keyCode) throws RemoteException {
        this.robot.keyPress(keyCode);
        this.robot.keyRelease(keyCode);
    }
}

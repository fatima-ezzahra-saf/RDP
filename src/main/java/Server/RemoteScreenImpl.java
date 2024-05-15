package Server;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RemoteScreenImpl extends UnicastRemoteObject implements RemoteScreen {
    protected RemoteScreenImpl() throws RemoteException {
        super();
    }

    @Override
    public byte[] captureScreen() throws RemoteException {
        try {
            Robot robot = new Robot();
            Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(rectangle);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", outputStream);
            return outputStream.toByteArray();
        } catch (AWTException | IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to capture screen.", e);
        }
    }
}

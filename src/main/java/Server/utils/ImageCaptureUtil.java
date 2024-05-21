package Server.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.imageio.ImageIO;

public class ImageCaptureUtil {
    public static byte[] captureScreen(Robot robot) throws RemoteException {
        try {
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle rectangle = new Rectangle(dimension);
            BufferedImage screenshot = robot.createScreenCapture(rectangle);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to capture screen.", e);
        }
    }
}

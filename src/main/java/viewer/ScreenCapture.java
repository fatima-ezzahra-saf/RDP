package viewer;

        import java.awt.*;
        import java.awt.image.BufferedImage;
        import java.io.ByteArrayOutputStream;
        import javax.imageio.ImageIO;

public class ScreenCapture {
    public static byte[] captureScreen() {

        try {

            Robot robot = new Robot();
            Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(rectangle);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}

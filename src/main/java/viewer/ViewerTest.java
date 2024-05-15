package viewer;

public class ViewerTest {

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 12345;
        int captureInterval = 1000;//capture every how much second

        try {
            while (true) {
                byte[] screenData = ScreenCapture.captureScreen();
                ScreenSender.sendScreen(screenData, serverAddress, serverPort);

                Thread.sleep(captureInterval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
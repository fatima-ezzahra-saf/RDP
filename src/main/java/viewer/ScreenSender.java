package viewer;

import java.io.OutputStream;
import java.net.Socket;

public class ScreenSender {
    public static void sendScreen(byte[] screenData, String serverAddress, int serverPort) {

        try (Socket socket = new Socket(serverAddress, serverPort)) {

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(screenData);
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

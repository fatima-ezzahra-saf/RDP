package Client;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ScreenSender {
    public static void sendScreen(byte[] screenData, Point cursorPos, String serverAddress, int serverPort) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            OutputStream outputStream = socket.getOutputStream();

            // Send cursor position to the server
            sendCursorPosition(outputStream, cursorPos);

            // Send screen data to the server
            outputStream.write(screenData);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendCursorPosition(OutputStream outputStream, Point cursorPos) throws IOException {
        // Send cursor position as individual coordinates
        outputStream.write((int) cursorPos.getX());
        outputStream.write((int) cursorPos.getY());
        outputStream.flush();
    }
}

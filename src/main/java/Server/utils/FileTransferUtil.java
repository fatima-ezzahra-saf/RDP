package Server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;

public class FileTransferUtil {
    public static void sendFile(String filePath, byte[] fileData) throws RemoteException {
        try {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileData);
            fos.close();
            System.out.println("File received: " + filePath);
        } catch (IOException e) {
            throw new RemoteException("Error sending file: " + e.getMessage());
        }
    }

    public static byte[] receiveFile(String filePath) throws RemoteException {
        try {
            File file = new File(filePath);
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new RemoteException("Error receiving file: " + e.getMessage());
        }
    }
}

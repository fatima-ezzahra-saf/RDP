package Client.utils;

import Server.RemoteScreen;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.rmi.RemoteException;

public class FileTransferHandler {
    public static void sendFile(JFrame frame, RemoteScreen remoteScreen) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                byte[] fileData = Files.readAllBytes(file.toPath());
                remoteScreen.sendFile(file.getName(), fileData);
                JOptionPane.showMessageDialog(frame, "File sent successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to send file: " + e.getMessage());
            }
        }
    }

    public static void receiveFile(JFrame frame, RemoteScreen remoteScreen) {
        String filePath = JOptionPane.showInputDialog(frame, "Enter the path to save the received file:", "Receive File", JOptionPane.QUESTION_MESSAGE);
        if (filePath != null && !filePath.isEmpty()) {
            try {
                byte[] fileData = remoteScreen.receiveFile(filePath);
                Files.write(new File(filePath).toPath(), fileData);
                JOptionPane.showMessageDialog(frame, "File received successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to receive file: " + e.getMessage());
            }
        }
    }
}

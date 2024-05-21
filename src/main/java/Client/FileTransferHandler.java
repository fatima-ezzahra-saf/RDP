package Client;

import Server.RemoteScreen;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTransferHandler {
    private RemoteScreen remoteScreen;
    private JFrame frame;

    public FileTransferHandler(RemoteScreen remoteScreen, JFrame frame) {
        this.remoteScreen = remoteScreen;
        this.frame = frame;
    }

    public void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("All Files", "*.*");
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String sourceFilePath = selectedFile.getAbsolutePath();
            String destinationFileName = selectedFile.getName();
            new FileTransferThread(true, sourceFilePath, destinationFileName).start();
        }
    }

    public void receiveFile() {
        String remoteFilePath = JOptionPane.showInputDialog(frame, "Enter the remote file path:");
        if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File localFile = fileChooser.getSelectedFile();
                String localFilePath = localFile.getAbsolutePath();
                new FileTransferThread(false, remoteFilePath, localFilePath).start();
            }
        }
    }

    private class FileTransferThread extends Thread {
        private boolean sendMode;
        private String sourceFilePath;
        private String destinationFilePath;

        public FileTransferThread(boolean sendMode, String sourceFilePath, String destinationFilePath) {
            this.sendMode = sendMode;
            this.sourceFilePath = sourceFilePath;
            this.destinationFilePath = destinationFilePath;
        }

        @Override
        public void run() {
            try {
                if (sendMode) {
                    byte[] fileData = Files.readAllBytes(Paths.get(sourceFilePath));
                    String destinationPath = "./" + destinationFilePath;
                    remoteScreen.sendFile(destinationPath, fileData);
                } else {
                    byte[] fileData = remoteScreen.receiveFile(sourceFilePath);
                    Files.write(Paths.get(destinationFilePath), fileData);
                }
                JOptionPane.showMessageDialog(frame, "File transfer completed successfully.", "File Transfer", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error during file transfer: " + e.getMessage(), "File Transfer Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

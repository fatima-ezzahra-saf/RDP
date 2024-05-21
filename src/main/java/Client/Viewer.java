package Client;

import Server.RemoteScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Viewer extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
    private RemoteScreen remoteScreen;
    private JLabel screenLabel;
    private JProgressBar progressBar;

    private double scaleX;
    private double scaleY;

    private Timer screenUpdateTimer;

    public Viewer() {
        super("Remote Screen Viewer");
        connectToRemoteScreen();
        initUI();
        startScreenUpdateTimer();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension localScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension remoteScreenSize = null;
        try {
            remoteScreenSize = remoteScreen.getScreenSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (remoteScreenSize != null) {
            double widthScale = localScreenSize.getWidth() / remoteScreenSize.getWidth();
            double heightScale = localScreenSize.getHeight() / remoteScreenSize.getHeight();
            double scale = Math.min(widthScale, heightScale);

            int width = (int) (remoteScreenSize.getWidth() * scale);
            int height = (int) (remoteScreenSize.getHeight() * scale);

            scaleX = scale;
            scaleY = scale;

            setSize(width, height);
        } else {
            setSize((int) (localScreenSize.getWidth() * 0.8), (int) (localScreenSize.getHeight() * 0.8));
        }

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        screenLabel = new JLabel();
        add(screenLabel, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        add(progressBar, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem sendFileItem = new JMenuItem("Send File");
        JMenuItem receiveFileItem = new JMenuItem("Receive File");

        sendFileItem.addActionListener(e -> sendFile());
        receiveFileItem.addActionListener(e -> receiveFile());

        fileMenu.add(sendFileItem);
        fileMenu.add(receiveFileItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void connectToRemoteScreen() {
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.137.87", 1099);
            remoteScreen = (RemoteScreen) registry.lookup("RemoteScreen");
        } catch (RemoteException | NotBoundException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to remote screen: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void startScreenUpdateTimer() {
        screenUpdateTimer = new Timer(300, e -> updateScreen());
        screenUpdateTimer.start();
    }

    private void updateScreen() {
        try {
            byte[] screenData = remoteScreen.captureScreen();
            ByteArrayInputStream bais = new ByteArrayInputStream(screenData);
            BufferedImage image = ImageIO.read(bais);

            if (screenLabel.getWidth() > 0 && screenLabel.getHeight() > 0) {
                Image scaledImage = image.getScaledInstance(screenLabel.getWidth(), screenLabel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImage);
                screenLabel.setIcon(icon);
            }
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error capturing remote screen: " + e.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Point mapLocalToRemoteCursor(Point localCursor, Dimension localScreen, Dimension remoteScreen) {
        double relativeX = (double) localCursor.x / localScreen.width;
        double relativeY = (double) localCursor.y / localScreen.height;

        int remoteX = (int) (relativeX * remoteScreen.width);
        int remoteY = (int) (relativeY * remoteScreen.height);

        return new Point(remoteX, remoteY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            remoteScreen.pressKey(e.getKeyCode());
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending key press to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        try {
            remoteScreen.releaseKey(e.getKeyCode());
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending key release to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        try {
            remoteScreen.typeKey(e.getKeyChar());
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending key typed to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        try {
            remoteScreen.pressMouseButton(e.getButton());
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending mouse press to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            remoteScreen.releaseMouseButton(e.getButton());
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending mouse release to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
//            Point clickPoint = e.getPoint();
//            Insets insets = getInsets();
//            int dx = -insets.left;
//            int dy = -insets.top + progressBar.getHeight() - getRootPane().getHeight() + screenLabel.getHeight();
//            clickPoint.translate(dx, dy);
//
//            Dimension localSize = screenLabel.getSize();
//            Dimension remoteSize = remoteScreen.getScreenSize();
//
//            Point remoteClickPoint = mapLocalToRemoteCursor(clickPoint, localSize, remoteSize);
            remoteScreen.clickMouse(e.getButton());
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending mouse click to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            Point movePoint = e.getPoint();
            Insets insets = getInsets();
            int dx = -insets.left;
            int dy = -insets.top + progressBar.getHeight() - getRootPane().getHeight() + screenLabel.getHeight();
            movePoint.translate(dx, dy);

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remoteMovePoint = mapLocalToRemoteCursor(movePoint, localSize, remoteSize);
            remoteScreen.moveCursor(remoteMovePoint.x, remoteMovePoint.y);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error moving cursor on remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        try {
            Point dragPoint = e.getPoint();
            Insets insets = getInsets();
            int dx = -insets.left;
            int dy = -insets.top + progressBar.getHeight() - getRootPane().getHeight() + screenLabel.getHeight();
            dragPoint.translate(dx, dy);

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remoteDragPoint = mapLocalToRemoteCursor(dragPoint, localSize, remoteSize);
            remoteScreen.moveCursor(remoteDragPoint.x, remoteDragPoint.y);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error dragging cursor on remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("All Files", "*.*");
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String sourceFilePath = selectedFile.getAbsolutePath();
            String destinationFileName = selectedFile.getName();
            new FileTransferThread(true, sourceFilePath, destinationFileName).start();
        }
    }

    private void receiveFile() {
        String remoteFilePath = JOptionPane.showInputDialog(this, "Enter the remote file path:");
        if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(this);

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
                    String destinationPath = "path/to/server/directory/" + destinationFilePath; // Specify the relative path on the server
                    remoteScreen.sendFile(destinationPath, fileData);
                } else {
                    byte[] fileData = remoteScreen.receiveFile(sourceFilePath);
                    Files.write(Paths.get(destinationFilePath), fileData);
                }
                JOptionPane.showMessageDialog(Viewer.this, "File transfer completed successfully.", "File Transfer", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Viewer.this, "Error during file transfer: " + e.getMessage(), "File Transfer Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Unused listener methods
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Viewer().setVisible(true));
    }
}
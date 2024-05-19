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

    public Viewer() {
        super("Remote Screen Viewer");
        connectToRemoteScreen();
        initUI();
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

        SwingUtilities.invokeLater(this::updateScreen);
    }

    private void connectToRemoteScreen() {
        try {
            System.out.println("Connecting to remote screen...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            remoteScreen = (RemoteScreen) registry.lookup("RemoteScreen");
            System.out.println("Connected to remote screen");
        } catch (RemoteException | NotBoundException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to remote screen: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending key press to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        try {
            remoteScreen.releaseKey(e.getKeyCode());
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending key release to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        try {
            remoteScreen.typeKey(e.getKeyChar());
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending key typed to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        try {
            Point pressPoint = e.getPoint();
            Insets insets = getInsets();
            pressPoint.translate(-insets.left, -insets.top - getRootPane().getHeight() + screenLabel.getHeight());

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remotePressPoint = mapLocalToRemoteCursor(pressPoint, localSize, remoteSize);
            remoteScreen.pressMouseButton(e.getButton());
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending mouse press to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            Point releasePoint = e.getPoint();
            Insets insets = getInsets();
            releasePoint.translate(-insets.left, -insets.top - getRootPane().getHeight() + screenLabel.getHeight());

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remoteReleasePoint = mapLocalToRemoteCursor(releasePoint, localSize, remoteSize);
            remoteScreen.releaseMouseButton(e.getButton());
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending mouse release to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Point clickPoint = e.getPoint();
            Insets insets = getInsets();
            clickPoint.translate(-insets.left, -insets.top - getRootPane().getHeight() + screenLabel.getHeight());

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remoteClickPoint = mapLocalToRemoteCursor(clickPoint, localSize, remoteSize);
            remoteScreen.clickMouse(remoteClickPoint.x, remoteClickPoint.y);
            updateScreen();
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
            movePoint.translate(-insets.left, -insets.top - getRootPane().getHeight() + screenLabel.getHeight());

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remoteMovePoint = mapLocalToRemoteCursor(movePoint, localSize, remoteSize);
            remoteScreen.moveCursor(remoteMovePoint.x, remoteMovePoint.y);
            updateScreen();
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
            dragPoint.translate(-insets.left, -insets.top - getRootPane().getHeight() + screenLabel.getHeight());

            Dimension localSize = screenLabel.getSize();
            Dimension remoteSize = remoteScreen.getScreenSize();

            Point remoteDragPoint = mapLocalToRemoteCursor(dragPoint, localSize, remoteSize);
            remoteScreen.dragMouse(remoteDragPoint.x, remoteDragPoint.y);
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error dragging cursor on remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            progressBar.setValue(0);
            new Thread(() -> {
                try {
                    System.out.println("Reading file: " + file.getAbsolutePath());
                    byte[] fileData = Files.readAllBytes(file.toPath());
                    String fileName = file.getName();
                    System.out.println("Sending file: " + fileName);
                    remoteScreen.sendFile(fileName, fileData);
                    progressBar.setValue(100);
                    System.out.println("File sent successfully: " + fileName);
                    JOptionPane.showMessageDialog(this, "File sent successfully: " + fileName);
                } catch (IOException e) {
                    System.out.println("Error sending file: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, "Error sending file: " + e.getMessage(), "File Transfer Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }).start();
        }
    }


    private void receiveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();
            new Thread(() -> {
                try {
                    String fileName = JOptionPane.showInputDialog(this, "Enter the name of the file to receive:");
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        byte[] fileData = remoteScreen.receiveFile(fileName);
                        if (fileData != null) {
                            Files.write(Paths.get(directory.getAbsolutePath(), fileName), fileData);
                            JOptionPane.showMessageDialog(this, "File received successfully: " + fileName);
                        } else {
                            JOptionPane.showMessageDialog(this, "File not found on remote server.", "File Transfer Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid file name.", "File Transfer Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error receiving file: " + e.getMessage(), "File Transfer Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }).start();
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Viewer::new);
    }
}

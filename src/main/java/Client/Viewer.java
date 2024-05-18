package Client;

import Server.RemoteScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Viewer extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
    private RemoteScreen remoteScreen;
    private JLabel screenLabel;

    public Viewer() {
        super("Remote Screen Viewer");
        connectToRemoteScreen();
        initUI();
        updateScreen();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = null;
        try {
            screenSize = remoteScreen.getScreenSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        int width = (int) (screenSize.width);
        int height = (int) (screenSize.height);
        setSize(width, height);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        screenLabel = new JLabel();
        add(screenLabel, BorderLayout.CENTER);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void connectToRemoteScreen() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            remoteScreen = (RemoteScreen) registry.lookup("RemoteScreen");
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
            ImageIcon icon = new ImageIcon(image);
            screenLabel.setIcon(icon);
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
    public void mouseClicked(MouseEvent e) {
        try {
            Point clickPoint = e.getPoint();
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

    // Unused listener methods
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Viewer().setVisible(true));
    }
}

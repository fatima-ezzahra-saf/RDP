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

        // Get the screen size of the remote
        Dimension screenSize = null;
        try {
            screenSize = remoteScreen.getScreenSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Set the frame size to 80% of the client's screen size
        int width = (int) (screenSize.width);
        int height = (int) (screenSize.height);
        setSize(width, height);

        // Center the frame on the screen
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
            //Registry registry = LocateRegistry.getRegistry("192.168.137.78", 1099);
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
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error capturing remote screen: " + e.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            int x = clickPoint.x;
            int y = clickPoint.y;

            int remoteWidth = remoteScreen.getScreenSize().width;
            int remoteHeight = remoteScreen.getScreenSize().height;
            int labelWidth = screenLabel.getWidth();
            int labelHeight = screenLabel.getHeight();

            double scaleX = (double) remoteWidth / labelWidth;
            double scaleY = (double) remoteHeight / labelHeight;

            int remoteX = (int) (x * scaleX);
            int remoteY = (int) (y * scaleY);

            remoteX = Math.max(0, Math.min(remoteX, remoteWidth - 1));
            remoteY = Math.max(0, Math.min(remoteY, remoteHeight - 1));

            remoteScreen.clickMouse(remoteX, remoteY); // Pass both remoteX and remoteY
            updateScreen();
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error sending mouse click to remote screen: " + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            remoteScreen.moveCursor(e.getX(), e.getY());
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
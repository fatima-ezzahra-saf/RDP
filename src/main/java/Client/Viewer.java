package Client;

import Server.RemoteScreen;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.rmi.Naming;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Viewer extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
    private RemoteScreen remoteScreen;
    private JLabel screenLabel;

    public Viewer() {
        super("Remote Screen Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        screenLabel = new JLabel();
        add(screenLabel, BorderLayout.CENTER);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        try {
            remoteScreen = (RemoteScreen) Naming.lookup("rmi://localhost/RemoteScreen");
            updateScreen();
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            remoteScreen.pressKey(e.getKeyCode());
            updateScreen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            int button = e.getButton();
            remoteScreen.clickMouse(button);
            updateScreen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            remoteScreen.moveCursor(e.getX(), e.getY());
            updateScreen();
        } catch (Exception ex) {
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
package viewer;

import remote.RemoteScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class EventHandler implements KeyListener, MouseListener, MouseMotionListener {
    private RemoteScreen remoteScreen;
    private JFrame frame;
    private JLabel screenLabel;
    private JProgressBar progressBar;
    private ScreenHandler screenHandler;

    public EventHandler(RemoteScreen remoteScreen, JFrame frame, JLabel screenLabel, JProgressBar progressBar, ScreenHandler screenHandler) {
        this.remoteScreen = remoteScreen;
        this.frame = frame;
        this.screenLabel = screenLabel;
        this.progressBar = progressBar;
        this.screenHandler = screenHandler;
    }

    public void registerListeners() {
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (remoteScreen != null) {
            try {
                remoteScreen.pressKey(e.getKeyCode());
            } catch (RemoteException ex) {
                showError("Error sending key press to remote screen: ", ex);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (remoteScreen != null) {
            try {
                remoteScreen.releaseKey(e.getKeyCode());
            } catch (RemoteException ex) {
                showError("Error sending key release to remote screen: ", ex);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (remoteScreen != null) {
            try {
                remoteScreen.pressMouseButton(e.getButton());
            } catch (RemoteException ex) {
                showError("Error sending mouse press to remote screen: ", ex);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (remoteScreen != null) {
            try {
                remoteScreen.releaseMouseButton(e.getButton());
            } catch (RemoteException ex) {
                showError("Error sending mouse release to remote screen: ", ex);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        if (remoteScreen != null && screenHandler != null) {
            try {
                Point movePoint = e.getPoint();
                Insets insets = frame.getInsets();
                int dx = -insets.left;
                int dy = -insets.top + progressBar.getHeight() - frame.getRootPane().getHeight() + screenLabel.getHeight();
                movePoint.translate(dx, dy);
                Dimension localSize = screenLabel.getSize();
                Dimension remoteSize = remoteScreen.getScreenSize();
                if (localSize != null && remoteSize != null) {
                    Point remoteMovePoint = screenHandler.mapLocalToRemoteCursor(movePoint, localSize, remoteSize);
                    remoteScreen.moveCursor(remoteMovePoint.x, remoteMovePoint.y);
                }
            } catch (RemoteException ex) {
                showError("Error moving cursor on remote screen: ", ex);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (remoteScreen != null && screenHandler != null) {
            try {
                Point dragPoint = e.getPoint();
                Insets insets = frame.getInsets();
                int dx = -insets.left;
                int dy = -insets.top + progressBar.getHeight() - frame.getRootPane().getHeight() + screenLabel.getHeight();
                dragPoint.translate(dx, dy);
                Dimension localSize = screenLabel.getSize();
                Dimension remoteSize = remoteScreen.getScreenSize();
                if (localSize != null && remoteSize != null) {
                    Point remoteDragPoint = screenHandler.mapLocalToRemoteCursor(dragPoint, localSize, remoteSize);
                    remoteScreen.moveCursor(remoteDragPoint.x, remoteDragPoint.y);
                }
            } catch (RemoteException ex) {
                showError("Error dragging cursor on remote screen: ", ex);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(frame, message + ex.getMessage(), "Remote Screen Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

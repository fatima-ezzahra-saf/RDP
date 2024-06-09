package viewer;

import remote.RemoteScreen;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Viewer extends JFrame {
    private RemoteScreen remoteScreen;
    private ScreenHandler screenHandler;
    private FileTransferHandler fileTransferHandler;
    private EventHandler eventHandler;
    private JMenuBar menuBar;
    private JMenu videoMenu;
    private JMenuItem playVideoItem;

    public Viewer() {
        super("Remote Screen Viewer");
        connectToRemoteScreen("localhost"); // DNS server IP
        screenHandler = new ScreenHandler(remoteScreen, this);
        fileTransferHandler = new FileTransferHandler(remoteScreen, this);
        eventHandler = new EventHandler(remoteScreen, this, screenHandler.getScreenLabel(), screenHandler.getProgressBar(), screenHandler);
        initUI();
        screenHandler.startScreenUpdateTimer();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension localScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension remoteScreenSize = screenHandler.getRemoteScreenSize();

        if (remoteScreenSize != null) {
            double widthScale = localScreenSize.getWidth() / remoteScreenSize.getWidth();
            double heightScale = localScreenSize.getHeight() / remoteScreenSize.getHeight();
            double scale = Math.min(widthScale, heightScale);
            int width = (int) (remoteScreenSize.getWidth() * scale);
            int height = (int) (remoteScreenSize.getHeight() * scale);
            setSize(width, height);
        } else {
            setSize((int) (localScreenSize.getWidth() * 0.8), (int) (localScreenSize.getHeight() * 0.8));
        }

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(screenHandler.getScreenLabel(), BorderLayout.CENTER);
        add(screenHandler.getProgressBar(), BorderLayout.SOUTH);

        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem sendFileItem = new JMenuItem("Send File");
        JMenuItem receiveFileItem = new JMenuItem("Receive File");
        videoMenu = new JMenu("Vidéo");
        playVideoItem = new JMenuItem("Lire la vidéo");

        sendFileItem.addActionListener(e -> fileTransferHandler.sendFile());
        receiveFileItem.addActionListener(e -> fileTransferHandler.receiveFile());
        playVideoItem.addActionListener(e -> {
            try {
                remoteScreen.startAudioStream();
                remoteScreen.stopAudioStream();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        videoMenu.add(playVideoItem);
        menuBar.add(videoMenu);
        fileMenu.add(sendFileItem);
        fileMenu.add(receiveFileItem);
        menuBar.add(fileMenu);

        // Ajout de l'option de chat
        JMenu chatMenu = new JMenu("Chat");
        JMenuItem chatMenuItem = new JMenuItem("Open Chat");
        chatMenuItem.addActionListener(e -> {
            ChatWindow chatWindow = new ChatWindow(remoteScreen);
            chatWindow.setVisible(true);
        });
        chatMenu.add(chatMenuItem);
        menuBar.add(chatMenu);

        setJMenuBar(menuBar);

        eventHandler.registerListeners();
    }

    private void connectToRemoteScreen(String host) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            remoteScreen = (RemoteScreen) registry.lookup("RemoteScreen");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to remote screen: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Viewer().setVisible(true));
    }
}

package viewer;

import remote.RemoteScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatWindow extends JFrame {
    private RemoteScreen remoteScreen;
    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Timer messageUpdateTimer;
    private SimpleDateFormat timestampFormat;

    public ChatWindow(RemoteScreen remoteScreen) {
        this.remoteScreen = remoteScreen;
        setTitle("Remote Chat");
        setSize(400, 300);
        setLayout(new BorderLayout());

        timestampFormat = new SimpleDateFormat("HH:mm:ss");

        chatArea = new JTextPane();
        chatArea.setContentType("text/html");
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        messageUpdateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMessages();
            }
        });
        messageUpdateTimer.start();
    }

    private void sendMessage() {
        try {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                String timestamp = timestampFormat.format(new Date());
                String formattedMessage = formatMessage("Viewer", message, timestamp, true);
                remoteScreen.sendMessage(formattedMessage);
                inputField.setText("");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateMessages() {
        try {
            StringBuilder messagesHtml = new StringBuilder("<html><body>");
            for (String message : remoteScreen.getMessages()) {
                messagesHtml.append(message);
            }
            messagesHtml.append("</body></html>");
            chatArea.setText(messagesHtml.toString());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String formatMessage(String sender, String message, String timestamp, boolean isViewer) {
        String align = isViewer ? "right" : "left";
        String color = isViewer ? "blue" : "green";
        return String.format(
                "<div style='text-align: %s;'><b>%s</b> (%s)<br><span style='color: %s;'>%s</span></div>",
                align, sender, timestamp, color, message
        );
    }

}

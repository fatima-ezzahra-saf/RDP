package Client.utils;

import Server.RemoteScreen;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class CursorMapper {
    public static Point mapToRemoteCursor(Point localPoint, JLabel screenLabel, RemoteScreen remoteScreen) {
        Dimension remoteScreenSize = null;
        try {
            remoteScreenSize = remoteScreen.getScreenSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (remoteScreenSize == null) {
            return localPoint;
        }

        double xScale = remoteScreenSize.getWidth() / screenLabel.getWidth();
        double yScale = remoteScreenSize.getHeight() / screenLabel.getHeight();

        int remoteX = (int) (localPoint.getX() * xScale);
        int remoteY = (int) (localPoint.getY() * yScale);

        return new Point(remoteX, remoteY);
    }
}

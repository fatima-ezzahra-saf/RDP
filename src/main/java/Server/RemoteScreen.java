package Server;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteScreen extends Remote {
    byte[] captureScreen() throws RemoteException;
    void moveCursor(int x, int y) throws RemoteException;
    void clickMouse(int x, int y) throws RemoteException;
    void pressKey(int keyCode) throws RemoteException;
    int getScreenWidth() throws RemoteException;
    int getScreenHeight() throws RemoteException;
}

package Server;

import java.awt.Dimension;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteScreen extends Remote {
    Dimension getScreenSize() throws RemoteException;

    byte[] captureScreen() throws RemoteException;

    void pressKey(int keyCode) throws RemoteException;

    void releaseKey(int keyCode) throws RemoteException;

    void typeKey(char keyChar) throws RemoteException;

    void pressMouseButton(int button) throws RemoteException;

    void releaseMouseButton(int button) throws RemoteException;

    void clickMouse(int x, int y) throws RemoteException;

    void moveCursor(int x, int y) throws RemoteException;

    void dragMouse(int x, int y) throws RemoteException;

    void sendFile(String fileName, byte[] data) throws RemoteException;

    byte[] receiveFile(String fileName) throws RemoteException;
}

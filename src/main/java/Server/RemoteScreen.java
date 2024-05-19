package Server;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteScreen extends Remote {
    byte[] captureScreen() throws RemoteException;
    Dimension getScreenSize() throws RemoteException;
    void moveCursor(int x, int y) throws RemoteException;
    void clickMouse(int x, int y) throws RemoteException;
    void pressKey(int keyCode) throws RemoteException;
    void releaseKey(int keyCode) throws RemoteException;
    void pressMouseButton(int button) throws RemoteException;
    void releaseMouseButton(int button) throws RemoteException;
    void typeKey(int keyCode) throws RemoteException;
    void typeText(String text) throws RemoteException;
    void sendFile(String filePath, byte[] fileData) throws RemoteException;
    byte[] receiveFile(String filePath) throws RemoteException;
    String[] listFiles(String directoryPath) throws RemoteException;  // New method for listing files
}

package Sender;

import java.awt.Dimension;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DesktopScreen extends Remote {

    Dimension getScreenSize() throws RemoteException;

    byte[] captureScreen() throws RemoteException;
}

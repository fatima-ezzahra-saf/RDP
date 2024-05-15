package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteScreen extends Remote {
    byte[] captureScreen() throws RemoteException;
}

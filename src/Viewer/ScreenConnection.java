package Viewer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import Sender.DesktopScreen;

public class ScreenConnection {

    public class RemoteScreenConnection {
        public static DesktopScreen connect(String host, int port, String serviceName) throws RemoteException, NotBoundException {
            Registry registry = LocateRegistry.getRegistry(host, port);
            return (DesktopScreen) registry.lookup(serviceName);
        }
    }
}
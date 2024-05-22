package viewer;


import remote.RemoteScreen;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteScreenConnection {
    public static RemoteScreen connect(String host, int port, String serviceName) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (RemoteScreen) registry.lookup(serviceName);
    }
}

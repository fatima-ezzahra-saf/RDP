package remote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteDesk {
    public static void main(String[] args) {
        try {
            RemoteScreenImpl remoteScreen = new RemoteScreenImpl();
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registry.rebind("RemoteScreen", remoteScreen);
            System.out.println("RemoteScreen server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

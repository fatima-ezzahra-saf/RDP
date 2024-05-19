package Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            RemoteScreenImpl remoteScreen = new RemoteScreenImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RemoteScreen", remoteScreen);
            System.out.println("RemoteScreen server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
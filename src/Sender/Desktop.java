package Sender;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Desktop {

        public static void main(String[] args) {
            try {
                DesktopScreenImpl remoteScreen = new DesktopScreenImpl();
                Registry registry = LocateRegistry.getRegistry("localhost", 1099); //  DNS server IP
                registry.rebind("RemoteScreen", remoteScreen);
                System.out.println("RemoteScreen server is running...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

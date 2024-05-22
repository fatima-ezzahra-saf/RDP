package DNSServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DnsServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("DNS Server is running...");
            while (true) {
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

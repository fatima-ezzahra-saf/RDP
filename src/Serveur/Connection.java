package Serveur;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {

        ServerSocket socket=null;
        DataInputStream password=null;
        DataOutputStream verify=null;
        String width="";
        String height="";

        Connection(int port,String value1){
            Robot robot=null;
            Rectangle rectangle=null;

            try {
                System.out.println("on attendant que le client soit connecter");
                socket=new ServerSocket(port);
                GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();//permet de savoir des infos sur le screen
                GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //recupere la taille de l'ecran en pixels
                String width = ""+dim.getWidth();
                String height = ""+dim.getHeight();
                rectangle=new Rectangle(dim); // creation d'un rectangle a partir des dims recuperer precedement
                robot=new Robot(gDev);//permet de capturer l'ecran donc on va utiliser le gDev aqui contient l objet qu on veux capturer

                drawGUI();

                while(true) {
                    // on va atendu jusqu'a ce que une connexion sera entree puis on va verifier avec password.readUTF() la valeur entrer a travers la socket du client
                    Socket sc=socket.accept();
                    password=new DataInputStream(sc.getInputStream());
                    verify=new DataOutputStream(sc.getOutputStream());

                    String pass=password.readUTF();
                    if(pass.equals(value1)) {
                        verify.writeUTF("valid");
                        verify.writeUTF(width);
                        verify.writeUTF(height);
                        //apres que la conexion est etablie on peut envoyer le screens et recevoir des evenements
                        new SendScreen(sc,robot,rectangle);
                    }else {
                        verify.writeUTF("Invalid");
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        private void drawGUI() {

        }
}
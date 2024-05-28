package Serveur;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;

public class SendScreen extends Thread{
    Socket socket=null;//les screens seront envoyer a l'aide de cet socket
    Robot robot=null;//pour controler souris et clavier
    Rectangle rectangle=null;
    boolean continueLoop=true;
    OutputStream oos=null;//flux de sorite pour envoyer les screens

    public SendScreen(Socket socket,Robot robot, Rectangle rect) {
        this.socket=socket;
        this.robot=robot;
        rectangle=rect;
        start();
    }

    public void run() {
        try {
            oos=socket.getOutputStream();
        }catch(IOException e) {
            e.printStackTrace();
        }
        //cette boucle permet d'envoyer de maniere continue les screens
        while(continueLoop) {
            BufferedImage image=robot.createScreenCapture(rectangle);
            try {
                ImageIO.write(image,"jpeg",oos);
            }catch(IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


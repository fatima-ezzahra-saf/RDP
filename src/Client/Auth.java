package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Auth extends JFrame implements ActionListener {

        private Socket cSocket=null;
        DataOutputStream passchk=null;
        DataInputStream verification=null;
        String verify="";
        JButton submit;
        JPanel panel;
        JLabel label,label1;
        String width="",height="";
        JTextField text1;

        Auth(Socket cSocket){
            label1=new JLabel();
            label1.setText("Code");
            text1=new JTextField(15);
            this.cSocket=cSocket;
            label=new JLabel();
            label.setText("");
            this.setLayout(new BorderLayout());
            submit=new JButton("Valider");
            panel=new JPanel(new GridLayout(2,1));
            panel.add(label1);
            panel.add(text1);
            panel.add(label);
            panel.add(submit);
            panel.setBackground(new Color(68, 147, 213));
            add(panel,BorderLayout.CENTER);
            submit.addActionListener(this);
            setTitle("connexion...");
        }

        public void actionPerformed(ActionEvent ae) {
            String value1=text1.getText();
            try {
                passchk=new DataOutputStream(cSocket.getOutputStream());
                verification=new DataInputStream(cSocket.getInputStream());
                passchk.writeUTF(value1);//cette valeur sera envoyer vers le serveur pour qu'il verifie si elle correspond bien a celle generer
                verify=verification.readUTF();//dont on va recuperer la valeur soit le code correspond ou pas
            }catch(IOException e) {
                e.printStackTrace();
            }
            if(verify.equals("valid")) {
                try {
                    width=verification.readUTF();
                    height=verification.readUTF();
                }catch(IOException e) {
                    e.printStackTrace();
                }
                Fenetre abc = new Fenetre(cSocket,width,height);
                dispose();//pour liberer les ressources employees par une fenetre lorsqu'il est fermee
            }
            else {
                System.out.print("entrz un code valide");
                JOptionPane.showMessageDialog(this, "le code est incorrect","Error",JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        }
}

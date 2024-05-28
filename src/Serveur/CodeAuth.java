package Serveur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CodeAuth extends JFrame implements ActionListener{

        static String port = "4907";
        JButton submit;
        JPanel panel;
        JTextField text1;
        String value1;

        CodeAuth() {
            JLabel label1 = new JLabel("Votre code");
            text1 = new JTextField(15);
            // le champ du code est non edutable car il sera generer
            text1.setEditable(false);

            JButton generateButton = new JButton("Générer votre Code");
            generateButton.addActionListener(e -> generateRandomCode());

            submit = new JButton("Valider");
            submit.addActionListener(this);

            panel = new JPanel(new GridLayout(3, 3)); // Augmente le nombre de lignes pour ajouter le bouton "Générer Code"
            panel.add(label1);
            panel.add(text1);
            panel.add(generateButton);
            panel.add(submit);
            panel.setBackground(new Color(68, 147, 213));

            add(panel, BorderLayout.CENTER);

            setTitle("Code pour le client");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 200);
            setLocationRelativeTo(null);
        }

        //le code qui va etre entrer il va creer avec le port une connection
        public void actionPerformed(ActionEvent e) {
            value1 = text1.getText();
            dispose();
            new Connection(Integer.parseInt(port), value1);
        }

        private void generateRandomCode() {
            Random random = new Random();
            // generer un nombre aleatoire de 6 chiffres
            int randomCode = 100000 + random.nextInt(900000);
            // met le nombre aleatoire dans le champ
            text1.setText(String.valueOf(randomCode));
        }
}

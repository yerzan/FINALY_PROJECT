package Login;

import dao.LoginDAO;
import SekretariatPanel.SekretariatPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JPanel jpanel2;
    private JPanel jpanel3;
    private JLabel JWelc;
    private JPanel jpanel4;
    private JLabel login1;
    private JLabel pass1;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel mainPanel;
    private JLabel icons1;
    private JPanel jpanel5;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String haslo = new String(passwordField.getPassword());

                LoginDAO loginDAO = new LoginDAO();
                boolean ok = loginDAO.sprawdzLogin(login, haslo);

                if (ok) {
                    ImageIcon icon = new ImageIcon("src/resourse/images/icons_good.png");
                    JOptionPane.showMessageDialog(null, "Zalogowano pomyślnie!", "Sukces", JOptionPane.INFORMATION_MESSAGE, icon);



                    JFrame sekretariatFrame = new JFrame("Panel Sekretariatu");
                    sekretariatFrame.setContentPane(new SekretariatPanel().getPanel());
                    sekretariatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    sekretariatFrame.pack();
                    sekretariatFrame.setLocationRelativeTo(null);
                    sekretariatFrame.setVisible(true);


                    SwingUtilities.getWindowAncestor(loginButton).dispose();
                } else {
                    ImageIcon icon = new ImageIcon("src/resourse/images/icons_warning.png");
                    JOptionPane.showMessageDialog(null, "Błędny login lub hasło!", "Błąd logowania", JOptionPane.ERROR_MESSAGE, icon);
                }
            }
        });
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}

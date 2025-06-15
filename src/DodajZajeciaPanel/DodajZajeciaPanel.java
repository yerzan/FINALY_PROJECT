package DodajZajeciaPanel;

import dao.ZajeciaDAO;
import model.Projekt;
import model.Laboratorium;
import model.Wyklad;
import model.Zajecia;
import SekretariatPanel.SekretariatPanel;

import javax.swing.*;

public class DodajZajeciaPanel {
    private JPanel panel1;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JComboBox comboBox4;
    private JComboBox comboBoxGrupa1;
    private JComboBox comboBoxGrupa2;
    private JButton zapiszButton;
    private JPanel jpanel20;
    private JPanel jpanel21;
    private JPanel jpanel22;
    private JLabel jtyp;
    private JLabel jkierunek;
    private JLabel jprzedmiot;
    private JLabel jprowadzocy;
    private JLabel jsala;
    private JLabel jgodzina;
    private JLabel jgrupa1;
    private JLabel jgrupa2;
    private JLabel jday;
    private JLabel jaddza;
    private JPanel jpanel23;


    private final ImageIcon warningIcon = new ImageIcon("src/resourse/images/icons_warning.png");
    private final ImageIcon successIcon = new ImageIcon("src/resourse/images/icons_good.png");

    public JPanel getPanel() {
        return panel1;
    }

    public DodajZajeciaPanel(JFrame parentFrame, JTable tableToRefresh, ZajeciaDAO dao, SekretariatPanel panelSekretariat) {
        comboBox1.removeAllItems();
        comboBox1.addItem("Wyklad");
        comboBox1.addItem("Laboratorium");
        comboBox1.addItem("Projekt");

        comboBox3.removeAllItems();
        comboBox3.addItem("Poniedziałek");
        comboBox3.addItem("Wtorek");
        comboBox3.addItem("Środa");
        comboBox3.addItem("Czwartek");
        comboBox3.addItem("Piątek");

        comboBox4.removeAllItems();
        for (int i = 9; i <= 15; i++) {
            comboBox4.addItem(i);
        }

        comboBoxGrupa1.setModel(new DefaultComboBoxModel<>(new String[]{"A", "B"}));
        comboBoxGrupa2.setModel(new DefaultComboBoxModel<>(new String[]{"A", "B"}));

        comboBox1.addActionListener(e -> {
            comboBox2.removeAllItems();
            String typ = (String) comboBox1.getSelectedItem();
            if (typ.equals("Wyklad")) {
                comboBox2.addItem("B2-126");
                comboBox2.addItem("B2-127");
                comboBox2.addItem("B2-128");
                comboBox2.addItem("B2-132");
                comboBox2.addItem("B2-135");
                comboBoxGrupa1.setSelectedItem("A");
                comboBoxGrupa2.setSelectedItem("B");
                comboBoxGrupa1.setEnabled(false);
                comboBoxGrupa2.setEnabled(false);
            } else if (typ.equals("Projekt")) {
                comboBox2.addItem("B1-201");
                comboBox2.addItem("B1-202");
                comboBox2.addItem("B1-203");
                comboBox2.addItem("B1-204");
                comboBox2.addItem("B1-205");
                comboBox2.addItem("B1-206");
                comboBox2.addItem("B1-207");
                comboBox2.addItem("B1-208");
                comboBoxGrupa1.setEnabled(true);
                comboBoxGrupa2.setEnabled(true);
            } else {
                comboBox2.addItem("B3-101");
                comboBox2.addItem("B3-102");
                comboBox2.addItem("B3-103");
                comboBox2.addItem("B3-104");
                comboBox2.addItem("B3-105");
                comboBox2.addItem("B3-106");
                comboBox2.addItem("B3-107");
                comboBoxGrupa1.setEnabled(true);
                comboBoxGrupa2.setEnabled(false);
                comboBoxGrupa2.setSelectedItem("");
            }
        });

        comboBox1.setSelectedIndex(0);

        zapiszButton.addActionListener(e -> {
            try {
                String typ = (String) comboBox1.getSelectedItem();
                String kierunek = textField1.getText().trim();
                String przedmiot = textField2.getText().trim();
                String prowadzacy = textField3.getText().trim();
                String sala = (String) comboBox2.getSelectedItem();
                String dzien = (String) comboBox3.getSelectedItem();
                int godzina = Integer.parseInt(comboBox4.getSelectedItem().toString());

                if (kierunek.isEmpty() || przedmiot.isEmpty() || prowadzacy.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Wszystkie pola tekstowe muszą być wypełnione!", "Błąd", JOptionPane.WARNING_MESSAGE, warningIcon);
                    return;
                }

                if (dao.czySalaZajeta(sala, dzien, godzina, null)) {
                    JOptionPane.showMessageDialog(null, "Sala jest już zajęta w tym dniu i godzinie!", "Konflikt", JOptionPane.WARNING_MESSAGE, warningIcon);
                    return;
                }

                Zajecia zajecie;

                if (typ.equals("Laboratorium")) {
                    String grupa = (String) comboBoxGrupa1.getSelectedItem();

                    if (dao.czyGrupaMaZajecia(grupa, dzien, godzina, null)) {
                        JOptionPane.showMessageDialog(null, "Grupa ma już zajęcia w tym dniu i godzinie!", "Konflikt", JOptionPane.WARNING_MESSAGE, warningIcon);
                        return;
                    }

                    zajecie = new Laboratorium(kierunek, przedmiot, prowadzacy, sala, dzien, godzina, grupa);

                } else if (typ.equals("Projekt")) {
                    String g1 = (String) comboBoxGrupa1.getSelectedItem();
                    String g2 = (String) comboBoxGrupa2.getSelectedItem();

                    if (g1.equals(g2)) {
                        JOptionPane.showMessageDialog(null, "Grupa 1 i Grupa 2 nie mogą być takie same!", "Błąd", JOptionPane.WARNING_MESSAGE, warningIcon);
                        return;
                    }

                    if (dao.czyGrupaMaZajecia(g1, dzien, godzina, null) || dao.czyGrupaMaZajecia(g2, dzien, godzina, null)) {
                        JOptionPane.showMessageDialog(null, "Jedna z grup ma już zajęcia w tym dniu i godzinie!", "Konflikt", JOptionPane.WARNING_MESSAGE, warningIcon);
                        return;
                    }

                    zajecie = new Projekt(kierunek, przedmiot, prowadzacy, sala, dzien, godzina, g1, g2);
                } else {
                    if (dao.czyGrupaMaZajecia("A", dzien, godzina, null) || dao.czyGrupaMaZajecia("B", dzien, godzina, null)) {
                        JOptionPane.showMessageDialog(null, "Grupa A lub B ma już zajęcia w tym czasie!", "Konflikt", JOptionPane.WARNING_MESSAGE, warningIcon);
                        return;
                    }

                    zajecie = new Wyklad(kierunek, przedmiot, prowadzacy, sala, dzien, godzina);
                }

                dao.dodajZajecia(zajecie);
                JOptionPane.showMessageDialog(null, "Zajęcia dodane!", "Sukces", JOptionPane.INFORMATION_MESSAGE, successIcon);
                SwingUtilities.getWindowAncestor(zapiszButton).dispose();
                panelSekretariat.odswiezTabele();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE, warningIcon);
                ex.printStackTrace();
            }
        });
    }
}

package EdytujZajeciaPanel;

import dao.ZajeciaDAO;
import model.*;

import javax.swing.*;

public class EdytujZajeciaPanel {
    private JPanel jpanel20;
    private JPanel jpanel23;
    private JLabel jaddza;
    private JPanel jpanel21;
    private JLabel jtyp;
    private JLabel jkierunek;
    private JLabel jprzedmiot;
    private JLabel jprowadzocy;
    private JLabel jsala;
    private JLabel jgodzina;
    private JLabel jgrupa1;
    private JLabel jgrupa2;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JComboBox comboBox2;
    private JLabel jday;
    private JComboBox comboBox3;
    private JComboBox comboBox4;
    private JComboBox comboBoxGrupa1;
    private JComboBox comboBoxGrupa2;
    private JPanel jpanel22;
    private JButton zapiszButton;
    private JPanel panel1;

    private ZajeciaDAO dao;
    private int zajeciaId;

    public JPanel getPanel() {
        return panel1;
    }

    public EdytujZajeciaPanel(JFrame parentFrame, Zajecia zajecie, ZajeciaDAO dao) {
        this.dao = dao;
        this.zajeciaId = zajecie.getId();

        ImageIcon iconWarning = new ImageIcon("src/resourse/images/icons_warning.png");
        ImageIcon iconSuccess = new ImageIcon("src/resourse/images/icons_good.png");

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

        comboBox1.setSelectedItem(zajecie.getClass().getSimpleName());
        textField1.setText(zajecie.getKierunek());
        textField2.setText(zajecie.getPrzedmiot());
        textField3.setText(zajecie.getProwadzacy());
        comboBox3.setSelectedItem(zajecie.getDzienTygodnia());
        comboBox4.setSelectedItem(zajecie.getGodzina());
        comboBox1.getActionListeners()[0].actionPerformed(null);
        comboBox2.setSelectedItem(zajecie.getSala());

        if (zajecie instanceof Laboratorium lab) {
            comboBoxGrupa1.setSelectedItem(lab.getNrGrupy());
            comboBoxGrupa2.setSelectedItem("");
        } else if (zajecie instanceof Projekt pr) {
            comboBoxGrupa1.setSelectedItem(pr.getGrupa1());
            comboBoxGrupa2.setSelectedItem(pr.getGrupa2());
        } else {
            comboBoxGrupa1.setSelectedItem("A");
            comboBoxGrupa2.setSelectedItem("B");
        }

        zapiszButton.addActionListener(e -> {
            try {
                String typ = (String) comboBox1.getSelectedItem();
                String kierunek = textField1.getText().trim();
                String przedmiot = textField2.getText().trim();
                String prowadzacy = textField3.getText().trim();
                String sala = (String) comboBox2.getSelectedItem();
                String dzien = (String) comboBox3.getSelectedItem();
                int godzina = (int) comboBox4.getSelectedItem();

                if (kierunek.isEmpty() || przedmiot.isEmpty() || prowadzacy.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Wszystkie pola tekstowe muszą być wypełnione!",
                            "Błąd", JOptionPane.ERROR_MESSAGE, iconWarning);
                    return;
                }

                if (dao.czySalaZajeta(sala, dzien, godzina, zajeciaId)) {
                    JOptionPane.showMessageDialog(null, "Sala jest już zajęta w tym dniu i godzinie!",
                            "Konflikt", JOptionPane.ERROR_MESSAGE, iconWarning);
                    return;
                }

                Zajecia nowe;

                if (typ.equals("Laboratorium")) {
                    String grupa = (String) comboBoxGrupa1.getSelectedItem();

                    if (dao.czyGrupaMaZajecia(grupa, dzien, godzina, zajeciaId)) {
                        JOptionPane.showMessageDialog(null, "Wybrana grupa ma już zajęcia w tym dniu i godzinie!",
                                "Konflikt", JOptionPane.ERROR_MESSAGE, iconWarning);
                        return;
                    }

                    nowe = new Laboratorium(zajeciaId, kierunek, przedmiot, prowadzacy, sala, dzien, godzina, grupa);

                } else if (typ.equals("Projekt")) {
                    String g1 = (String) comboBoxGrupa1.getSelectedItem();
                    String g2 = (String) comboBoxGrupa2.getSelectedItem();

                    if (g1.equals(g2)) {
                        JOptionPane.showMessageDialog(null, "Grupa 1 i Grupa 2 nie mogą być takie same!",
                                "Błąd danych", JOptionPane.ERROR_MESSAGE, iconWarning);
                        return;
                    }

                    if (dao.czyGrupaMaZajecia(g1, dzien, godzina, zajeciaId) ||
                            dao.czyGrupaMaZajecia(g2, dzien, godzina, zajeciaId)) {
                        JOptionPane.showMessageDialog(null, "Jedna z grup ma już zajęcia w tym dniu i godzinie!",
                                "Konflikt", JOptionPane.ERROR_MESSAGE, iconWarning);
                        return;
                    }

                    nowe = new Projekt(zajeciaId, kierunek, przedmiot, prowadzacy, sala, dzien, godzina, g1, g2);

                } else {
                    if (dao.czyGrupaMaZajecia("A", dzien, godzina, zajeciaId) ||
                            dao.czyGrupaMaZajecia("B", dzien, godzina, zajeciaId)) {
                        JOptionPane.showMessageDialog(null, "Grupa A lub B ma już zajęcia w tym czasie!",
                                "Konflikt", JOptionPane.ERROR_MESSAGE, iconWarning);
                        return;
                    }

                    nowe = new Wyklad(zajeciaId, kierunek, przedmiot, prowadzacy, sala, dzien, godzina);
                }

                dao.updateZajecia(nowe);
                JOptionPane.showMessageDialog(null, "Zmieniono!",
                        "Sukces", JOptionPane.INFORMATION_MESSAGE, iconSuccess);
                SwingUtilities.getWindowAncestor(panel1).dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Błąd: " + ex.getMessage(),
                        "Wyjątek", JOptionPane.ERROR_MESSAGE, iconWarning);
                ex.printStackTrace();
            }
        });
    }
}


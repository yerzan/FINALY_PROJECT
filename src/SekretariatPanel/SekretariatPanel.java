package SekretariatPanel;

import DodajZajeciaPanel.DodajZajeciaPanel;
import dao.ZajeciaDAO;
import model.Projekt;
import model.Laboratorium;
import model.Zajecia;
import EdytujZajeciaPanel.EdytujZajeciaPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class SekretariatPanel {
    private JPanel panel1;
    private JPanel jpanel10, jpanel11, jpanel12, jpanel14, jpanel15, jpanel13;
    private JScrollPane scrolp;
    private JTable table1;
    private JButton dodajButton, usunButton, edytujButton, refreshButton;
    private JComboBox comboBox1, comboBox2, comboBox3;
    private JButton zastosujFiltry;
    private JLabel PanelSek, sala1, grupa1, typzajec;

    private DefaultTableModel model;
    private ZajeciaDAO dao;

    public JPanel getPanel() {
        return panel1;
    }

    public SekretariatPanel() {
        dao = new ZajeciaDAO();
        initTable();
        initComboBoxes();
        loadZajecia(dao.getAllZajecia());

        edytujButton.addActionListener(e -> {
            int selected = table1.getSelectedRow();
            if (selected != -1) {
                int id = (int) model.getValueAt(selected, 0);
                Zajecia zajecie = dao.getAllZajecia().stream()
                        .filter(z -> z.getId() == id)
                        .findFirst()
                        .orElse(null);

                if (zajecie != null) {
                    JFrame frame = new JFrame("Edytuj zajęcia");
                    EdytujZajeciaPanel panel = new EdytujZajeciaPanel(frame, zajecie, dao);
                    frame.setContentPane(panel.getPanel());
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Nie znaleziono zajęcia.");
                }
            } else {
                ImageIcon icon = new ImageIcon("src/resourse/images/icon_not.png");
                JOptionPane.showMessageDialog(null, "Wybierz zajęcie do edycji.", "Błąd", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });

        if (refreshButton != null) {
            refreshButton.addActionListener(e -> {
                loadZajecia(dao.getAllZajecia());
                initComboBoxes();
            });
        }

        zastosujFiltry.addActionListener(e -> {
            String sala = (String) comboBox1.getSelectedItem();
            String grupaStr = (String) comboBox2.getSelectedItem();
            String typ = (String) comboBox3.getSelectedItem();

            List<Zajecia> wynik = dao.getAllZajecia();

            if (sala != null && !sala.equals("Wszystkie")) {
                wynik = wynik.stream()
                        .filter(z -> z.getSala().equals(sala))
                        .collect(Collectors.toList());
            }

            if (grupaStr != null && !grupaStr.equals("Wszystkie")) {
                wynik = wynik.stream()
                        .filter(z -> z.czyDlaGrupy(grupaStr))
                        .collect(Collectors.toList());
            }

            if (typ != null && !typ.equals("Wszystkie")) {
                wynik = wynik.stream()
                        .filter(z -> {
                            String typName = z instanceof Projekt ? "Projekt" : z.getClass().getSimpleName();
                            return typName.equals(typ);
                        })
                        .collect(Collectors.toList());
            }

            loadZajecia(wynik);
        });


        dodajButton.addActionListener(e -> {
            JFrame frame = new JFrame("Dodaj zajęcia");
            DodajZajeciaPanel panel = new DodajZajeciaPanel(frame, table1, dao, this);
            frame.setContentPane(panel.getPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        usunButton.addActionListener(e -> {
            int selected = table1.getSelectedRow();
            if (selected != -1) {
                int id = (int) model.getValueAt(selected, 0);
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Czy na pewno chcesz usunąć zajęcia o ID " + id + "?",
                        "Potwierdzenie", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dao.usunZajecia(id);
                    odswiezTabele();
                }
            } else {
                ImageIcon icon = new ImageIcon("src/resourse/images/icon_not.png");
                JOptionPane.showMessageDialog(null, "Wybierz zajęcia do usunięcia.", "Błąd", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
    }

    private void initTable() {
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "ID", "Dzień", "Godzina", "Typ", "Kierunek", "Przedmiot", "Prowadzący", "Sala", "Grupa"
        });
        table1.setModel(model);
    }

    private void initComboBoxes() {
        List<Zajecia> lista = dao.getAllZajecia();

        comboBox1.removeAllItems();
        comboBox1.addItem("Wszystkie");
        lista.stream()
                .map(Zajecia::getSala)
                .distinct()
                .forEach(comboBox1::addItem);

        comboBox2.removeAllItems();
        comboBox2.addItem("Wszystkie");
        comboBox2.addItem("A");
        comboBox2.addItem("B");

        comboBox3.removeAllItems();
        comboBox3.addItem("Wszystkie");
        comboBox3.addItem("Wyklad");
        comboBox3.addItem("Projekt");
        comboBox3.addItem("Laboratorium");
    }

    private void loadZajecia(List<Zajecia> lista) {
        model.setRowCount(0);
        for (Zajecia z : lista) {
            String grupaStr;
            if (z instanceof Projekt pr) {
                String g1 = pr.getGrupa1();
                String g2 = pr.getGrupa2();
                grupaStr = g1 + " i " + g2;
            } else if (z instanceof Laboratorium lab) {
                grupaStr = lab.getNrGrupy();
            } else {
                grupaStr = "A i B";
            }

            String typStr = z instanceof Projekt ? "Projekt" : z.getClass().getSimpleName();

            model.addRow(new Object[]{
                    z.getId(),
                    z.getDzienTygodnia(),
                    z.getGodzina(),
                    typStr,
                    z.getKierunek(),
                    z.getPrzedmiot(),
                    z.getProwadzacy(),
                    z.getSala(),
                    grupaStr
            });
        }
    }


    public void odswiezTabele() {
        loadZajecia(dao.getAllZajecia());
    }
}

package model;

public class Laboratorium extends Zajecia {
    private String nrGrupy;

    public Laboratorium(String kierunek, String przedmiot, String prowadzacy,
                        String sala, String dzienTygodnia, int godzina, String nrGrupy) {
        super(kierunek, przedmiot, prowadzacy, sala, dzienTygodnia, godzina);
        this.nrGrupy = nrGrupy;
    }

    public Laboratorium(int id, String kierunek, String przedmiot, String prowadzacy,
                        String sala, String dzienTygodnia, int godzina, String nrGrupy) {
        super(kierunek, przedmiot, prowadzacy, sala, dzienTygodnia, godzina);
        this.id = id;
        this.nrGrupy = nrGrupy;
    }

    @Override
    public boolean czyDlaGrupy(String nr) {
        return nrGrupy.equals(nr);
    }

    public String getNrGrupy() {
        return nrGrupy;
    }
}

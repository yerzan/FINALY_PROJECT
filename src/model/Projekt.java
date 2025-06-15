package model;

public class Projekt extends Zajecia {
    private String grupa1;
    private String grupa2;

    public Projekt(String kierunek, String przedmiot, String prowadzacy,
                   String sala, String dzienTygodnia, int godzina,
                   String grupa1, String grupa2) {
        super(kierunek, przedmiot, prowadzacy, sala, dzienTygodnia, godzina);
        this.grupa1 = grupa1;
        this.grupa2 = grupa2;
    }

    public Projekt(int id, String kierunek, String przedmiot, String prowadzacy,
                   String sala, String dzienTygodnia, int godzina,
                   String grupa1, String grupa2) {
        super(kierunek, przedmiot, prowadzacy, sala, dzienTygodnia, godzina);
        this.id = id;
        this.grupa1 = grupa1;
        this.grupa2 = grupa2;
    }

    @Override
    public boolean czyDlaGrupy(String grupa) {
        return grupa.equalsIgnoreCase(grupa1) || grupa.equalsIgnoreCase(grupa2);
    }

    public String getGrupa1() {
        return grupa1;
    }

    public String getGrupa2() {
        return grupa2;
    }
}

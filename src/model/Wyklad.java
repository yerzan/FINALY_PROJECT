package model;

public class Wyklad extends Zajecia {

    public Wyklad(String kierunek, String przedmiot, String prowadzacy,
                  String sala, String dzienTygodnia, int godzina) {
        super(kierunek, przedmiot, prowadzacy, sala, dzienTygodnia, godzina);
    }

    public Wyklad(int id, String kierunek, String przedmiot, String prowadzacy,
                  String sala, String dzienTygodnia, int godzina) {
        super(kierunek, przedmiot, prowadzacy, sala, dzienTygodnia, godzina);
        this.id = id;
    }

    @Override
    public boolean czyDlaGrupy(String nr) {
        return true;
    }
}

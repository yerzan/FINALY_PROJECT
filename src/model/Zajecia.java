package model;

public abstract class Zajecia {
    protected int id;
    protected String kierunek;
    protected String przedmiot;
    protected String prowadzacy;
    protected String sala;
    protected String dzienTygodnia;
    protected int godzina;

    public Zajecia(String kierunek, String przedmiot, String prowadzacy,
                   String sala, String dzienTygodnia, int godzina) {
        this.kierunek = kierunek;
        this.przedmiot = przedmiot;
        this.prowadzacy = prowadzacy;
        this.sala = sala;
        this.dzienTygodnia = dzienTygodnia;
        this.godzina = godzina;
    }

    public int getId() {
        return id;
    }

    public String getKierunek() {
        return kierunek;
    }

    public String getPrzedmiot() {
        return przedmiot;
    }

    public String getProwadzacy() {
        return prowadzacy;
    }

    public String getSala() {
        return sala;
    }

    public String getDzienTygodnia() {
        return dzienTygodnia;
    }

    public int getGodzina() {
        return godzina;
    }

    public abstract boolean czyDlaGrupy(String grupa);

}

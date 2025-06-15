package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlanZajec {
    private List<Zajecia> zajeciaList = new ArrayList<>();

    public void dodajZajecia(Zajecia z) {
        zajeciaList.add(z);
    }

    public void usunZajecia(Zajecia z) {
        zajeciaList.remove(z);
    }

    public List<Zajecia> filtrujPoDniu(String dzien) {
        return zajeciaList.stream()
                .filter(z -> z.getDzienTygodnia().equalsIgnoreCase(dzien))
                .collect(Collectors.toList());
    }

    public List<Zajecia> filtrujPoGrupie(String grupa) {
        return zajeciaList.stream()
                .filter(z -> z.czyDlaGrupy(grupa))
                .collect(Collectors.toList());
    }

    public List<Zajecia> filtrujPoSali(String sala) {
        return zajeciaList.stream()
                .filter(z -> z.getSala().equalsIgnoreCase(sala))
                .collect(Collectors.toList());
    }

    public boolean czySalaZajeta(String sala, String dzien, int godzina) {
        return zajeciaList.stream().anyMatch(z ->
                z.getSala().equalsIgnoreCase(sala) &&
                        z.getDzienTygodnia().equalsIgnoreCase(dzien) &&
                        z.getGodzina() == godzina);
    }

    public boolean czyGrupaZajeta(String grupa, String dzien, int godzina) {
        return zajeciaList.stream().anyMatch(z ->
                z.getDzienTygodnia().equalsIgnoreCase(dzien) &&
                        z.getGodzina() == godzina &&
                        z.czyDlaGrupy(grupa));
    }

    public List<Zajecia> getWszystkieZajecia() {
        return zajeciaList;
    }
}

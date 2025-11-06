package com.earth;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class World {
    private List<Aeroport> list = new ArrayList<>();

    public World(String fileName) {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(fileName));
            String s = buf.readLine(); // lit la première ligne
            while (s != null) {
                s = s.replaceAll("\"", ""); // supprime les guillemets
                String[] fields = s.split(",");

                // On ne garde que les grands aéroports
                if (fields.length > 11 && fields[1].equals("large_airport")) {
                    try {
                        String name = fields[2];
                        String codeIATA = fields[9];
                        if (codeIATA == null || codeIATA.isEmpty()) {
                            s = buf.readLine();
                            continue;
                        }

                        // les coordonnées GPS sont souvent dans le champ 11 ou 12
                        String coordString = fields[11];
                        if (coordString.contains(" ")) coordString = coordString.replace(" ", "");
                        String[] coords = coordString.split(",");
                        if (coords.length < 2) {
                            s = buf.readLine();
                            continue;
                        }

                        double longitude = Double.parseDouble(coords[0]);
                        double latitude = Double.parseDouble(coords[1]);

                        Aeroport a = new Aeroport(name, codeIATA, latitude, longitude);
                        list.add(a);
                    } catch (Exception ignored) {}
                }
                s = buf.readLine();
            }
            buf.close();
            System.out.println("Chargement terminé : " + list.size() + " aéroports trouvés.");
        } catch (Exception e) {
            System.out.println("Erreur de lecture du fichier CSV !");
            e.printStackTrace();
        }
    }

    public List<Aeroport> getList() {
        return list;
    }

    public Aeroport findByCode(String code) {
        for (Aeroport a : list) {
            if (a.getCodeIATA().equalsIgnoreCase(code)) {
                return a;
            }
        }
        return null;
    }

    public Aeroport findNearestAirport(double longitude, double latitude) {
        if (list.isEmpty()) return null;

        Aeroport ref = new Aeroport("ref", "XXX", latitude, longitude);
        Aeroport nearest = list.get(0);
        double minDist = ref.calculDistance(nearest);

        for (Aeroport a : list) {
            double dist = ref.calculDistance(a);
            if (dist < minDist) {
                minDist = dist;
                nearest = a;
            }
        }
        return nearest;
    }
}

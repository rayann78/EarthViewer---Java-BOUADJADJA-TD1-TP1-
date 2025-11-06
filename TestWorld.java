package com.earth;

public class TestWorld {
    public static void main(String[] args) {
        World w = new World("src/main/resources/data/airport-codes_no_comma.csv");

        System.out.println("Nombre d'aéroports : " + w.getList().size());
        Aeroport paris = w.findNearestAirport(2.316, 48.866);
        System.out.println("Aéroport le plus proche de Paris : " + paris);
    }
}

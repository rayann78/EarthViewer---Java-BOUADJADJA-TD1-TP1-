package com.earth;

public class Aeroport { 
    private String name;
    private String codeIATA;
    private double latitude;
    private double longitude;

    public Aeroport(String name, String codeIATA, double latitude, double longitude) {
        this.name = name;
        this.codeIATA = codeIATA;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getCodeIATA() {
        return codeIATA;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /** 
     * Calcule la "distance" simplifiée entre cet aéroport et un autre,
     * selon la formule donnée dans l’énoncé.
     */
    public double calculDistance(Aeroport a) {
        double theta1 = Math.toRadians(this.latitude);
        double phi1 = Math.toRadians(this.longitude);
        double theta2 = Math.toRadians(a.latitude);
        double phi2 = Math.toRadians(a.longitude);

        return Math.pow(theta2 - theta1, 2)
                + Math.pow((phi2 - phi1) * Math.cos((theta2 + theta1) / 2), 2);
    }

    @Override
    public String toString() {
        return name + " (" + codeIATA + ") [" + latitude + ", " + longitude + "]";
    }
}

package com.medipass.models;

public class Antecedent {
    private int idAntecedent;
    private String type;
    private String description;
    private String date;
    private String gravite;
    private boolean actif;

    private static int compteur = 1; 

    public Antecedent(String type, String description, String date) {
        this.idAntecedent = compteur++;
        this.type = type;
        this.description = description;
        this.date = date;
        this.gravite = "Moyenne";
        this.actif = true;
    }

    // Getters
    public int getIdAntecedent() { 
        return idAntecedent;
    }
    public String getType() {
         return type; 
    }
    public String getDescription() { 
        return description; 
    }
    public String getDate() { 
        return date; 
    }
    public String getGravite() { 
        return gravite; 
    }
    public boolean isActif() {
        return actif; 
    }
    
    // Setters
    public void setGravite(String g) { 
        this.gravite = g; 
    }
    public void setActif(boolean a) { 
        this.actif = a; 
    }

    public void afficher() {
        System.out.print("  - [" + type + "] " + description + " (" + date + ", gravité: " + gravite + ")");
        if (!actif) {
            System.out.print(" [Inactif !]");
        }
        System.out.println();
    }

}

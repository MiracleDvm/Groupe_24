package com.medipass.models;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DossierMedical {
    private int idDossier;
    private String dateCreation;
    private String dateDerniereModification;
    private List<Antecedent> antecedents;
    // private List<Consultation> consultations; // Pour plus tard
    private Patient patient;

    private static int compteur = 5000;
    
    public DossierMedical(Patient patient) {
        this.idDossier = compteur++;
        this.patient = patient;
        this.antecedents = new ArrayList<>();
        // this.consultations = new ArrayList<>();
        
        // Date de création
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateCreation = LocalDate.now().format(formatter);
        this.dateDerniereModification = this.dateCreation;
    }

    // Getters
    public int getIdDossier() { 
        return idDossier; 
    }
    public String getDateCreation() { 
        return dateCreation; 
    }
    public String getDateDerniereModification() { 
        return dateDerniereModification; 
    }
    public List<Antecedent> getAntecedents() { 
        return antecedents; 
    }
    
    // Les autres Méthodes
    public void ajouterAntecedent(Antecedent ant) {
        if (ant != null) {
            antecedents.add(ant);
            
            // Mise à jour de la date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            this.dateDerniereModification = LocalDate.now().format(formatter);
        }
    }
    
    public void afficherAntecedents() {
        if (antecedents.isEmpty()) {
            System.out.println("  Aucun antécédent enregistré.");
            return;
        }
        
        System.out.println("\n  Antécédents médicaux (" + antecedents.size() + "):");
        for (Antecedent ant : antecedents) {
            ant.afficher();
        }
    }
    
    public void getHistorique() {
        System.out.println("\n=== Dossier Médical #" + idDossier + " ===");
        System.out.println("Patient: " + patient.getNom() + " " + patient.getPrenom());
        System.out.println("Créé le: " + dateCreation);
        System.out.println("Dernière modification: " + dateDerniereModification);
        afficherAntecedents();
    }
}

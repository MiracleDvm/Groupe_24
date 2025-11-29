package com.medipass.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvWriteOptions;

import com.medipass.model.*;
import com.medipass.user.*;

/**
 * Service de gestion des données avec Tablesaw 0.43.1
 * Gère le chargement (racine du projet) et la sauvegarde/exportation (dossier exportedFiles/).
 */
public class DataService {

    // Fichiers d'entrée (lecture) restent à la racine du projet
    private static final String PATIENTS_FILE = "patients.csv";
    private static final String PROS_FILE = "pros.csv";
    private static final String CONSULTATIONS_FILE = "consultations.csv";

    // Dossier de sortie pour les exports (Relatif à la racine du projet)
    private static final String EXPORT_DIR = "com/medipass/exportedFiles/";
    
    // ==================== Méthodes Utilitaires d'Exportation ====================

    /**
     * Crée le répertoire d'exportation s'il n'existe pas.
     */
    private void ensureExportDirectoryExists() throws IOException {
        File dir = new File(EXPORT_DIR);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new IOException("Impossible de créer le répertoire d'exportation : " + EXPORT_DIR);
            }
            System.out.println("Création du répertoire d'exportation : " + EXPORT_DIR);
        }
    }

    /**
     * Retourne le chemin du répertoire d'exportation.
     */
    public String getExportDir() {
        return EXPORT_DIR;
    }
    
    // NOUVEAU: Méthode générique pour lire une Table du dossier d'export
    private Table loadTableFromExportDir(String filename) throws IOException {
        String filePath = EXPORT_DIR + filename;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Le fichier d'export n'existe pas : " + filePath);
        }
        // Utiliser les options de lecture qui gèrent le séparateur (;)
        CsvReadOptions options = CsvReadOptions.builder(file)
            .separator(';')
            .header(true)
            .missingValueIndicator("", "null", "NULL")
            .build();
        return Table.read().usingOptions(options);
    }
    
    // NOUVEAU: Méthodes pour charger les tables à partir du dossier d'export
    public Table loadPatientsTableForExport() throws IOException {
        return loadTableFromExportDir(PATIENTS_FILE);
    }
    public Table loadProfessionnelsTableForExport() throws IOException {
        return loadTableFromExportDir(PROS_FILE);
    }
    public Table loadConsultationsTableForExport() throws IOException {
        return loadTableFromExportDir(CONSULTATIONS_FILE);
    }

    /**
     * Sauvegarde la table fusionnée dans le dossier d'exportation. (Conservé, mais plus utilisé par CSVExportService)
     */
    public void saveFinalExport(Table finalTable, String filename) throws IOException {
        ensureExportDirectoryExists();
        
        CsvWriteOptions options = CsvWriteOptions.builder(new File(EXPORT_DIR + filename))
            .separator(';')
            .header(true)
            .build();
            
        finalTable.write().csv(options);
    }

    // ==================== MÉTHODES PRIVÉES DE CRÉATION DE TABLE (REFRACTORISATION) ====================
    
    private CsvWriteOptions getDefaultWriteOptions(String fullPath) {
        return CsvWriteOptions.builder(new File(fullPath))
            .separator(';')
            .header(true)
            .build();
    }

    /**
     * Crée et remplit la Table pour les Patients.
     */
    private Table createPatientTable(List<Patient> patients) {
        Table table = Table.create("Patients");
        
        table.addColumns(
            tech.tablesaw.api.IntColumn.create("id"),
            tech.tablesaw.api.StringColumn.create("nom"),
            tech.tablesaw.api.StringColumn.create("prenom"),
            tech.tablesaw.api.StringColumn.create("numeroSecuriteSociale"),
            tech.tablesaw.api.StringColumn.create("groupeSanguin")
        );

        for (Patient p : patients) {
            Row row = table.appendRow();
            row.setInt("id", p.getId());
            row.setString("nom", p.getNom());
            row.setString("prenom", p.getPrenom());
            row.setString("numeroSecuriteSociale", 
                p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "");
            row.setString("groupeSanguin", 
                p.getGroupeSanguin() != null ? p.getGroupeSanguin() : "");
        }
        return table;
    }
    
    /**
     * Crée et remplit la Table pour les Professionnels de Santé.
     */
    private Table createProsTable(List<ProfessionnelSante> pros) {
        Table table = Table.create("Professionnels");
        
        table.addColumns(
            tech.tablesaw.api.StringColumn.create("login"),
            tech.tablesaw.api.StringColumn.create("password"),
            tech.tablesaw.api.StringColumn.create("nom"),
            tech.tablesaw.api.StringColumn.create("prenom"),
            tech.tablesaw.api.StringColumn.create("specialite"),
            tech.tablesaw.api.StringColumn.create("numeroOrdre"),
            tech.tablesaw.api.StringColumn.create("horairesDisponibilite")
        );

        for (ProfessionnelSante p : pros) {
            Row row = table.appendRow();
            row.setString("login", p.getLoginID());
            row.setString("password", p.getPassword());
            row.setString("nom", p.getNom());
            row.setString("prenom", p.getPrenom());
            row.setString("specialite", p.getSpecialite());
            row.setString("numeroOrdre", p.getNumeroOrdre());
            row.setString("horairesDisponibilite", p.getHorairesDisponibilite());
        }
        return table;
    }

    /**
     * Crée et remplit la Table pour les Consultations.
     */
    private Table createConsultationTable(List<Consultation> consultations) {
        Table table = Table.create("Consultations");
        
        table.addColumns(
            tech.tablesaw.api.StringColumn.create("dateHeure"),
            tech.tablesaw.api.StringColumn.create("motif"),
            tech.tablesaw.api.StringColumn.create("professionnelLogin"),
            tech.tablesaw.api.IntColumn.create("patientId"),
            tech.tablesaw.api.IntColumn.create("dureeMinutes"),
            tech.tablesaw.api.StringColumn.create("statut"),
            tech.tablesaw.api.StringColumn.create("observations"),
            tech.tablesaw.api.StringColumn.create("diagnostic")
        );

        for (Consultation c : consultations) {
            Row row = table.appendRow();
            row.setString("dateHeure", c.getDateHeure().toString());
            row.setString("motif", c.getMotif());
            row.setString("professionnelLogin", c.getProfessionnel().getLoginID());
            row.setInt("patientId", c.getPatient().getId());
            row.setInt("dureeMinutes", c.getDureeMinutes());
            row.setString("statut", c.getStatut());
            row.setString("observations", 
                c.getObservations() != null ? c.getObservations().replace(";", ",") : "");
            row.setString("diagnostic", 
                c.getDiagnostic() != null ? c.getDiagnostic().replace(";", ",") : "");
        }
        return table;
    }

    // ==================== PATIENTS (CHARGEMENT & SAUVEGARDE) ====================

    /**
     * Sauvegarde la liste des patients dans le fichier par défaut (PATIENTS_FILE).
     */
    public void savePatients(List<Patient> patients) {
        savePatientsAs(patients, PATIENTS_FILE);
    }
    
    /**
     * NOUVEAU: Sauvegarde la liste des patients dans un fichier CSV avec un nom spécifié.
     */
    public void savePatientsAs(List<Patient> patients, String filename) {
        try {
            ensureExportDirectoryExists();
            String exportPath = EXPORT_DIR + filename;
            
            Table table = createPatientTable(patients); // Utilise la méthode privée
            
            CsvWriteOptions options = getDefaultWriteOptions(exportPath);
            
            table.write().csv(options);
            System.out.println("✓ " + patients.size() + " patients exportés vers " + exportPath);
            
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde patients dans " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Charge la liste des patients depuis le fichier CSV (à la racine)
     */
    public List<Patient> loadPatients() {
        // ... (Logique de chargement inchangée) ...
        List<Patient> patients = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        
        if (!file.exists()) {
            return patients;
        }

        try {
            // Configuration de lecture avec séparateur point-virgule
            CsvReadOptions options = CsvReadOptions.builder(file)
                .separator(';')
                .header(true)
                .missingValueIndicator("", "null", "NULL")
                .build();
            
            Table patientTable = Table.read().csv(options);

            // Parcourir chaque ligne
            for (Row row : patientTable) {
                try {
                    int id = row.getInt("id");
                    String nom = row.getString("nom");
                    String prenom = row.getString("prenom");
                    
                    Patient p = new Patient(id, nom, prenom);

                    // Champs optionnels
                    String nss = row.getString("numeroSecuriteSociale");
                    if (nss != null && !nss.isEmpty()) {
                        p.setNumeroSecuriteSociale(nss);
                    }

                    String gs = row.getString("groupeSanguin");
                    if (gs != null && !gs.isEmpty()) {
                        p.setGroupeSanguin(gs);
                    }
                    
                    patients.add(p);
                    
                } catch (Exception e) {
                    System.err.println("Erreur parsing patient ligne " + row.getRowNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement patients: " + e.getMessage());
        }
        
        return patients;
    }

    // ==================== PROFESSIONNELS (CHARGEMENT & SAUVEGARDE) ====================

    /**
     * Sauvegarde la liste des professionnels dans le fichier par défaut (PROS_FILE).
     */
    public void saveProfessionnels(List<ProfessionnelSante> pros) {
        saveProfessionnelsAs(pros, PROS_FILE);
    }
    
    /**
     * NOUVEAU: Sauvegarde la liste des professionnels dans un fichier CSV avec un nom spécifié.
     */
    public void saveProfessionnelsAs(List<ProfessionnelSante> pros, String filename) {
        try {
            ensureExportDirectoryExists();
            String exportPath = EXPORT_DIR + filename;
            
            Table table = createProsTable(pros); // Utilise la méthode privée
            
            CsvWriteOptions options = getDefaultWriteOptions(exportPath);
            
            table.write().csv(options);
            System.out.println("✓ " + pros.size() + " professionnels exportés vers " + exportPath);
            
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde professionnels dans " + filename + ": " + e.getMessage());
        }
    }


    /**
     * Charge la liste des professionnels depuis un fichier CSV (à la racine)
     */
    public List<ProfessionnelSante> loadProfessionnels() {
        // ... (Logique de chargement inchangée) ...
        List<ProfessionnelSante> pros = new ArrayList<>();
        File file = new File(PROS_FILE);
        
        if (!file.exists()) {
            return pros;
        }

        try {
            CsvReadOptions options = CsvReadOptions.builder(file)
                .separator(';')
                .header(true)
                .missingValueIndicator("", "null", "NULL")
                .build();
            
            Table prosTable = Table.read().csv(options);

            for (Row row : prosTable) {
                try {
                    String login = row.getString("login");
                    String password = row.getString("password");
                    String nom = row.getString("nom");
                    String prenom = row.getString("prenom");
                    String specialite = row.getString("specialite");
                    String numeroOrdre = row.getString("numeroOrdre");
                    
                    ProfessionnelSante p = new ProfessionnelSante(
                        login, password, "PRO", nom, prenom, specialite, numeroOrdre
                    );
                    
                    String horaires = row.getString("horairesDisponibilite");
                    if (horaires != null && !horaires.isEmpty()) {
                        p.setHorairesDisponibilite(horaires);
                    }
                    
                    pros.add(p);
                    
                } catch (Exception e) {
                    System.err.println("Erreur parsing professionnel ligne " + row.getRowNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement professionnels: " + e.getMessage());
        }
        
        return pros;
    }

    // ==================== CONSULTATIONS (CHARGEMENT & SAUVEGARDE) ====================

    /**
     * Sauvegarde la liste des consultations dans le fichier par défaut (CONSULTATIONS_FILE).
     */
    public void saveConsultations(List<Consultation> consultations) {
        saveConsultationsAs(consultations, CONSULTATIONS_FILE);
    }
    
    /**
     * NOUVEAU: Sauvegarde la liste des consultations dans un fichier CSV avec un nom spécifié.
     */
    public void saveConsultationsAs(List<Consultation> consultations, String filename) {
        try {
            ensureExportDirectoryExists();
            String exportPath = EXPORT_DIR + filename;
            
            Table table = createConsultationTable(consultations); // Utilise la méthode privée
            
            CsvWriteOptions options = getDefaultWriteOptions(exportPath);
            
            table.write().csv(options);
            System.out.println("✓ " + consultations.size() + " consultations exportées vers " + exportPath);
            
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde consultations dans " + filename + ": " + e.getMessage());
        }
    }


    /**
     * Charge la liste des consultations depuis un fichier CSV (à la racine)
     */
    public List<Consultation> loadConsultations(List<Patient> patients, List<ProfessionnelSante> pros) {
        // ... (Logique de chargement inchangée) ...
        List<Consultation> consultations = new ArrayList<>();
        File file = new File(CONSULTATIONS_FILE);
        
        if (!file.exists()) {
            return consultations;
        }

        try {
            CsvReadOptions options = CsvReadOptions.builder(file)
                .separator(';')
                .header(true)
                .missingValueIndicator("", "null", "NULL")
                .build();
            
            Table consultTable = Table.read().csv(options);

            for (Row row : consultTable) {
                try {
                    LocalDateTime date = LocalDateTime.parse(row.getString("dateHeure"));
                    String motif = row.getString("motif");
                    String proLogin = row.getString("professionnelLogin");
                    int patientId = row.getInt("patientId");

                    ProfessionnelSante pro = pros.stream()
                        .filter(p -> p.getLoginID().equals(proLogin))
                        .findFirst().orElse(null);

                    Patient patient = patients.stream()
                        .filter(p -> p.getId() == patientId)
                        .findFirst().orElse(null);

                    if (pro != null && patient != null) {
                        Consultation c = new Consultation(date, motif, pro, patient);
                        
                        // Champs optionnels
                        try {
                            c.setDureeMinutes(row.getInt("dureeMinutes"));
                        } catch (Exception e) { /* Ignorer si non un entier */ }
                        
                        String statut = row.getString("statut");
                        if (statut != null && !statut.isEmpty()) {
                            c.setStatut(statut);
                        }
                        
                        String obs = row.getString("observations");
                        if (obs != null && !obs.isEmpty()) {
                            c.setObservations(obs);
                        }
                        
                        String diag = row.getString("diagnostic");
                        if (diag != null && !diag.isEmpty()) {
                            c.setDiagnostic(diag);
                        }

                        consultations.add(c);

                        // Re-link to objects
                        pro.ajouterConsultation(c);
                        patient.getDossierMedical().ajouterConsultation(c);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur parsing consultation ligne " + row.getRowNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement consultations: " + e.getMessage());
        }
        
        return consultations;
    }
}
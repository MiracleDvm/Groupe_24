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
<<<<<<< HEAD
 * Service de persistance des données en fichiers CSV
=======
 * Service de gestion des données avec Tablesaw 0.43.1
 * Gère le chargement (racine du projet) et la sauvegarde/exportation (dossier exportedFiles/).
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
 * Gère le chargement (racine du projet) et la sauvegarde/exportation (dossier
 * exportedFiles/).
 */
public class DataService {

    // Fichiers d'entrée (lecture) restent à la racine du projet
    private static final String PATIENTS_FILE = "patients.csv";
    private static final String PROS_FILE = "pros.csv";
    private static final String CONSULTATIONS_FILE = "consultations.csv";
    private static final String ANTECEDENTS_FILE = "antecedents.csv";

    // ========== PATIENTS ==========

<<<<<<< HEAD
    public void savePatients(List<Patient> patients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            // Écrire l'en-tête
            writer.println("id;nom;prenom;sexe;dateDeNaissance;numeroSecuriteSociale;groupeSanguin");
            for (Patient p : patients) {
                writer.printf("%d;%s;%s;%s;%s;%s;%s\n",
                        p.getId(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getSexe(),
                        p.getDateDeNaissance(),
                        p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "",
                        p.getGroupeSanguin() != null ? p.getGroupeSanguin() : "");
=======
    // Dossier de sortie pour les exports (Relatif à la racine du projet)
    private static final String EXPORT_DIR = "exportedFiles/";

    // ==================== Méthodes Utilitaires d'Exportation ====================

    /**
     * Crée le répertoire d'exportation s'il n'existe pas.
     */
    // Dans DataService.java
    private void ensureExportDirectoryExists() throws IOException {
        File dir = new File(EXPORT_DIR);

        // NOUVEAU: Afficher le chemin absolu pour le débogage
        System.out.println("\nDEBUG: Tentative d'écriture dans le répertoire: " + dir.getAbsolutePath());

        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new IOException("Impossible de créer le répertoire d'exportation : " + EXPORT_DIR);
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
                // Afficher le chemin absolu en cas d'échec de création
                throw new IOException("Impossible de créer le répertoire d'exportation : " + dir.getAbsolutePath());
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
     * Sauvegarde la table fusionnée dans le dossier d'exportation. (Conservé, mais
     * plus utilisé par CSVExportService)
     */
    public void saveFinalExport(Table finalTable, String filename) throws IOException {
        ensureExportDirectoryExists();

        CsvWriteOptions options = CsvWriteOptions.builder(new File(EXPORT_DIR + filename))
                .separator(';')
                .header(true)
                .build();

        finalTable.write().csv(options);
    }

    // ==================== MÉTHODES PRIVÉES DE CRÉATION DE TABLE (REFRACTORISATION)
    // ====================

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
                tech.tablesaw.api.StringColumn.create("groupeSanguin"));

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
                tech.tablesaw.api.StringColumn.create("horairesDisponibilite"));

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
                tech.tablesaw.api.StringColumn.create("diagnostic"));

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


// ==================== GESTION IMPORTATION CSV ====================


/**
 * Lit un fichier CSV générique à partir d'un chemin spécifié (pour l'importation).
 */
private Table readImportFile(String absoluteOrRelativePath) throws IOException {
    File file = new File(absoluteOrRelativePath);
    if (!file.exists()) {
        throw new FileNotFoundException("Le fichier d'import n'existe pas : " + absoluteOrRelativePath);
    }
    
    // Utiliser les options de lecture communes (séparateur point-virgule)
    CsvReadOptions options = CsvReadOptions.builder(file)
        .separator(';')
        .header(true)
        .missingValueIndicator("", "null", "NULL")
        .build();
        
    return Table.read().usingOptions(options);
}

// ----------------------------------------------------------------------------------

/**
 * Importe les données Patients à partir d'un fichier CSV spécifié et les sauvegarde dans PATIENTS_FILE.
 * @param importFilePath Le chemin absolu ou relatif vers le fichier CSV source.
 * @param existingPatients La liste actuelle des patients en mémoire (pour l'ajout).
 * @return Le nombre de nouveaux patients importés.
 */
public int importPatients(String importFilePath, List<Patient> existingPatients) throws Exception {
    Table importTable = readImportFile(importFilePath);
    
    int newPatientsCount = 0;
    
    // Parcourir chaque ligne de la table importée
    for (Row row : importTable) {
        // Validation basique de la colonne 'id'
        if (!row.columnNames().contains("id")) {
            System.err.println("❌ Fichier invalide: La colonne 'id' est manquante.");
            throw new Exception("Structure de fichier Patients invalide.");
        }
        
        try {
            int id = row.getInt("id");
            String nom = row.getString("nom");
            String prenom = row.getString("prenom");
            
            // Vérifier si le patient existe déjà (si l'ID est déjà utilisé)
            boolean exists = existingPatients.stream().anyMatch(p -> p.getId() == id);
            
            if (!exists) {
                // Crée le nouvel objet Patient
                Patient newPatient = new Patient(id, nom, prenom);

                // Gérer les champs optionnels
                String nss = row.getString("numeroSecuriteSociale");
                if (nss != null && !nss.isEmpty()) newPatient.setNumeroSecuriteSociale(nss);

                String gs = row.getString("groupeSanguin");
                if (gs != null && !gs.isEmpty()) newPatient.setGroupeSanguin(gs);
                
                existingPatients.add(newPatient); // Ajoute à la liste en mémoire
                newPatientsCount++;
            } else {
                 System.out.println("Avertissement: Patient avec ID " + id + " déjà existant. Ignoré.");
            }
        } catch (Exception e) {
            System.err.println("Erreur parsing patient ligne " + row.getRowNumber() + " dans le fichier import: " + e.getMessage());
        }
    }
    
    // Sauvegarder la liste complète (existante + importée) dans le fichier d'origine
    savePatients(existingPatients); // savePatients utilise le fichier PATIENTS_FILE par défaut
    
    return newPatientsCount;
}

// ----------------------------------------------------------------------------------

/**
 * Importe les données Professionnels à partir d'un fichier CSV spécifié et les sauvegarde dans PROS_FILE.
 * @param importFilePath Le chemin absolu ou relatif vers le fichier CSV source.
 * @param existingPros La liste actuelle des professionnels en mémoire.
 * @return Le nombre de nouveaux professionnels importés.
 */
public int importProfessionnels(String importFilePath, List<ProfessionnelSante> existingPros) throws Exception {
    Table importTable = readImportFile(importFilePath);
    
    int newProsCount = 0;
    
    for (Row row : importTable) {
        if (!row.columnNames().contains("login")) {
            System.err.println("❌ Fichier invalide: La colonne 'login' est manquante.");
            throw new Exception("Structure de fichier Professionnels invalide.");
        }
        
        try {
            String login = row.getString("login");
            String password = row.getString("password");
            String nom = row.getString("nom");
            String prenom = row.getString("prenom");
            String specialite = row.getString("specialite");
            String numeroOrdre = row.getString("numeroOrdre");
            
            // Vérifier si le professionnel existe déjà (si le login est déjà utilisé)
            boolean exists = existingPros.stream().anyMatch(p -> p.getLoginID().equals(login));
            
            if (!exists) {
                ProfessionnelSante newPro = new ProfessionnelSante(
                    login, password, "PRO", nom, prenom, specialite, numeroOrdre
                );
                
                String horaires = row.getString("horairesDisponibilite");
                if (horaires != null && !horaires.isEmpty()) newPro.setHorairesDisponibilite(horaires);
                
                existingPros.add(newPro); // Ajoute à la liste en mémoire
                newProsCount++;
            } else {
                 System.out.println("Avertissement: Professionnel avec login " + login + " déjà existant. Ignoré.");
            }
        } catch (Exception e) {
            System.err.println("Erreur parsing professionnel ligne " + row.getRowNumber() + " dans le fichier import: " + e.getMessage());
        }
    }
    
    // Sauvegarder la liste complète dans le fichier d'origine
    saveProfessionnels(existingPros); 
    
    return newProsCount;
}

// ----------------------------------------------------------------------------------

/**
 * Importe les données Consultations à partir d'un fichier CSV spécifié et les sauvegarde dans CONSULTATIONS_FILE.
 * NOTE: Les consultations nécessitent que les Patients et Professionnels existent déjà pour être valides.
 * @param importFilePath Le chemin absolu ou relatif vers le fichier CSV source.
 * @param existingConsultations La liste actuelle des consultations en mémoire.
 * @param allPatients La liste complète des patients (pour l'association).
 * @param allPros La liste complète des professionnels (pour l'association).
 * @return Le nombre de nouvelles consultations importées.
 */
public int importConsultations(String importFilePath, List<Consultation> existingConsultations, 
                               List<Patient> allPatients, List<ProfessionnelSante> allPros) throws Exception {
    Table importTable = readImportFile(importFilePath);
    
    int newConsultationsCount = 0;
    
    for (Row row : importTable) {
        // Validation des colonnes clés
        if (!row.columnNames().contains("patientId") || !row.columnNames().contains("professionnelLogin")) {
            System.err.println("❌ Fichier invalide: Colonnes clés manquantes.");
            throw new Exception("Structure de fichier Consultations invalide.");
        }
        
        try {
            LocalDateTime date = LocalDateTime.parse(row.getString("dateHeure"));
            String motif = row.getString("motif");
            String proLogin = row.getString("professionnelLogin");
            int patientId = row.getInt("patientId");

            // Recherche des objets associés (Patients et Pros)
            ProfessionnelSante pro = allPros.stream()
                .filter(p -> p.getLoginID().equals(proLogin))
                .findFirst().orElse(null);

            Patient patient = allPatients.stream()
                .filter(p -> p.getId() == patientId)
                .findFirst().orElse(null);

            if (pro != null && patient != null) {
                // Pas de vérification de doublon facile pour les consultations, nous ajoutons simplement
                Consultation newConsultation = new Consultation(date, motif, pro, patient);
                
                // Champs optionnels
                try { newConsultation.setDureeMinutes(row.getInt("dureeMinutes")); } catch (Exception e) { /* Ignorer */ }
                String statut = row.getString("statut");
                if (statut != null && !statut.isEmpty()) newConsultation.setStatut(statut);
                String obs = row.getString("observations");
                if (obs != null && !obs.isEmpty()) newConsultation.setObservations(obs.replace(";", ","));
                String diag = row.getString("diagnostic");
                if (diag != null && !diag.isEmpty()) newConsultation.setDiagnostic(diag.replace(";", ","));

                existingConsultations.add(newConsultation); // Ajout en mémoire
                
                // Re-link to objects (important pour l'état en mémoire)
                pro.ajouterConsultation(newConsultation);
                patient.getDossierMedical().ajouterConsultation(newConsultation);
                
                newConsultationsCount++;
            } else {
                System.out.println("Avertissement: Consultation ignorée (Patient ID: " + patientId + " ou Pro Login: " + proLogin + " non trouvé).");
            }
        } catch (Exception e) {
            System.err.println("Erreur parsing consultation ligne " + row.getRowNumber() + " dans le fichier import: " + e.getMessage());
        }
    }
    
    // Sauvegarder la liste complète dans le fichier d'origine
    saveConsultations(existingConsultations);
    
    return newConsultationsCount;
}

    // ==================== PATIENTS (CHARGEMENT & SAUVEGARDE) ====================

    /**
     * Sauvegarde la liste des patients dans le fichier par défaut (PATIENTS_FILE).
     */
    public void savePatients(List<Patient> patients) {
        savePatientsAs(patients, PATIENTS_FILE);
    }

    /**
     * NOUVEAU: Sauvegarde la liste des patients dans un fichier CSV avec un nom
     * spécifié.
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

<<<<<<< HEAD
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        Patient p = new Patient(id, parts[1], parts[2]);
                        if (parts.length > 3 && !parts[3].isEmpty()) {
                            p.setNumeroSecuriteSociale(parts[3]);
                        }
                        if (parts.length > 4 && !parts[4].isEmpty()) {
                            p.setGroupeSanguin(parts[4]);
                        }
                        patients.add(p);
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur parsing patient: " + e.getMessage());
=======
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
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
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

<<<<<<< HEAD
    // ========== PROFESSIONNELS ==========

    public void saveProfessionnels(List<ProfessionnelSante> pros) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROS_FILE))) {
            // Écrire l'en-tête
            writer.println("login;password;accessLevel;nom;prenom;specialite;numeroOrdre;horairesDisponibilite");
            for (ProfessionnelSante p : pros) {
                writer.printf("%s;%s;%s;%s;%s;%s;%s;%s\n",
                        p.getLoginID(),
                        p.getPassword(),
                        p.getAccessLevels(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getSpecialite(),
                        p.getNumeroOrdre(),
                        p.getHorairesDisponibilite());
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde professionnels: " + e.getMessage());
=======
    // ==================== PROFESSIONNELS (CHARGEMENT & SAUVEGARDE) ====================
    // ==================== PROFESSIONNELS (CHARGEMENT & SAUVEGARDE)
    // ====================

    /**
     * Sauvegarde la liste des professionnels dans le fichier par défaut
     * (PROS_FILE).
     */
    public void saveProfessionnels(List<ProfessionnelSante> pros) {
        saveProfessionnelsAs(pros, PROS_FILE);
    }

    /**
     * NOUVEAU: Sauvegarde la liste des professionnels dans un fichier CSV avec un
     * nom spécifié.
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
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
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

<<<<<<< HEAD
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 6) {
                    try {
                        ProfessionnelSante p = new ProfessionnelSante(
                                parts[0], parts[1], "PRO", parts[2], parts[3], parts[4], parts[5], parts[6]);
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            p.setHorairesDisponibilite(parts[6]);
                        }
                        pros.add(p);
                    } catch (Exception e) {
                        System.err.println("Erreur parsing professionnel: " + e.getMessage());
=======
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
                            login, password, "PRO", nom, prenom, specialite, numeroOrdre);

                    String horaires = row.getString("horairesDisponibilite");
                    if (horaires != null && !horaires.isEmpty()) {
                        p.setHorairesDisponibilite(horaires);
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
                    }

                    pros.add(p);

                } catch (Exception e) {
                    System.err.println(
                            "Erreur parsing professionnel ligne " + row.getRowNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement professionnels: " + e.getMessage());
        }

        return pros;
    }

<<<<<<< HEAD
    // ========== CONSULTATIONS ==========

    public void saveConsultations(List<Consultation> consultations) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONSULTATIONS_FILE))) {
            writer.println("dateHeure;motif;professionnelLogin;patientId;dureeMinutes;statut;observations;diagnostic");
            for (Consultation c : consultations) {
                writer.printf("%s;%s;%s;%d;%d;%s;%s;%s\n",
                        c.getDateHeure().toString(),
                        c.getMotif(),
                        c.getProfessionnel().getLoginID(),
                        c.getPatient().getId(),
                        c.getDureeMinutes(),
                        c.getStatut(),
                        c.getObservations() != null ? c.getObservations().replace(";", ",") : "",
                        c.getDiagnostic() != null ? c.getDiagnostic().replace(";", ",") : "");
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde consultations: " + e.getMessage());
=======
    // ==================== CONSULTATIONS (CHARGEMENT & SAUVEGARDE) ====================
    // ==================== CONSULTATIONS (CHARGEMENT & SAUVEGARDE)
    // ====================

    /**
     * Sauvegarde la liste des consultations dans le fichier par défaut
     * (CONSULTATIONS_FILE).
     */
    public void saveConsultations(List<Consultation> consultations) {
        saveConsultationsAs(consultations, CONSULTATIONS_FILE);
    }

    /**
     * NOUVEAU: Sauvegarde la liste des consultations dans un fichier CSV avec un
     * nom spécifié.
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
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
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

<<<<<<< HEAD
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    try {
                        LocalDateTime date = LocalDateTime.parse(parts[0]);
                        String motif = parts[1];
                        String proLogin = parts[2];
                        int patientId = Integer.parseInt(parts[3]);
=======
        try {
            CsvReadOptions options = CsvReadOptions.builder(file)
                    .separator(';')
                    .header(true)
                    .missingValueIndicator("", "null", "NULL")
                    .build();

            Table consultTable = Table.read().csv(options);
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242

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
                        } catch (Exception e) {
                            /* Ignorer si non un entier */ }

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
                    System.err
                            .println("Erreur parsing consultation ligne " + row.getRowNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement consultations: " + e.getMessage());
        }

        return consultations;
    }
<<<<<<< HEAD

    // ========== ANTÉCÉDENTS ==========

    /**
     * Sauvegarde les antécédents de tous les patients
     */
    public void saveAntecedents(List<Patient> patients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ANTECEDENTS_FILE))) {
            writer.println("patientId;type;description;date;gravite;actif");
            for (Patient p : patients) {
                for (Antecedent a : p.getDossierMedical().getAntecedents()) {
                    writer.printf("%d;%s;%s;%s;%s;%s\n",
                        p.getId(),
                        a.getType(),
                        a.getDescription() != null ? a.getDescription().replace(";", ",") : "",
                        a.getDate(),
                        a.getGravite(),
                        a.isActif()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde antécédents: " + e.getMessage());
        }
    }

    /**
     * Charge les antécédents depuis le fichier CSV
     */
    public void loadAntecedents(List<Patient> patients) {
        File file = new File(ANTECEDENTS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    try {
                        int patientId = Integer.parseInt(parts[0]);
                        Patient patient = patients.stream()
                            .filter(p -> p.getId() == patientId)
                            .findFirst().orElse(null);
                        
                        if (patient != null) {
                            Antecedent ant = new Antecedent(
                                parts[1], // type
                                parts[2], // description
                                LocalDate.parse(parts[3]), // date
                                parts[4], // gravité
                                parts.length > 5 ? Boolean.parseBoolean(parts[5]) : true // actif
                            );
                            patient.getDossierMedical().ajouterAntecedent(ant);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur parsing antécédent: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement antécédents: " + e.getMessage());
        }
    }
=======
>>>>>>> 85fc14e8dd740cac6c1046a2201ef4aea3ad4242
}
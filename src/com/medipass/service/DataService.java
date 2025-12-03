package com.medipass.service;

import java.io.*;
import java.time.LocalDate;
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
    private static final String ANTECEDENTS_FILE = "antecedents.csv";

    // Dossier de sortie pour les exports (Relatif à la racine du projet)
    private static final String EXPORT_DIR = "exportedFiles/";

    // ==================== Méthodes Utilitaires d'Exportation ====================

    private void ensureExportDirectoryExists() throws IOException {
        File dir = new File(EXPORT_DIR);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new IOException("Impossible de créer le répertoire d'exportation : " + dir.getAbsolutePath());
            }
        }
    }

    public String getExportDir() {
        return EXPORT_DIR;
    }

    private Table loadTableFromExportDir(String filename) throws IOException {
        String filePath = EXPORT_DIR + filename;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Le fichier d'export n'existe pas : " + filePath);
        }
        CsvReadOptions options = CsvReadOptions.builder(file)
                .separator(';')
                .header(true)
                .missingValueIndicator("", "null", "NULL")
                .build();
        return Table.read().usingOptions(options);
    }

    public Table loadPatientsTableForExport() throws IOException {
        return loadTableFromExportDir(PATIENTS_FILE);
    }

    public Table loadProfessionnelsTableForExport() throws IOException {
        return loadTableFromExportDir(PROS_FILE);
    }

    public Table loadConsultationsTableForExport() throws IOException {
        return loadTableFromExportDir(CONSULTATIONS_FILE);
    }

    private CsvWriteOptions getDefaultWriteOptions(String fullPath) {
        return CsvWriteOptions.builder(new File(fullPath))
                .separator(';')
                .header(true)
                .build();
    }

    // ==================== MÉTHODES PRIVÉES DE CRÉATION DE TABLE ====================

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
            row.setString("numeroSecuriteSociale", p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "");
            row.setString("groupeSanguin", p.getGroupeSanguin() != null ? p.getGroupeSanguin() : "");
        }
        return table;
    }

    private Table createProsTable(List<ProfessionnelSante> pros) {
        Table table = Table.create("Professionnels");
        table.addColumns(
                tech.tablesaw.api.StringColumn.create("login"),
                tech.tablesaw.api.StringColumn.create("password"),
                tech.tablesaw.api.StringColumn.create("accessLevels"),
                tech.tablesaw.api.StringColumn.create("nom"),
                tech.tablesaw.api.StringColumn.create("prenom"),
                tech.tablesaw.api.StringColumn.create("specialite"),
                tech.tablesaw.api.StringColumn.create("numeroOrdre"),
                tech.tablesaw.api.StringColumn.create("horairesDisponibilite"));

        for (ProfessionnelSante p : pros) {
            Row row = table.appendRow();
            row.setString("login", p.getLoginID());
            row.setString("password", p.getPassword());
            row.setString("accessLevels", p.getAccessLevels());
            row.setString("nom", p.getNom());
            row.setString("prenom", p.getPrenom());
            row.setString("specialite", p.getSpecialite());
            row.setString("numeroOrdre", p.getNumeroOrdre());
            row.setString("horairesDisponibilite", p.getHorairesDisponibilite());
        }
        return table;
    }

    private Table createConsultationTable(List<Consultation> consultations) {
        Table table = Table.create("Consultations");
        table.addColumns(
                tech.tablesaw.api.IntColumn.create("idConsultation"),
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
            row.setInt("idConsultation", c.getIdConsultation());
            row.setString("dateHeure", c.getDateHeure().toString());
            row.setString("motif", c.getMotif());
            row.setString("professionnelLogin", c.getProfessionnel().getLoginID());
            row.setInt("patientId", c.getPatient().getId());
            row.setInt("dureeMinutes", c.getDureeMinutes());
            row.setString("statut", c.getStatut());
            row.setString("observations", c.getObservations() != null ? c.getObservations().replace(";", ",") : "");
            row.setString("diagnostic", c.getDiagnostic() != null ? c.getDiagnostic().replace(";", ",") : "");
        }
        return table;
    }

    // ==================== GESTION IMPORTATION CSV ====================

    private Table readImportFile(String absoluteOrRelativePath) throws IOException {
        File file = new File(absoluteOrRelativePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Le fichier d'import n'existe pas : " + absoluteOrRelativePath);
        }
        CsvReadOptions options = CsvReadOptions.builder(file)
            .separator(';')
            .header(true)
            .missingValueIndicator("", "null", "NULL")
            .build();
        return Table.read().usingOptions(options);
    }

    public int importPatients(String importFilePath, List<Patient> existingPatients) throws Exception {
        Table importTable = readImportFile(importFilePath);
        int newPatientsCount = 0;
        for (Row row : importTable) {
            if (!row.columnNames().contains("id")) {
                throw new Exception("Structure de fichier Patients invalide.");
            }
            try {
                int id = row.getInt("id");
                boolean exists = existingPatients.stream().anyMatch(p -> p.getId() == id);
                if (!exists) {
                    String nom = row.getString("nom");
                    String prenom = row.getString("prenom");
                    Patient newPatient = new Patient(id, nom, prenom);
                    if (row.columnNames().contains("numeroSecuriteSociale")) {
                         String nss = row.getString("numeroSecuriteSociale");
                         if (nss != null) newPatient.setNumeroSecuriteSociale(nss);
                    }
                    if (row.columnNames().contains("groupeSanguin")) {
                        String gs = row.getString("groupeSanguin");
                        if (gs != null) newPatient.setGroupeSanguin(gs);
                    }
                    existingPatients.add(newPatient);
                    newPatientsCount++;
                }
            } catch (Exception e) {
                System.err.println("Erreur parsing patient: " + e.getMessage());
            }
        }
        savePatients(existingPatients);
        return newPatientsCount;
    }

    public int importProfessionnels(String importFilePath, List<ProfessionnelSante> existingPros) throws Exception {
        Table importTable = readImportFile(importFilePath);
        int newProsCount = 0;
        for (Row row : importTable) {
            if (!row.columnNames().contains("login")) {
                throw new Exception("Structure de fichier Professionnels invalide.");
            }
            try {
                String login = row.getString("login");
                boolean exists = existingPros.stream().anyMatch(p -> p.getLoginID().equals(login));
                if (!exists) {
                    String password = row.getString("password");
                    String nom = row.getString("nom");
                    String prenom = row.getString("prenom");
                    String specialite = row.getString("specialite");
                    String numeroOrdre = row.getString("numeroOrdre");
                    String accessLevels = "STANDARD";
                    if (row.columnNames().contains("accessLevels")) {
                        accessLevels = row.getString("accessLevels");
                    }
                    ProfessionnelSante newPro = new ProfessionnelSante(login, password, "PRO", accessLevels, nom, prenom, specialite, numeroOrdre);
                    if (row.columnNames().contains("horairesDisponibilite")) {
                        String horaires = row.getString("horairesDisponibilite");
                        if (horaires != null) newPro.setHorairesDisponibilite(horaires);
                    }
                    existingPros.add(newPro);
                    newProsCount++;
                }
            } catch (Exception e) {
                System.err.println("Erreur parsing professionnel: " + e.getMessage());
            }
        }
        saveProfessionnels(existingPros);
        return newProsCount;
    }

    public int importConsultations(String importFilePath, List<Consultation> existingConsultations, List<Patient> allPatients, List<ProfessionnelSante> allPros) throws Exception {
        Table importTable = readImportFile(importFilePath);
        int newConsultationsCount = 0;
        for (Row row : importTable) {
            if (!row.columnNames().contains("patientId") || !row.columnNames().contains("professionnelLogin")) {
                throw new Exception("Structure de fichier Consultations invalide.");
            }
            try {
                int idConsultation = row.columnNames().contains("idConsultation") ? row.getInt("idConsultation") : 0;
                String proLogin = row.getString("professionnelLogin");
                int patientId = row.getInt("patientId");
                ProfessionnelSante pro = allPros.stream().filter(p -> p.getLoginID().equals(proLogin)).findFirst().orElse(null);
                Patient patient = allPatients.stream().filter(p -> p.getId() == patientId).findFirst().orElse(null);

                if (pro != null && patient != null) {
                    LocalDateTime date = LocalDateTime.parse(row.getString("dateHeure"));
                    String motif = row.getString("motif");
                    Consultation newConsultation;
                    if (idConsultation > 0) {
                        newConsultation = new Consultation(idConsultation, date, motif, pro, patient);
                    } else {
                        newConsultation = new Consultation(date, motif, pro, patient);
                    }
                    if (row.columnNames().contains("dureeMinutes")) newConsultation.setDureeMinutes(row.getInt("dureeMinutes"));
                    if (row.columnNames().contains("statut")) newConsultation.setStatut(row.getString("statut"));
                    if (row.columnNames().contains("observations")) newConsultation.setObservations(row.getString("observations").replace(";", ","));
                    if (row.columnNames().contains("diagnostic")) newConsultation.setDiagnostic(row.getString("diagnostic").replace(";", ","));
                    
                    existingConsultations.add(newConsultation);
                    pro.ajouterConsultation(newConsultation);
                    patient.getDossierMedical().ajouterConsultation(newConsultation);
                    newConsultationsCount++;
                }
            } catch (Exception e) {
                System.err.println("Erreur parsing consultation: " + e.getMessage());
            }
        }
        saveConsultations(existingConsultations);
        return newConsultationsCount;
    }

    // ==================== PATIENTS ====================

    public void savePatients(List<Patient> patients) {
        try {
            Table table = createPatientTable(patients);
            table.write().csv(getDefaultWriteOptions(PATIENTS_FILE));
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde patients: " + e.getMessage());
        }
    }

    public void savePatientsAs(List<Patient> patients, String filename) {
        try {
            ensureExportDirectoryExists();
            Table table = createPatientTable(patients);
            table.write().csv(getDefaultWriteOptions(EXPORT_DIR + filename));
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde patients: " + e.getMessage());
        }
    }

    public List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) return patients;
        try {
            Table table = Table.read().csv(CsvReadOptions.builder(file).separator(';').header(true).missingValueIndicator("", "null", "NULL").build());
            for (Row row : table) {
                try {
                    int id = row.getInt("id");
                    String nom = row.getString("nom");
                    String prenom = row.getString("prenom");
                    Patient p = new Patient(id, nom, prenom);
                    if (row.columnNames().contains("numeroSecuriteSociale")) p.setNumeroSecuriteSociale(row.getString("numeroSecuriteSociale"));
                    if (row.columnNames().contains("groupeSanguin")) p.setGroupeSanguin(row.getString("groupeSanguin"));
                    patients.add(p);
                } catch (Exception e) { System.err.println("Erreur parsing patient: " + e.getMessage()); }
            }
        } catch (Exception e) { System.err.println("Erreur chargement patients: " + e.getMessage()); }
        return patients;
    }

    // ==================== PROFESSIONNELS ====================

    public void saveProfessionnels(List<ProfessionnelSante> pros) {
        try {
            Table table = createProsTable(pros);
            table.write().csv(getDefaultWriteOptions(PROS_FILE));
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde professionnels: " + e.getMessage());
        }
    }

    public void saveProfessionnelsAs(List<ProfessionnelSante> pros, String filename) {
        try {
            ensureExportDirectoryExists();
            Table table = createProsTable(pros);
            table.write().csv(getDefaultWriteOptions(EXPORT_DIR + filename));
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde professionnels: " + e.getMessage());
        }
    }

    public List<ProfessionnelSante> loadProfessionnels() {
        List<ProfessionnelSante> pros = new ArrayList<>();
        File file = new File(PROS_FILE);
        if (!file.exists()) return pros;
        try {
            Table table = Table.read().csv(CsvReadOptions.builder(file).separator(';').header(true).missingValueIndicator("", "null", "NULL").build());
            for (Row row : table) {
                try {
                    String login = row.getString("login");
                    String password = row.getString("password");
                    String accessLevel = row.getString("accessLevels");
                    String nom = row.getString("nom");
                    String prenom = row.getString("prenom");
                    String specialite = row.getString("specialite");
                    String numeroOrdre = row.getString("numeroOrdre");
                    String accessLevels = "STANDARD";
                    if (row.columnNames().contains("accessLevels")) accessLevels = row.getString("accessLevels");
                    
                    ProfessionnelSante p = new ProfessionnelSante(login, password, "PRO", accessLevels, nom, prenom, specialite, numeroOrdre);
                    if (row.columnNames().contains("horairesDisponibilite")) p.setHorairesDisponibilite(row.getString("horairesDisponibilite"));
                    pros.add(p);
                } catch (Exception e) { System.err.println("Erreur parsing professionnel: " + e.getMessage()); }
            }
        } catch (Exception e) { System.err.println("Erreur chargement professionnels: " + e.getMessage()); }
        return pros;
    }

    // ==================== CONSULTATIONS ====================

    public void saveConsultations(List<Consultation> consultations) {
        try {
            Table table = createConsultationTable(consultations);
            table.write().csv(getDefaultWriteOptions(CONSULTATIONS_FILE));
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde consultations: " + e.getMessage());
        }
    }

    public void saveConsultationsAs(List<Consultation> consultations, String filename) {
        try {
            ensureExportDirectoryExists();
            Table table = createConsultationTable(consultations);
            table.write().csv(getDefaultWriteOptions(EXPORT_DIR + filename));
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde consultations: " + e.getMessage());
        }
    }

    public List<Consultation> loadConsultations(List<Patient> patients, List<ProfessionnelSante> pros) {
        List<Consultation> consultations = new ArrayList<>();
        File file = new File(CONSULTATIONS_FILE);
        if (!file.exists()) return consultations;
        try {
            Table table = Table.read().csv(CsvReadOptions.builder(file).separator(';').header(true).missingValueIndicator("", "null", "NULL").build());
            for (Row row : table) {
                try {
                    int idConsultation = row.columnNames().contains("idConsultation") ? row.getInt("idConsultation") : 0;
                    LocalDateTime date = LocalDateTime.parse(row.getString("dateHeure"));
                    String motif = row.getString("motif");
                    String proLogin = row.getString("professionnelLogin");
                    int patientId = row.getInt("patientId");
                    ProfessionnelSante pro = pros.stream().filter(p -> p.getLoginID().equals(proLogin)).findFirst().orElse(null);
                    Patient patient = patients.stream().filter(p -> p.getId() == patientId).findFirst().orElse(null);
                    if (pro != null && patient != null) {
                        Consultation c;
                        if (idConsultation > 0) {
                            c = new Consultation(idConsultation, date, motif, pro, patient);
                        } else {
                            c = new Consultation(date, motif, pro, patient);
                        }
                        if (row.columnNames().contains("dureeMinutes")) c.setDureeMinutes(row.getInt("dureeMinutes"));
                        if (row.columnNames().contains("statut")) c.setStatut(row.getString("statut"));
                        if (row.columnNames().contains("observations")) c.setObservations(row.getString("observations"));
                        if (row.columnNames().contains("diagnostic")) c.setDiagnostic(row.getString("diagnostic"));
                        consultations.add(c);
                        pro.ajouterConsultation(c);
                        patient.getDossierMedical().ajouterConsultation(c);
                    }
                } catch (Exception e) { System.err.println("Erreur parsing consultation: " + e.getMessage()); }
            }
        } catch (Exception e) { System.err.println("Erreur chargement consultations: " + e.getMessage()); }
        return consultations;
    }

    // ==================== ANTECEDENTS ====================

    public void saveAntecedents(List<Patient> patients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ANTECEDENTS_FILE))) {
            writer.println("patientId;type;description;date;gravite;actif");
            for (Patient p : patients) {
                for (Antecedent a : p.getDossierMedical().getAntecedents()) {
                    writer.printf("%d;%s;%s;%s;%s;%s\n", p.getId(), a.getType(), a.getDescription() != null ? a.getDescription().replace(";", ",") : "", a.getDate(), a.getGravite(), a.isActif());
                }
            }
        } catch (IOException e) { System.err.println("Erreur sauvegarde antécédents: " + e.getMessage()); }
    }

    public void loadAntecedents(List<Patient> patients) {
        File file = new File(ANTECEDENTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    try {
                        int patientId = Integer.parseInt(parts[0]);
                        Patient patient = patients.stream().filter(p -> p.getId() == patientId).findFirst().orElse(null);
                        if (patient != null) {
                            Antecedent ant = new Antecedent(parts[1], parts[2], LocalDate.parse(parts[3]), parts[4], parts.length > 5 ? Boolean.parseBoolean(parts[5]) : true);
                            patient.getDossierMedical().ajouterAntecedent(ant);
                        }
                    } catch (Exception e) { System.err.println("Erreur parsing antécédent: " + e.getMessage()); }
                }
            }
        } catch (Exception e) { System.err.println("Erreur chargement antécédents: " + e.getMessage()); }
    }
}
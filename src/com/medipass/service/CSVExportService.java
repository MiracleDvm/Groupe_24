package com.medipass.service;

import com.medipass.model.*;
import com.medipass.user.*;
import java.io.IOException;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

// Cette classe s'occupe de coordonner l'exportation des données.
public class CSVExportService {

    private final DataService dataService;
    private final PatientService patientService;
    private final AdministrateurService adminService;
    private final ConsultationService consultationService;
    
    // Nouveaux noms de fichiers d'exportation individuels
    private static final String EXPORT_PATIENT_FILE = "exportedPatients.csv";
    private static final String EXPORT_PROS_FILE = "exportedPros.csv";
    private static final String EXPORT_CONSULTATION_FILE = "exportedConsultations.csv";
    private static final String EXPORT_ANTECEDENTS_FILE = "exportedAntecedents.csv";

    public CSVExportService(DataService dataService, PatientService patientService,
                            AdministrateurService adminService, ConsultationService consultationService) {
        this.dataService = dataService;
        this.patientService = patientService;
        this.adminService = adminService;
        this.consultationService = consultationService;
    }

    /**
     * Exporte toutes les données métier vers le dossier "exportedFiles", un fichier par entité.
     * Les fichiers seront nommés exportedPatients.csv, exportedPros.csv, et exportedConsultations.csv.
     */
    public void exportAllData() {
        System.out.println("\n--- Démarrage de l'exportation des données CSV individuelles ---");
        
        try {
            // Appel des nouvelles méthodes de sauvegarde flexibles du DataService
            dataService.savePatientsAs(patientService.getPatients(), EXPORT_PATIENT_FILE);
            dataService.saveProfessionnelsAs(adminService.getProfessionnels(), EXPORT_PROS_FILE);
            dataService.saveConsultationsAs(consultationService.getConsultations(), EXPORT_CONSULTATION_FILE);
            dataService.saveAntecedentsAs(patientService.getPatients(), EXPORT_ANTECEDENTS_FILE);

            System.out.println("✓ Exportation de fichiers individuels terminée dans le répertoire : " + dataService.getExportDir());

        } catch (Exception e) {
            System.err.println("❌ Échec de l'exportation des données: " + e.getMessage());
        }
    }
}
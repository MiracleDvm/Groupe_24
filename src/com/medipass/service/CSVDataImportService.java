package com.medipass.service;

import com.medipass.model.*;
import com.medipass.user.*;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Service pour gérer l'importation de données CSV externes 
 * vers les fichiers de données internes (PATIENTS_FILE, PROS_FILE, etc.).
 */
public class CSVDataImportService {

    private final DataService dataService;
    private final PatientService patientService;
    private final AdministrateurService adminService;
    private final ConsultationService consultationService;

    public CSVDataImportService(DataService dataService, PatientService patientService,
                                AdministrateurService adminService, ConsultationService consultationService) {
        this.dataService = dataService;
        this.patientService = patientService;
        this.adminService = adminService;
        this.consultationService = consultationService;
    }

    // ==================== Fonctions d'Importation pour le menu ====================

    /**
     * Importe de nouveaux patients à partir d'un fichier CSV et met à jour la liste en mémoire 
     * et le fichier patients.csv.
     * @param importFilePath Le chemin du fichier CSV source.
     * @throws Exception Si la lecture ou la sauvegarde échoue.
     */
    public void importPatientsData(String importFilePath) throws Exception {
        System.out.println("\n--- Importation des Patients depuis " + importFilePath + " ---");
        
        // 1. Récupérer la liste des patients en mémoire pour l'enrichir
        List<Patient> patients = patientService.getPatients();
        
        // 2. Déléguer la lecture, l'ajout et la sauvegarde au DataService
        int importedCount = dataService.importPatients(importFilePath, patients);
        
        System.out.println("✅ " + importedCount + " nouveaux patients importés et sauvegardés.");
    }

    /**
     * Importe de nouveaux professionnels à partir d'un fichier CSV et met à jour la liste en mémoire 
     * et le fichier pros.csv.
     * @param importFilePath Le chemin du fichier CSV source.
     * @throws Exception Si la lecture ou la sauvegarde échoue.
     */
    public void importProfessionnelsData(String importFilePath) throws Exception {
        System.out.println("\n--- Importation des Professionnels depuis " + importFilePath + " ---");
        
        // 1. Récupérer la liste des professionnels en mémoire
        List<ProfessionnelSante> pros = adminService.getProfessionnels();
        
        // 2. Déléguer la lecture, l'ajout et la sauvegarde au DataService
        int importedCount = dataService.importProfessionnels(importFilePath, pros);
        
        System.out.println("✅ " + importedCount + " nouveaux professionnels importés et sauvegardés.");
    }

    /**
     * Importe de nouvelles consultations à partir d'un fichier CSV et met à jour la liste en mémoire 
     * et le fichier consultations.csv.
     * @param importFilePath Le chemin du fichier CSV source de l'import.
     * @throws Exception Si la lecture ou la sauvegarde échoue.
     */
    public void importConsultationsData(String importFilePath) throws Exception {
        System.out.println("\n--- Importation des Consultations depuis " + importFilePath + " ---");
        
        // 1. Récupérer toutes les listes nécessaires (Consultations, Patients, Pros)
        List<Consultation> consultations = consultationService.getConsultations();
        List<Patient> patients = patientService.getPatients();
        List<ProfessionnelSante> pros = adminService.getProfessionnels();
        
        // 2. Vérification rapide avant l'importation des consultations
        if (patients.isEmpty() || pros.isEmpty()) {
            System.err.println("❌ IMPOSSIBLE D'IMPORTER: Les listes de Patients ou Professionnels sont vides. Veuillez les importer d'abord.");
            return;
        }

        // 3. Déléguer la lecture, l'ajout et la sauvegarde au DataService
        int importedCount = dataService.importConsultations(importFilePath, consultations, patients, pros);
        
        System.out.println("✅ " + importedCount + " nouvelles consultations importées et liées.");
    }
}
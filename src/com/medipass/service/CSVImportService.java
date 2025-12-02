package com.medipass.service;

import com.medipass.model.*;
import com.medipass.user.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service d'orchestration pour l'importation (chargement) des données CSV 
 * au démarrage du système.
 */
public class CSVImportService {

    private final DataService dataService;

    public CSVImportService(DataService dataService) {
        this.dataService = dataService;
    }

    // ==================== Méthodes de Chargement Granulaire ====================

    /**
     * Charge la liste des patients depuis 'patients.csv'.
     * @return La liste des patients chargés.
     */
    public List<Patient> loadPatients() {
        System.out.print("Chargement des patients... ");
        List<Patient> patients = dataService.loadPatients();
        System.out.println("✓ " + patients.size() + " patients chargés.");
        return patients;
    }

    /**
     * Charge la liste des professionnels depuis 'pros.csv'.
     * @return La liste des professionnels chargés.
     */
    public List<ProfessionnelSante> loadProfessionnels() {
        System.out.print("Chargement des professionnels... ");
        List<ProfessionnelSante> pros = dataService.loadProfessionnels();
        System.out.println("✓ " + pros.size() + " professionnels chargés.");
        return pros;
    }

    /**
     * Charge la liste des consultations depuis 'consultations.csv' et les lie
     * aux patients et professionnels déjà chargés.
     * * @param patients Liste des patients déjà chargés.
     * @param pros Liste des professionnels déjà chargés.
     * @return La liste des consultations chargées et liées.
     */
    public List<Consultation> loadConsultations(List<Patient> patients, List<ProfessionnelSante> pros) {
        // La liaison est essentielle pour les consultations. On vérifie l'existence des listes de base.
        if (patients == null || pros == null || patients.isEmpty() || pros.isEmpty()) {
            System.err.println("⚠️ Impossible de lier les consultations: Les listes de Patients et/ou Professionnels sont incomplètes. Retourne une liste vide.");
            return new ArrayList<>();
        }
        
        System.out.print("Chargement des consultations... ");
        List<Consultation> consultations = dataService.loadConsultations(patients, pros);
        System.out.println("✓ " + consultations.size() + " consultations chargées et liées.");
        return consultations;
    }
    
    // ==================== Méthode de Chargement Global (pour le démarrage) ====================
    
    /**
     * Charge toutes les données (Patients, Pros, Consultations) dans le bon ordre.
     * @return Une liste contenant [Patients, Professionnels, Consultations].
     */
    public List<List<?>> loadAllData() {
        System.out.println("\n--- Démarrage du chargement des données CSV ---");
        
        // 1. Charger les entités de base
        List<Patient> patients = loadPatients();
        List<ProfessionnelSante> professionnels = loadProfessionnels();
        
        // 2. Charger les entités liées (Consultations)
        List<Consultation> consultations = loadConsultations(patients, professionnels);
        
        // Retourne les trois listes dans un ordre fixe pour l'initialisation dans Main
        List<List<?>> allData = new ArrayList<>();
        allData.add(patients);
        allData.add(professionnels);
        allData.add(consultations);

        return allData;
    }
}
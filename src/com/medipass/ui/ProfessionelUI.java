package com.medipass.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.medipass.model.*;
import com.medipass.service.*;
import com.medipass.user.ProfessionnelSante;

/**
 * Interface utilisateur pour les professionnels de santé
 */
public class ProfessionelUI implements MenuInterface {

    private final Scanner sc;
    private final ProfessionnelSante professionnel;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final DataService dataService;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ProfessionelUI(Scanner sc, ProfessionnelSante professionnel, PatientService patientService,
            ConsultationService consultationService, DataService dataService) {
        this.sc = sc;
        this.professionnel = professionnel;
        this.patientService = patientService;
        this.consultationService = consultationService;
        this.dataService = dataService;
    }

    @Override
    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═════════════════════════════════════╗");
            System.out.println("║  MENU PROFESSIONNEL                 ║");
            System.out.println("║  " + professionnel.getNom() + " " + professionnel.getPrenom());
            System.out.println("╠═════════════════════════════════════╣");
            System.out.println("║ 1) Gestion des patients             ║");
            System.out.println("║ 2) Programmer une consultation      ║");
            System.out.println("║ 3) Voir mon planning                ║");
            System.out.println("║ 4) Clôturer une consultation        ║");
            System.out.println("║ 5) Voir antécédents patient         ║");
            System.out.println("║ 0) Se déconnecter                   ║");
            System.out.println("╚═════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" ->
                    menuGestionPatients();
                case "2" ->
                    programmerConsultation();
                case "3" ->
                    afficherPlanning();
                case "4" ->
                    clotureConsultation();
                case "5" ->
                    afficherAntecedentsPatient();
                case "0" ->
                    continuer = false;
                default ->
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    // gestion des patients

        private void menuGestionPatients() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║  GESTION DES PATIENTS              ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1) Créer un patient                ║");
            System.out.println("║ 2) Lister les patients             ║");
            System.out.println("║ 3) Consulter dossier patient       ║");
            System.out.println("║ 4) Modifier patient                ║");
            System.out.println("║ 5) Ajouter antécédent              ║");
            System.out.println("║ 0) Retour                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" ->
                    creerPatient();
                case "2" ->
                    listerPatients();
                case "3" ->
                    consulterDossierPatient();
                case "4" ->
                    modifierPatient();
                case "5" ->
                    ajouterAntecedent();
                case "0" ->
                    continuer = false;
                default ->
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    private void creerPatient() {
        System.out.println("\n--- Création d'un patient ---");
        int id = lireEntier("ID: ");
        String nom = lireChaine("Nom: ");
        String prenom = lireChaine("Prénom: ");
        String sexe = lireChaine("Sexe: ");

        Patient patient = new Patient(id, nom, prenom);
        patient.setNumeroSecuriteSociale(lireChaine("Numéro de Sécurité Sociale: "));
        patient.setGroupeSanguin(lireChaine("Groupe sanguin: "));

        if (patientService.creerPatient(patient)) {
            System.out.println("✓ Patient créé avec succès. Dossier ID: " + patient.getDossierMedical().getIdDossier());
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Erreur lors de la création (ID peut-être déjà utilisé)");
        }
    }

    private void consulterDossierPatient() {
        int id = lireEntier("ID du patient: ");
        System.out.println(patientService.afficherInfoPatient(id));
    }

    private void modifierPatient() {
        int id = lireEntier("ID du patient: ");
        String nom = lireChaine("Nouveau nom (ou vide): ");
        String prenom = lireChaine("Nouveau prénom (ou vide): ");
        String groupe = lireChaine("Nouveau groupe sanguin (ou vide): ");

        if (patientService.modifierPatient(id,
                nom.isEmpty() ? null : nom,
                prenom.isEmpty() ? null : prenom,
                null,
                groupe.isEmpty() ? null : groupe)) {
            System.out.println("✓ Patient modifié");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Patient non trouvé");
        }
    }

    private void ajouterAntecedent() {
        int patientId = lireEntier("ID du patient: ");
        String type = lireChaine("Type d'antécédent (allergie, maladie, intervention, etc.): ");
        String description = lireChaine("Description: ");
        String gravite = lireChaine("Gravité (bénin, modéré, grave): ");

        Antecedent antecedent = new Antecedent(type, description, LocalDate.now(), gravite, true);
        if (patientService.ajouterAntecedentAuPatient(patientId, antecedent)) {
            System.out.println("✓ Antécédent ajouté");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Patient non trouvé");
        }
    }

    private void listerPatients() {
        System.out.println("\n=== LISTE DES PATIENTS ===");
        List<Patient> patients = patientService.getPatients();
        if (patients.isEmpty()) {
            System.out.println("Aucun patient enregistré");
        } else {
            for (Patient p : patients) {
                System.out.printf("[%d] %s %s\n", p.getId(), p.getNom(), p.getPrenom());
            }
        }
    }


    // consultations

    private void programmerConsultation() {
        System.out.println("\n--- Programmation d'une consultation ---");
        int patientId = lireEntier("ID du patient: ");

        Patient patient = patientService.findPatientById(patientId);
        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        LocalDateTime dateHeure = lireDate("Date et heure (YYYY-MM-DD HH:MM): ");
        if (dateHeure == null) {
            return;
        }

        int duree = lireEntier("Durée en minutes (défaut 30): ");
        if (duree <= 0) {
            duree = 30;
        }

        String motif = lireChaine("Motif: ");

        if (consultationService.programmerConsultation(dateHeure, motif, professionnel, patient)) {
            List<Consultation> all = consultationService.getConsultations();
            if (!all.isEmpty()) {
                Consultation created = all.get(all.size() - 1);
                created.setDureeMinutes(duree);
            }
            System.out.println("✓ Consultation programmée");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Impossible de programmer (conflit horaire ou erreur)");
        }
    }

    private void afficherPlanning() {
        System.out.println("\n=== PLANNING DE " + professionnel.getNom() + " " + professionnel.getPrenom() + " ===");
        List<Consultation> planning = professionnel.getPlanning();
        if (planning.isEmpty()) {
            System.out.println("Aucune consultation programmée");
        } else {
            for (Consultation c : planning) {
                System.out.println(c);
            }
        }
    }

    private void clotureConsultation() {
        int id = lireEntier("ID de la consultation: ");
        String observations = lireChaine("Observations: ");
        String diagnostic = lireChaine("Diagnostic: ");

        boolean obsOk = consultationService.ajouterObservations(id, observations);
        boolean diagOk = consultationService.ajouterDiagnostic(id, diagnostic);
        boolean effectueOk = consultationService.marquerEffectuee(id);

        if (obsOk && diagOk && effectueOk) {
            System.out.println("✓ Consultation clôturée");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Consultation non trouvée");
        }
    }

    private void afficherAntecedentsPatient() {
        int patientId = lireEntier("ID du patient: ");
        List<Antecedent> antecedents = patientService.getAntecedentsPatient(patientId);

        System.out.println("\n=== ANTÉCÉDENTS ===");
        if (antecedents.isEmpty()) {
            System.out.println("Aucun antécédent enregistré");
        } else {
            for (Antecedent a : antecedents) {
                System.out.println(a);
            }
        }
    }

    private void sauvegarderDonnees() {
        dataService.savePatients(patientService.getPatients());
        dataService.saveConsultations(consultationService.getConsultations());
        System.out.println("(Données sauvegardées)");
    }

    // --- Méthodes utilitaires pour la saisie sécurisée ---
    private String lireChaine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private int lireEntier(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre entier valide.");
            }
        }
    }

    private LocalDateTime lireDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return LocalDateTime.parse(input, DATE_FORMATTER);
            } catch (Exception e) {
                System.out.println("❌ Format de date invalide. Utilisez 'yyyy-MM-dd HH:mm'");
            }
        }
    }
}

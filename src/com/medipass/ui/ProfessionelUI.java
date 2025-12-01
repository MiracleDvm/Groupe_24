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
    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ProfessionelUI(Scanner sc,
            ProfessionnelSante professionnel,
            PatientService patientService,
            ConsultationService consultationService,
            DataService dataService) {
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
            System.out.println("║ 5) Gestion des antécédents          ║");
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
                    menuPlanning();
                case "4" ->
                    clotureConsultation();
                case "5" ->
                    menuAntecedents();
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
            System.out.println("║ 1) Créer un dossier medical        ║");
            System.out.println("║ 2) Lister les patients             ║");
            System.out.println("║ 3) Consulter dossier patient       ║");
            System.out.println("║ 4) Modifier patient                ║");
            System.out.println("║ 5) Annuler consultation            ║");
            System.out.println("║ 0) Retour                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" ->
                    creerPatient(professionnel.getAccessLevels());
                case "2" ->
                    listerPatients(professionnel.getAccessLevels());
                case "3" ->
                    consulterDossierPatient(professionnel.getAccessLevels());
                case "4" ->
                    modifierPatient(professionnel.getAccessLevels());
                case "5" ->
                    annulerConsultation(professionnel.getAccessLevels());
                case "0" ->
                    continuer = false;
                default ->
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    private void creerPatient(String accessLevels) {
        char accessLevel = '1';
        // debug: System.out.println(accessLevels.indexOf(accessLevel) != -1);
        // debug: System.out.println(accessLevels);
        if (accessLevels.indexOf(accessLevel) != -1) {
            System.out.println("\n--- Création d'un patient ---");
            int id = generatedID();
            String nom = lireChaine("Nom: ");
            String prenom = lireChaine("Prénom: ");

            Patient patient = new Patient(id, nom, prenom);
            // check si le sexe est "M" ou "F" et rien d'autre
            String sexe = lireChaine("Sexe (M ou F): ");
            while (!sexe.trim().toUpperCase().equals("F") && !sexe.trim().toUpperCase().equals("M")) {
                sexe = lireChaine("Sexe (M ou F): ");
            }
            patient.setSexe(sexe.toUpperCase());
            patient.setDateNaissance(lireDate("Date de naissance 'yyyy-MM-dd': "));
            patient.setNumeroSecuriteSociale(lireChaine("Numéro de Sécurité Sociale: "));
            patient.setGroupeSanguin(lireChaine("Groupe sanguin: "));

            if (patientService.creerPatient(patient)) {
                System.out.println(
                        "✓ Patient créé avec succès. Dossier ID: " + patient.getDossierMedical().getIdDossier());
                sauvegarderDonnees();
            } else {
                System.out.println("❌ Erreur lors de la création (ID peut-être déjà utilisé)");
            }
        } else {
            System.out.println("❌ Accès refusé\nVous n'êtes pas habilité(e) à accéder a cette option. ");
        }
    }

    private void consulterDossierPatient(String accessLevels) {
        char accessLevel = '3';
        if (accessLevels.indexOf(accessLevel) != -1) {

            int id = lireEntier("ID du patient: ");
            System.out.println(patientService.afficherInfoPatient(id));
        } else {
            System.out.println("❌ Accès refusé\nVous n'êtes pas habilité(e) à accéder a cette option. ");
        }
    }

    private void modifierPatient(String accessLevels) {
        char accessLevel = '4';
        if (accessLevels.indexOf(accessLevel) != -1) {
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
        } else {
            System.out.println("❌ Accès refusé\nVous n'êtes pas habilité(e) à accéder a cette option. ");
        }
    }

    // private void ajouterAntecedent() {
    //     int patientId = lireEntier("ID du patient: ");
    //     String type = lireChaine("Type d'antécédent (allergie, maladie, intervention, etc.): ");
    //     String description = lireChaine("Description: ");
    //     String gravite = lireChaine("Gravité (bénin, modéré, grave): ");

    //     Antecedent antecedent = new Antecedent(type, description, LocalDate.now(), gravite, true);
    //     if (patientService.ajouterAntecedentAuPatient(patientId, antecedent)) {
    //         System.out.println("✓ Antécédent ajouté");
    //         sauvegarderDonnees();
    //     } else {
    //         System.out.println("❌ Patient non trouvé");
    //     }
    // }

    private void listerPatients(String accessLevels) {
        char accessLevel = '2';
        if (accessLevels.indexOf(accessLevel) != -1) {
            System.out.println("\n=== LISTE DES PATIENTS ===");
            List<Patient> patients = patientService.getPatients();
            if (patients.isEmpty()) {
                System.out.println("Aucun patient enregistré");
            } else {
                for (Patient p : patients) {
                    System.out.printf("[%d] %s %s\n", p.getId(), p.getNom(), p.getPrenom());
                }
            }
        } else {
            System.out.println("❌ Accès refusé\nVous n'êtes pas habilité(e) à accéder a cette option. ");
        }
    }

    // generate un Id en auto en fonction du dernier patient de la liste
    private int generatedID() {
        List<Patient> patients = patientService.getPatients();
        if (patients.isEmpty()) {
            return 1;
        } else {
            return patients.get(patients.size() - 1).getId() + 1;
        }
    }

    // consultations professionnel de sante

    private void programmerConsultation() {
        System.out.println("\n--- Programmation d'une consultation ---");
        int patientId = lireEntier("ID du patient: ");

        Patient patient = patientService.findPatientById(patientId);
        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        LocalDateTime dateHeure = lireDateHeure("Date et heure (YYYY-MM-DD HH:MM): ");
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

    private void annulerConsultation(String accessLevels) {
        char accessLevel = '2';
        if (accessLevels.indexOf(accessLevel) != -1) {
            int id = lireEntier("ID de la consultation à annuler: ");

            Consultation consultation = consultationService.findConsultationById(id);
            if (consultation == null) {
                System.out.println("❌ Consultation non trouvée");
                return;
            }

            if (!consultation.getProfessionnel().getLoginID().equals(professionnel.getLoginID())) {
                System.out.println("❌ Cette consultation ne vous appartient pas");
                return;
            }

            if (consultationService.annulerConsultation(id)) {
                System.out.println("✓ Consultation annulée");
                sauvegarderDonnees();
            } else {
                System.out.println("❌ Erreur lors de l'annulation");
            }
        } else {
            System.out.println("❌ Accès refusé\nVous n'êtes pas habilité(e) à accéder a cette option. ");
        }
    }

    /* ===================== PLANNING ===================== */

    private void menuPlanning() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  PLANNING DE " + professionnel.getNom() + " " + professionnel.getPrenom());
            System.out.println("╠═══════════════════════════════════════╣");
            System.out.println("║ 1) Planning complet                   ║");
            System.out.println("║ 2) Planning du jour                   ║");
            System.out.println("║ 3) Planning de la semaine             ║");
            System.out.println("║ 4) Planning du mois                   ║");
            System.out.println("║ 5) Planning personnalisé              ║");
            System.out.println("║ 0) Retour                             ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> afficherPlanningComplet();
                case "2" -> afficherPlanningJour();
                case "3" -> afficherPlanningSemaine();
                case "4" -> afficherPlanningMois();
                case "5" -> afficherPlanningPersonnalise();
                case "0" -> {
                    continuer = false;
                }
                default -> System.out.println("❌ Choix invalide");
            }
        }
    }

    private void afficherPlanningComplet() {
        System.out.println(
                "\n=== PLANNING COMPLET DE " + professionnel.getNom() + " " + professionnel.getPrenom() + " ===");
        List<Consultation> planning = professionnel.getPlanning();
        if (planning.isEmpty()) {
            System.out.println("Aucune consultation programmée");
        } else {
            planning.stream()
                    .filter(c -> !"annulée".equalsIgnoreCase(c.getStatut()))
                    .sorted((c1, c2) -> c1.getDateHeure().compareTo(c2.getDateHeure()))
                    .forEach(c -> System.out.println(c));
        }
    }

    private void afficherPlanningJour() {
        System.out.print("Date (YYYY-MM-DD) [Entrée pour aujourd'hui]: ");
        String input = sc.nextLine().trim();
        LocalDate date = input.isEmpty() ? LocalDate.now() : parseDate(input);

        if (date != null) {
            System.out.println(consultationService.afficherPlanningJour(professionnel, date));
        }
    }

    private void afficherPlanningSemaine() {
        System.out.print("Date de début de semaine (YYYY-MM-DD) [Entrée pour cette semaine]: ");
        String input = sc.nextLine().trim();
        LocalDate dateDebut = input.isEmpty() ? LocalDate.now().with(java.time.DayOfWeek.MONDAY) : parseDate(input);

        if (dateDebut != null) {
            System.out.println(consultationService.afficherPlanningSemaine(professionnel, dateDebut));
        }
    }

    private void afficherPlanningMois() {
        System.out.print("Année [Entrée pour année actuelle]: ");
        String anneeStr = sc.nextLine().trim();
        int annee = anneeStr.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(anneeStr);

        System.out.print("Mois (1-12) [Entrée pour mois actuel]: ");
        String moisStr = sc.nextLine().trim();
        int mois = moisStr.isEmpty() ? LocalDate.now().getMonthValue() : Integer.parseInt(moisStr);

        System.out.println(consultationService.afficherPlanningMois(professionnel, annee, mois));
    }

    private void afficherPlanningPersonnalise() {
        System.out.print("Date de début (YYYY-MM-DD): ");
        LocalDate debut = parseDate(sc.nextLine().trim());
        System.out.print("Date de fin (YYYY-MM-DD): ");
        LocalDate fin = parseDate(sc.nextLine().trim());

        if (debut != null && fin != null) {
            System.out.println(consultationService.afficherPlanningPeriode(
                    professionnel, debut.atStartOfDay(), fin.atTime(23, 59)));
        }
    }

    /* ===================== ANTÉCÉDENTS (NOUVEAU) ===================== */

    private void menuAntecedents() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║  GESTION DES ANTÉCÉDENTS              ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ 1) Voir antécédents d'un patient      ║");
        System.out.println("║ 2) Ajouter un antécédent              ║");
        System.out.println("║ 3) Voir historique complet patient    ║");
        System.out.println("║ 0) Retour                             ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Votre choix: ");
        String choix = sc.nextLine().trim();

        switch (choix) {
            case "1" -> afficherAntecedentsPatient();
            case "2" -> ajouterAntecedent();
            case "3" -> afficherHistoriqueComplet();
            case "0" -> {
            }
            default -> System.out.println("❌ Choix invalide");
        }
    }

    private void afficherAntecedentsPatient() {
        int patientId = lireEntier("ID du patient: ");
        Patient patient = patientService.findPatientById(patientId);

        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        List<Antecedent> antecedents = patientService.getAntecedentsPatient(patientId);

        System.out.println("\n=== ANTÉCÉDENTS DE " + patient.getNom() + " " + patient.getPrenom() + " ===");
        if (antecedents.isEmpty()) {
            System.out.println("Aucun antécédent enregistré");
        } else {
            for (Antecedent a : antecedents) {
                System.out.println(a);
            }
        }
    }

    private void ajouterAntecedent() {
        System.out.println("\n--- Ajout d'un antécédent ---");
        int patientId = lireEntier("ID du patient: ");

        Patient patient = patientService.findPatientById(patientId);
        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        System.out.println("Patient : " + patient.getNom() + " " + patient.getPrenom());
        System.out.println("\nTypes d'antécédents :");
        System.out.println("  - allergie (médicaments, aliments, etc.)");
        System.out.println("  - maladie chronique (diabète, hypertension, etc.)");
        System.out.println("  - intervention chirurgicale");
        System.out.println("  - antécédents familiaux");
        System.out.println("  - traitement en cours");
        System.out.println("  - autre\n");

        String type = lireChaine("Type d'antécédent: ");
        String description = lireChaine("Description détaillée: ");

        System.out.println("\nDate de survenue/diagnostic");
        System.out.print("Date (YYYY-MM-DD) [Entrée pour aujourd'hui]: ");
        String dateStr = sc.nextLine().trim();
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : parseDate(dateStr);

        if (date == null) {
            System.out.println("❌ Date invalide, utilisation de la date du jour");
            date = LocalDate.now();
        }

        System.out.println("\nNiveau de gravité :");
        System.out.println("  1) Bénin");
        System.out.println("  2) Modéré");
        System.out.println("  3) Grave");
        System.out.print("Votre choix (1-3): ");
        String graviteChoice = sc.nextLine().trim();

        String gravite;
        switch (graviteChoice) {
            case "1" -> gravite = "bénin";
            case "2" -> gravite = "modéré";
            case "3" -> gravite = "grave";
            default -> {
                System.out.println("⚠️ Choix invalide, 'modéré' par défaut");
                gravite = "modéré";
            }
        }

        System.out.print("\nL'antécédent est-il toujours actif/pertinent ? (o/n) [o]: ");
        String actifStr = sc.nextLine().trim().toLowerCase();
        boolean actif = actifStr.isEmpty() || actifStr.equals("o") || actifStr.equals("oui");

        Antecedent antecedent = new Antecedent(type, description, date, gravite, actif);

        if (patientService.ajouterAntecedentAuPatient(patientId, antecedent)) {
            System.out.println("\n✓ Antécédent ajouté avec succès");
            System.out.println("  Type: " + type);
            System.out.println("  Description: " + description);
            System.out.println("  Date: " + date);
            System.out.println("  Gravité: " + gravite);
            System.out.println("  Statut: " + (actif ? "actif" : "inactif"));
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Erreur lors de l'ajout de l'antécédent");
        }
    }

    private void afficherHistoriqueComplet() {
        int patientId = lireEntier("ID du patient: ");
        Patient patient = patientService.findPatientById(patientId);

        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  DOSSIER MÉDICAL COMPLET                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");

        // Informations du patient
        System.out.println("\n=== PATIENT ===");
        System.out.println("Nom: " + patient.getNom() + " " + patient.getPrenom());
        System.out.println("ID: " + patient.getId());
        System.out.println("Numéro SS: " + patient.getNumeroSecuriteSociale());
        System.out.println("Groupe sanguin: " + patient.getGroupeSanguin());

        // Antécédents
        System.out.println("\n=== ANTÉCÉDENTS ===");
        List<Antecedent> antecedents = patient.getDossierMedical().getAntecedents();
        if (antecedents.isEmpty()) {
            System.out.println("Aucun antécédent enregistré");
        } else {
            for (Antecedent a : antecedents) {
                System.out.println(a);
            }
        }

        // Consultations
        System.out.println("\n=== CONSULTATIONS ===");
        List<Consultation> consultations = patient.getDossierMedical().getConsultations();
        if (consultations.isEmpty()) {
            System.out.println("Aucune consultation enregistrée");
        } else {
            for (Consultation c : consultations) {
                System.out.println("\n" + c);
                if (c.getObservations() != null && !c.getObservations().isEmpty()) {
                    System.out.println("  Observations: " + c.getObservations());
                }
                if (c.getDiagnostic() != null && !c.getDiagnostic().isEmpty()) {
                    System.out.println("  Diagnostic: " + c.getDiagnostic());
                }
            }
        }
    }

    /* ===================== SAUVEGARDE ===================== */

    private void sauvegarderDonnees() {
        dataService.savePatients(patientService.getPatients());
        dataService.saveConsultations(consultationService.getConsultations());
        dataService.saveAntecedents(patientService.getPatients());
        System.out.println("(Données sauvegardées)");
    }

    /* ===================== UTILITAIRES ===================== */

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

    private LocalDateTime lireDateHeure(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return LocalDateTime.parse(input, DATETIME_FORMATTER);
            } catch (Exception e) {
                System.out.println("❌ Format de date invalide. Utilisez 'yyyy-MM-dd HH:mm'");
                return null;
            }
        }
    }

    private LocalDate lireDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (Exception e) {
                System.out.println("❌ Format de date invalide. Utilisez 'yyyy-MM-dd'");
            }
        }
    }

    private LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input);
        } catch (Exception e) {
            System.out.println("❌ Format invalide (utilisez YYYY-MM-DD)");
            return null;
        }
    }

}

package com.medipass.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.medipass.model.*;
import com.medipass.service.*;
import com.medipass.user.Administrateur;

/**
 * Interface utilisateur pour les administrateurs
 * L'administrateur ne peut PAS accéder aux données médicales (antécédents,
 * diagnostics)
 */
public class AdminUI implements MenuInterface {

    private final Scanner sc;
    private final Administrateur admin;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final AdministrateurService adminService;
    private final StatistiquesService statsService;
    private final DataService dataService;
    private final CSVDataImportService importService;
    private final CSVExportService exportService;

    public AdminUI(Scanner sc,
            Administrateur admin,
            PatientService patientService,
            ConsultationService consultationService,
            AdministrateurService adminService,
            StatistiquesService statsService,
            DataService dataService,
            CSVDataImportService importService,
            CSVExportService exportService) {
        this.sc = sc;
        this.admin = admin;
        this.patientService = patientService;
        this.consultationService = consultationService;
        this.adminService = adminService;
        this.statsService = statsService;
        this.dataService = dataService;
        this.importService = importService;
        this.exportService = exportService;
    }

    @Override
    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═══════════════════════════════════╗");
            System.out.println("║  MENU ADMINISTRATEUR              ║");
            System.out.println("╠═══════════════════════════════════╣");
            System.out.println("║ 1) Gestion des utilisateurs       ║");
            System.out.println("║ 2) Statistiques du système        ║");
            System.out.println("║ 3) Sauvegarder les données        ║");
            System.out.println("║ 4) Importer des données           ║");
            System.out.println("║ 5) Exporter des données           ║");
            System.out.println("║ 0) Se déconnecter                 ║");
            System.out.println("╚═══════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" ->
                    menuGestionUtilisateurs();
                case "2" ->
                    afficherStatistiques();
                case "3" ->
                    sauvegarderDonnees();
                case "4" ->
                    menuImporterDonnees();
                case "5" ->
                    menuExporterDonnees();
                case "0" ->
                    continuer = false;
                default ->
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    /* ===================== UTILISATEURS ===================== */

    private void menuGestionUtilisateurs() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║  GESTION DES UTILISATEURS          ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1) Lister les professionnels       ║");
            System.out.println("║ 2) Afficher un utilisateur         ║");
            System.out.println("║ 3) Modifier contact utilisateur    ║");
            System.out.println("║ 4) Gérer droits d'accès            ║");
            System.out.println("║ 5) Activer/Désactiver compte       ║");
            System.out.println("║ 6) Créer un professionnel          ║");
            System.out.println("║ 7) Supprimer un utilisateur        ║");
            System.out.println("║ 0) Retour                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" ->
                    System.out.println(adminService.afficherProfessionnels());
                case "2" ->
                    afficherUtilisateur();
                case "3" ->
                    modifierContactUtilisateur();
                case "4" ->
                    gererDroitsAcces();
                case "5" ->
                    activerDesactiverCompte();
                case "6" ->
                    creerProfessionnel();
                case "7" ->
                    supprimerUtilisateur();
                case "0" ->
                    continuer = false;
                default ->
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    private void afficherUtilisateur() {
        String login = lireChaine("Login de l'utilisateur: ");
        System.out.println(adminService.afficherUtilisateur(login));
    }

    private void modifierContactUtilisateur() {
        String login = lireChaine("Login de l'utilisateur: ");
        String email = lireChaine("Nouvel email (ou vide pour ne pas changer): ");
        String telephone = lireChaine("Nouveau téléphone (ou vide pour ne pas changer): ");

        boolean success = adminService.modifierContactUtilisateur(
                login,
                email.isEmpty() ? null : email,
                telephone.isEmpty() ? null : telephone);

        if (success) {
            System.out.println("✓ Contact modifié avec succès");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Utilisateur non trouvé");
        }
    }

    private void gererDroitsAcces() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║           DROITS D'ACCES                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ Le droit d'accès est une chaîne de minimum '1' caractère ║");
        System.out.println("║ et de maximum '5' caractères qui spécifie l'accès à un   ║");
        System.out.println("║ ou plusieurs menus utilisateur. Si un droit d'accès est  ║");
        System.out.println("║ '143', il donne acces aux options 1,4 et 3 du menu utili-║");
        System.out.println("║ sateur, et ainsi de suite .                              ║");
        System.out.println("║ Options disponibles:                                     ║");
        System.out.println("║ 1) Gestion des patients                                  ║");
        System.out.println("║ 2) Programmer une consultation                           ║");
        System.out.println("║ 3) Voir mon planning                                     ║");
        System.out.println("║ 4) Clôturer une consultation                             ║");
        System.out.println("║ 5) Gestion des antécédents                               ║");
        System.out.println("║                                                          ║");
        System.out.println("║ NB: Ne pas entrer un droit d'accès superieur a '5'       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        String login = lireChaine("Login de l'utilisateur: ");
        String accessLevel = lireChaine("Entrez les droits d'accès: ");

        if (accessLevel.length() > 0 && accessLevel.length() <= 5) {
            boolean success = adminService.modifierDroitAcces(login, accessLevel);
            if (success) {
                System.out.println("✓ Droit d'acces modifié avec succès");
                sauvegarderDonnees();
            } else {
                System.out.println("❌ Operation echouée");
            }
        } else {
            System.out.println("❌ Operation echouée, veuillez entrer un droit d'accès valide.");

        }
    }

    private void activerDesactiverCompte() {
        String login = lireChaine("Login de l'utilisateur: ");
        String action = lireChaine("1) Activer compte utilisateur\n0) Désactiver compte utilisateur\nAction:")
                .toLowerCase();

        boolean success = false;
        if ("1".equals(action)) {
            success = adminService.activerCompte(login);
        } else if ("0".equals(action)) {
            success = adminService.desactiverCompte(login);
        }

        if (success) {
            System.out.println("✓ Compte " + (action.equals("0") ? "désactivé" : "activé") + " avec succès");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Opération échouée");
        }
    }

    private void afficherStatistiques() {
        System.out.println(statsService.afficherStatistiques(
                patientService.getNombrePatients(),
                adminService.getNombreProfessionnels(),
                consultationService.getNombreConsultations(),
                consultationService.getConsultations(),
                adminService.getProfessionnels()));
    }

    private void afficherConsultationsParPeriode() {
        System.out.print("Date de début (YYYY-MM-DD): ");
        LocalDate debut = parseDate(sc.nextLine().trim());
        System.out.print("Date de fin (YYYY-MM-DD): ");
        LocalDate fin = parseDate(sc.nextLine().trim());

        if (debut == null || fin == null) {
            System.out.println("❌ Dates invalides");
            return;
        }

        List<Consultation> consultationsPeriode = consultationService.getConsultationsParPeriode(
                debut.atStartOfDay(), fin.atTime(23, 59));

        System.out.println("\n=== CONSULTATIONS DU " + debut + " AU " + fin + " ===");
        System.out.println("Nombre total : " + consultationsPeriode.size());

        Map<String, Long> parStatut = consultationsPeriode.stream()
                .collect(Collectors.groupingBy(Consultation::getStatut, Collectors.counting()));

        System.out.println("\nPar statut :");
        parStatut.forEach((statut, count) -> System.out.println("  - " + statut + " : " + count));

        Map<String, Long> parPro = consultationsPeriode.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getProfessionnel().getNom() + " " + c.getProfessionnel().getPrenom(),
                        Collectors.counting()));

        System.out.println("\nPar professionnel :");
        parPro.forEach((pro, count) -> System.out.println("  - " + pro + " : " + count));
    }

    private void afficherPlanningProfessionnel() {
        String login = lireChaine("Login du professionnel: ");
        com.medipass.user.ProfessionnelSante pro = adminService.findProfessionnel(login);

        if (pro == null) {
            System.out.println("❌ Professionnel non trouvé");
            return;
        }

        System.out.print("Date de début (YYYY-MM-DD) [Entrée pour cette semaine]: ");
        String input = sc.nextLine().trim();
        LocalDate debut = input.isEmpty()
                ? LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                : parseDate(input);

        if (debut != null) {
            System.out.println(consultationService.afficherPlanningSemaine(pro, debut));
        }
    }

    /* ===================== COMPTES & SAUVEGARDE ===================== */

    private void creerProfessionnel() {
        System.out.println("\n--- Création d'un professionnel de santé ---");
        String login = lireChaine("Login: ");
        String mdp = lireChaine("Mot de passe: ");
        String nom = lireChaine("Nom: ");
        String prenom = lireChaine("Prénom: ");
        String specialite = lireChaine("Spécialité: ");
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║           DROITS D'ACCES                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ Le droit d'accès est une chaine de minimum '1' caractère ║");
        System.out.println("║ et de maximum '5' caractères qui spécifie l'accès à un   ║");
        System.out.println("║ ou plusieurs menus utilisateur. Si un droit d'accès est  ║");
        System.out.println("║ '143', il donne acces aux options 1,4 et 3 du menu utili-║");
        System.out.println("║ sateur, et ainsi de suite .                              ║");
        System.out.println("║ Options disponibles:                                     ║");
        System.out.println("║ 1) Gestion des patients                                  ║");
        System.out.println("║ 2) Programmer une consultation                           ║");
        System.out.println("║ 3) Voir mon planning                                     ║");
        System.out.println("║ 4) Clôturer une consultation                             ║");
        System.out.println("║ 5) Gestion des antécédents                               ║");
        System.out.println("║                                                          ║");
        System.out.println("║ NB: Ne pas entrer un droit d'accès superieur a '5'       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        String accessLevels = lireChaine("Droits d'accès: ");

        com.medipass.user.ProfessionnelSante pro = new com.medipass.user.ProfessionnelSante(
                login, mdp, "PRO", accessLevels, nom, prenom, specialite, "NUM" + System.currentTimeMillis() % 10000);

        if (adminService.creerCompte(pro)) {
            System.out.println("✓ Professionnel créé. Vous pouvez maintenant vous connecter.");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Login déjà existant");
        }
    }

    private void supprimerUtilisateur() {
        System.out.println("\n--- Suppression d'un utilisateur ---");
        String login = lireChaine("Login de l'utilisateur à supprimer: ");

        // Vérification que l'utilisateur existe avant de demander le mot de passe
        if (adminService.findUtilisateur(login) == null) {
            System.out.println("❌ Utilisateur introuvable.");
            return;
        }

        if (login.equals(admin.getLoginID())) {
            System.out.println("❌ Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }

        System.out.println("⚠️  ATTENTION : Cette action est irréversible !");
        String password = lireChaine("Confirmez avec votre mot de passe administrateur: ");

        if (admin.seConnecter(admin.getLoginID(), password)) {
            if (adminService.supprimerCompte(login)) {
                System.out.println("✓ Utilisateur supprimé avec succès.");
                sauvegarderDonnees();
            } else {
                System.out.println("❌ Erreur lors de la suppression.");
            }
        } else {
            System.out.println("❌ Mot de passe incorrect. Suppression annulée.");
        }
    }

    private void sauvegarderDonnees() {
        dataService.savePatients(patientService.getPatients());
        dataService.saveProfessionnels(adminService.getProfessionnels());
        dataService.saveConsultations(consultationService.getConsultations());
        //dataService.saveAntecedents(patientService.getPatients());
        System.out.println("(Données sauvegardées)");
    }

    private void menuImporterDonnees() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║  IMPORTATION DE DONNÉES            ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1) Importer Patients (CSV)         ║");
            System.out.println("║ 2) Importer Professionnels (CSV)   ║");
            System.out.println("║ 3) Importer Consultations (CSV)    ║");
            System.out.println("║ 0) Retour                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> {
                        String path = lireChaine("Chemin du fichier CSV Patients: ");
                        importService.importPatientsData(path);
                    }
                    case "2" -> {
                        String path = lireChaine("Chemin du fichier CSV Professionnels: ");
                        importService.importProfessionnelsData(path);
                    }
                    case "3" -> {
                        String path = lireChaine("Chemin du fichier CSV Consultations: ");
                        importService.importConsultationsData(path);
                    }
                    case "0" -> continuer = false;
                    default -> System.out.println("❌ Choix invalide");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de l'importation: " + e.getMessage());
            }
        }
    }

    private void menuExporterDonnees() {
        System.out.println("\n--- Exportation des données ---");
        System.out.println("Cela va générer des fichiers CSV dans le dossier 'exportedFiles/'.");
        String confirmation = lireChaine("Confirmer l'exportation ? (O / N): ");
        
        if (confirmation.equalsIgnoreCase("O")) {
            exportService.exportAllData();
        } else {
            System.out.println("Exportation annulée.");
        }
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

    private LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input);
        } catch (Exception e) {
            System.out.println("❌ Format invalide (utilisez YYYY-MM-DD)");
            return null;
        }
    }
}

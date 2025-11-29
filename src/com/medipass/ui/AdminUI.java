package com.medipass.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.medipass.model.*;
import com.medipass.service.*;
import com.medipass.user.Administrateur;

/**
 * Interface utilisateur pour les administrateurs
 */
public class AdminUI implements MenuInterface {

    private final Scanner sc;
    private final Administrateur admin;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final AdministrateurService adminService;
    private final StatistiquesService statsService;
    private final DataService dataService;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AdminUI(Scanner sc, Administrateur admin, PatientService patientService,
            ConsultationService consultationService, AdministrateurService adminService,
            StatistiquesService statsService, DataService dataService) {
        this.sc = sc;
        this.admin = admin;
        this.patientService = patientService;
        this.consultationService = consultationService;
        this.adminService = adminService;
        this.statsService = statsService;
        this.dataService = dataService;
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
                case "0" ->
                    continuer = false;
                default ->
                    System.out.println("❌ Choix invalide");
            }
        }
    }

    private void menuGestionUtilisateurs() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║  GESTION DES UTILISATEURS          ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1) Lister les professionnels       ║");
            System.out.println("║ 2) Afficher un utilisateur         ║");
            System.out.println("║ 3) Modifier contact utilisateur    ║");
            System.out.println("║ 4) Activer/Désactiver compte       ║");
            System.out.println("║ 5) Créer un professionnel          ║");
            System.out.println("║ 6) Supprimer un utilisateur        ║");
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
                    activerDesactiverCompte();
                case "5" ->
                    creerProfessionnel();
                case "6" ->
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

        boolean success = adminService.modifierContactUtilisateur(login,
                email.isEmpty() ? null : email,
                telephone.isEmpty() ? null : telephone);

        if (success) {
            System.out.println("✓ Contact modifié avec succès");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Utilisateur non trouvé");
        }
    }

    private void activerDesactiverCompte() {
        String login = lireChaine("Login de l'utilisateur: ");
        String action = lireChaine("1) Activer compte utilisateur\n0) Désactiver compte utilisateur\nAction:").toLowerCase();

        boolean success = false;
        if ("1".equals(action)) {
            success = adminService.activerCompte(login);
        } else if ("0".equals(action)) {
            success = adminService.desactiverCompte(login);
        }

        if (success) {
            System.out.println("✓ Compte " + (action.equals("0")? "désactivé":"activé") + " avec succès");
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
                adminService.getProfessionnels()
        ));
    }

    private void creerProfessionnel() {
        System.out.println("\n--- Création d'un professionnel de santé ---");
        String login = lireChaine("Login: ");
        String mdp = lireChaine("Mot de passe: ");
        String nom = lireChaine("Nom: ");
        String prenom = lireChaine("Prénom: ");
        String specialite = lireChaine("Spécialité: ");

        com.medipass.user.ProfessionnelSante pro = new com.medipass.user.ProfessionnelSante(
                login, mdp, "PRO", nom, prenom, specialite, "NUM" + System.currentTimeMillis() % 10000);

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

        // Empêcher l'admin de se supprimer lui-même (sécurité basique)
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
}

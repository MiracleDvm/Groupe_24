package com.medipass.app;

import java.io.Console;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.io.Console;
import java.util.Arrays;

import com.medipass.model.*;
import com.medipass.security.AuthentificationService;
import com.medipass.service.*;
import com.medipass.ui.*;
import com.medipass.user.*;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Services
    private static final AuthentificationService auth = new AuthentificationService();
    private static final PatientService patientService = new PatientService();
    private static final ConsultationService consultationService = new ConsultationService();
    private static final AdministrateurService adminService = new AdministrateurService();
    private static final StatistiquesService statsService = new StatistiquesService();
    private static final DataService dataService = new DataService();

    public static void main(String[] args) {
        initializationSysteme();

        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║         BIENVENUE À MEDIPASS          ║");
        System.out.println("║   Système d'Information Médical       ║");
        System.out.println("╚═══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            afficherMenuPrincipal();
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> handleAuthentification();
                case "0" -> {
                    sauvegarderDonnees();
                    System.out.println("Au revoir!");
                    running = false;
                }
                default -> System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
        }
        sc.close();
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║     MENU PRINCIPAL                    ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ 1) Se connecter                       ║");
        System.out.println("║ 0) Quitter                            ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Votre choix: ");
    }

    private static void handleAuthentification() {
        String login = lireChaine("\nLogin: ");
        String mdp = lirePasswordCache("Mot de passe");
        // String mdp = lireChaine("Mot de passe: ");

        if (auth.login(login, mdp)) {
            Utilisateur u = auth.getCurrentUser();
            System.out.println("\n✓ Connexion réussie! Bienvenue "
                    + u.getNom() + " " + u.getPrenom());

            switch (u) {
                case ProfessionnelSante pro -> {
                    MenuInterface menu = new ProfessionelUI(
                            sc, pro, patientService, consultationService, dataService);
                    menu.afficherMenu();
                }
                case Administrateur admin -> {
                    MenuInterface menu = new AdminUI(
                            sc, admin, patientService, consultationService,
                            adminService, statsService, dataService);
                    menu.afficherMenu();
                }
                default -> System.out.println("Menu non disponible pour ce rôle.");
            }
            auth.logout();
        } else {
            System.out.println("❌ Identifiants incorrects ou compte inactif.");
        }
    }

    private static void initializationSysteme() {
        // Charger patients et professionnels
        List<Patient> patients = dataService.loadPatients();
        List<ProfessionnelSante> pros = dataService.loadProfessionnels();

        // Créer l’admin par défaut
        Administrateur admin = new Administrateur("admin", "admin");
        auth.register(admin);
        adminService.creerCompte(admin);

        if (!patients.isEmpty() || !pros.isEmpty()) {
            System.out.println("Chargement des données...");

            // Patients
            for (Patient p : patients) {
                patientService.creerPatient(p);
            }

            // Professionnels
            for (ProfessionnelSante p : pros) {
                adminService.creerCompte(p);
                auth.register(p);
            }

            // Consultations
            List<Consultation> consultations = dataService.loadConsultations(
                    patientService.getPatients(),
                    adminService.getProfessionnels()
            );
            for (Consultation c : consultations) {
                consultationService.ajouterConsultationExistante(c);
            }

            // Antécédents
            //dataService.loadAntecedents(patientService.getPatients());

            System.out.println("✓ Données chargées: "
                    + patients.size() + " patients, "
                    + pros.size() + " professionnels, "
                    + consultations.size() + " consultations.");
        } else {
            System.out.println("ℹ Système initialisé. Aucune donnée sauvegardée.");
            sauvegarderDonnees();
        }
    }

    private static void sauvegarderDonnees() {
        dataService.savePatients(patientService.getPatients());
        dataService.saveProfessionnels(adminService.getProfessionnels());
        dataService.saveConsultations(consultationService.getConsultations());
        //dataService.saveAntecedents(patientService.getPatients());
        System.out.println("(Données sauvegardées)");
    }

    // ---------- Méthodes utilitaires de saisie ----------

    private static String lireChaine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int lireEntier(String prompt) {
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

    private static LocalDateTime lireDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return LocalDateTime.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "❌ Format de date invalide. Utilisez 'yyyy-MM-dd HH:mm' (ex: 2023-12-25 14:30)");
            }
        }
    }
    private static String lirePasswordCache(String prompt) {
        // 1. Tente d'obtenir une instance de Console
        Console console = System.console();
        
        if (console == null) {
            // Cas 1 : Exécution depuis un IDE ou sans véritable terminal
            System.out.print(prompt + " (Avertissement: Entrée visible) : ");
            
            // Revenir à une saisie standard si la console n'est pas disponible
            // Utiliser le Scanner partagé 'sc' pour éviter de créer et ne pas fermer un nouveau Scanner.
            return sc.nextLine();
        }

        // Cas 2 : Console réelle disponible (Masquage fonctionnel)
        char[] passwordChars = null;
        String password = null;

        try {
            // 2. Utiliser readPassword() pour la saisie masquée
            // L'entrée est retournée sous forme de tableau de caractères (char[]) pour plus de sécurité.
            passwordChars = console.readPassword(prompt + " : ");
            
            // 3. Convertir le tableau de caractères en String pour un usage temporaire
            if (passwordChars != null) {
                password = new String(passwordChars);
            }
        } finally {
            // 4. IMPORTANT : Effacer le tableau de caractères de la mémoire
            if (passwordChars != null) {
                Arrays.fill(passwordChars, ' ');
            }
        }

        return password;
    }
}

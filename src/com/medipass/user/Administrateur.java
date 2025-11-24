package com.medipass.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Administrateur : gestion basique des comptes (en mémoire).
 */
public class Administrateur extends Utilisateur {
    private final Map<String, Utilisateur> comptes = new HashMap<>();

    public Administrateur(String loginID, String mdp) {
        super(loginID, mdp, "ADMIN");
    }

    public boolean creerCompte(Utilisateur u){
        if(comptes.containsKey(u.getLoginID())) return false;
        comptes.put(u.getLoginID(), u);
        return true;
    }

    public boolean supprimerCompte(String login){
        return comptes.remove(login) != null;
    }

    public boolean modifierDroits(String login, String nouveauRole){
        Utilisateur u = comptes.get(login);
        if(u==null) return false;
        u.role = nouveauRole;
        return true;
    }

    public Map<String,Utilisateur> getComptes(){ return comptes; }
}

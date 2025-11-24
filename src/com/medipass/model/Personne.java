package com.medipass.models;

public abstract class Personne {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String dateNaissance;
    protected char sexe;
    protected String adresse;
    protected String telephone;
    protected String email;
    
    public Personne() {
        this.id = 0;
        this.sexe = 'M';
    }
    
    // Getters
    public int getId() {
        return id; 
    }
    public String getNom() { 
        return nom; 
    }
    public String getPrenom() { 
        return prenom;
    }
    public String getDateNaissance() { 
        return dateNaissance; 
    }
    public char getSexe() { 
        return sexe; 
    }
    public String getAdresse() { 
        return adresse; 
    }
    public String getTelephone() { 
        return telephone; 
    }
    public String getEmail() { 
        return email; 
    }
    
    // Setters
    public void setNom(String n) { 
        this.nom = n; 
    }
    public void setPrenom(String p) {
        this.prenom = p;
    }
    public void setDateNaissance(String date) { 
        this.dateNaissance = date; 
    }
    
    public void setSexe(char s) {
        if (s == 'M' || s == 'F' || s == 'm' || s == 'f') {
            this.sexe = Character.toUpperCase(s);
        }
    }
    
    public void setAdresse(String addr) { 
        this.adresse = addr; 
    }
    public void setTelephone(String tel) { 
        this.telephone = tel; 
    }
    public void setEmail(String mail) { 
        this.email = mail; 
    }
    
    public int getAge() {
        // Il faudra calculer l'âge réel
        return 0;
    }
}

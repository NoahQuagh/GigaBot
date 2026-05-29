package botdiscord.gigabot.utils.DB.structure;

import java.sql.Timestamp;

public class Rappel {
    private int equipe_id;
    private Timestamp date_match;
    private String joueur;

    public Rappel(int equipe_id, Timestamp date_match, String joueur) {
        this.equipe_id = equipe_id;
        this.date_match = date_match;
        this.joueur = joueur;
    }

    public int getEquipe_id() {
        return equipe_id;
    }

    public void setEquipe_id(int equipe_id) {
        this.equipe_id = equipe_id;
    }

    public Timestamp getDate_match() {
        return date_match;
    }

    public void setDate_match(Timestamp date_match) {
        this.date_match = date_match;
    }

    public String getJoueur() {
        return joueur;
    }

    public void setJoueur(String cree_par) {
        this.joueur = cree_par;
    }
}

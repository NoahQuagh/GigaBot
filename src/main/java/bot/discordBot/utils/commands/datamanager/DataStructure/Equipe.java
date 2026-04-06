package bot.discordBot.utils.commands.datamanager.DataStructure;

import bot.discordBot.utils.commands.datamanager.DataManager;

import java.util.ArrayList;

public class Equipe {
    private String equipeId;
    private String chefId;
    private String chefAdjId;
    private ArrayList<String> joueurIds;

    public Equipe(String equipeId, String chefId, ArrayList<String> joueurIds) {
        this.equipeId = equipeId;
        this.chefId = chefId;
        this.chefAdjId = "";
        this.joueurIds = joueurIds;
    }

    public String getEquipeId() {
        return equipeId;
    }

    public String getChefId() {
        return chefId;
    }

    public ArrayList<String> getJoueurIds() {
        return joueurIds;
    }

    public String getChefAdjId() {
        return chefAdjId;
    }

    public static boolean nomEquipteUtiliser(String nomteam){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for (Equipe equipe : equipes) {
            if (equipe.getEquipeId().equals(nomteam)) return true;
        }
        return false;
    }

    public static boolean estDejaCapitaineDuneEquipe(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return true;
        }
        return false;
    }

    public static boolean CapitaineNaPasDEquipe(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return true;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return false;
        }
        return true;
    }

    public static boolean AdjointNaPasDEquipe(String idAdjoint){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return true;
        for (Equipe equipe : equipes) {
            if (equipe.getChefAdjId().equals(idAdjoint)) return false;
        }
        return true;
    }

    public static String getTeamNameByIdCapitaine(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return equipe.getEquipeId();
        }
        return null;
    }
    public static String getTeamNameByIdAdjoint(String idAdjoin){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for (Equipe equipe : equipes) {
            if (equipe.getChefAdjId().equals(idAdjoin)) return equipe.getEquipeId();
        }
        return null;
    }

    public static boolean NbJoueurMaxAtteint(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for(Equipe equipe : equipes){
            if(equipe.getJoueurIds().size()==7 & equipe.getChefId().equals(idCapitaine)){
                return true;
            }
        }
        return false;
    }

    public static boolean removeJoueurByIdCapitaineIdJoueur(String idCapitaine,String idJoueur){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for(Equipe equipe : equipes){
            if(equipe.getChefId().equals(idCapitaine)){
                equipe.getJoueurIds().removeIf(id -> id.equals(idJoueur));
                DataManager.saveEquipes(equipes);
                return true;
            }
        }
        return false;
    }

    public static boolean joueurDejaDansUneEquipe(String idJoueur){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        for(Equipe equipe : equipes){
            for(String joueur : equipe.getJoueurIds()){
                if(joueur.equals(idJoueur)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean estDansCetteEquipe(String team,String player){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        for(Equipe equipe : equipes){
            if(equipe.getEquipeId().equals(team)){
                for(String joueur : equipe.getJoueurIds()){
                    if(joueur.equals(player)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Equipe getEquipeByEquipeName(String name){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for(Equipe equipe : equipes){
            if(equipe.getEquipeId().equals(name)){
                return equipe;
            }
        }
        return null;
    }
}

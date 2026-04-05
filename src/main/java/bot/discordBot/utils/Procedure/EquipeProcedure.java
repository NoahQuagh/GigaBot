package bot.discordBot.utils.Procedure;

import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;

import java.util.ArrayList;

public class EquipeProcedure {
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

    public static String getTeamNameByIdCapitaine(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return equipe.getEquipeId();
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

    public static Equipe getEquipeByIdCapitaine(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for(Equipe equipe : equipes){
            if(equipe.getChefId().equals(idCapitaine)){
                return equipe;
            }
        }
        return null;
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

}

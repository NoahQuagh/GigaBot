package bot.discordBot.utils.Procedure;

import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;

import java.util.ArrayList;

public class ValoDisProcedure {
    public static boolean pseudoValoExist(String pseudo){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return false;}
        for(CompteValoDiscord compte : listeCompteValoDis){
            if(compte.getPseudoValo().equals(pseudo)){
                return true;
            }
        }
        return false;
    }

    public static boolean nomDiscordExist(String nom){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return false;}
        for(CompteValoDiscord compte : listeCompteValoDis){
            if(compte.getNomDiscord().equals(nom)){
                return true;
            }
        }
        return false;
    }

    public static boolean idDiscordExist(String id){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return false;}
        for(CompteValoDiscord compte : listeCompteValoDis){
            if(compte.getIdDiscord().equals(id)){
                return true;
            }
        }
        return false;
    }

    public static int nombreCompteEnregistrer(){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return 0;}
        return listeCompteValoDis.size();
    }

    public static String sertARien(int n){//attente d'un utiliter
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return "Aucun compte à proposer";}
        String nomDs=listeCompteValoDis.get(n).getNomDiscord();
        if(nomDs.isEmpty()){
            return proposerAutoCompleteValue(n);
        }else{
            return nomDs;
        }
    }

    public static String proposerAutoCompleteValue(int n){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return "Aucun compte à proposer";}
        return listeCompteValoDis.get(n).getPseudoValo();
    }
}

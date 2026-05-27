package bot.discordBot.utils.BDD;

import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.commands.CommandContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static bot.discordBot.utils.BDD.DataBaseManager.getConnectionDB;

public class equipe_DB {

    public static int createTeam(CommandContext ctx, String name, String idCapitaine){
        if(teamNameExiste(name)) throw new EquipeException(ctx,"Ce nom d'équipe existe déjà");
        if(estDejaCapitaine(idCapitaine)) throw new CapitaineException(ctx,"Ce joueur est déjà capitaine d'une autre équipe");
        if(getTeamIdByTeamName(name)==0) throw new EquipeException(ctx,"Ce nom d'équipe existe déjà");
        if(estDejaDansUneEquipe(idCapitaine,name)) throw new EquipeException(ctx,"Ce joueur est déjà dans une équipe Premier");

        String requete = "INSERT INTO equipes_bot(nom, capitaine_id) VALUES (?, ?)";
        try (Connection db = getConnectionDB(); PreparedStatement preState = db.prepareStatement(requete)) {
            preState.setString(1, name);
            preState.setString(2, idCapitaine);
            return preState.executeUpdate();
        }catch (SQLException | CapitaineException | EquipeException e) {
            System.err.println("Erreur critique lors de la création de l'équipe : " + e.getMessage());
            return 0;
        }
    }

    public static boolean teamNameExiste(String nomTeam){
        String requete = "SELECT nom FROM equipes_bot";
        try (Connection db = getConnectionDB();
            PreparedStatement preState = db.prepareStatement(requete);
            java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("nom").equals(nomTeam)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            log_DB.writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification team Existe échoué : "+e);
            return true;
        }
    }

    public static boolean estDejaCapitaine(String capitaineId){
        String requete = "SELECT capitaine_id FROM equipes_bot";
        try (Connection db = getConnectionDB();
             PreparedStatement preState = db.prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("capitaine_id").equals(capitaineId)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            log_DB.writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification est Deja Capitaine échoué : "+e);
            return true;
        }
    }

    public static boolean estDejaDansUneEquipe(String joueurId,String name){
        String requete = "SELECT discord_id FROM equipe_joueurs_bot WHERE equipe_id=?";
        try (Connection db = getConnectionDB();
             PreparedStatement preState = db.prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setInt(1, getTeamIdByTeamName(name));
            while (rs.next()) {
                if(rs.getString("discord_id").equals(joueurId)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            log_DB.writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification est Deja Dans Une Équipe échoué : "+e);
            return true;
        }
    }

    public static int getTeamIdByTeamName(String teamName){
        if(!(teamNameExiste(teamName))) return 0;
        String requete = "SELECT id FROM equipes_bot WHERE equipe_id=?";
        try (Connection db = getConnectionDB();
             PreparedStatement preState = db.prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setString(1, teamName);
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            log_DB.writeLog(LevelLog.ERR,equipe_DB.class.getName(),"get Team id par nom d'équipe échoué : "+e);
            return 0;
        }
    }
}

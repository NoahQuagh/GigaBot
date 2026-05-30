package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.enumDB.RoleTeamPremier;
import botdiscord.gigabot.utils.DB.structure.JoueurPremier;
import botdiscord.gigabot.utils.exception.CapitaineException;
import botdiscord.gigabot.utils.exception.EquipeException;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static botdiscord.gigabot.utils.DB.enumDB.RoleTeamPremier.fromString;


public class equipe_DB extends DataBaseManager{

    public equipe_DB(){
        super();
    }

    public int createTeam(CommandContext ctx, String name, String idCapitaine){
        String requete = "INSERT INTO equipes_bot(nom, capitaine_id) VALUES (?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            if(teamNameExiste(name)) throw new EquipeException(ctx,"Ce nom d'équipe existe déjà");
            if(estCapitaine(idCapitaine)) throw new CapitaineException(ctx,"Ce joueur est déjà capitaine d'une autre équipe");
            if(getTeamIdByTeamName(name)<=0) throw new EquipeException(ctx,"Ce nom d'équipe existe déjà");
            if(estDansUneEquipe(idCapitaine)) throw new EquipeException(ctx,"Ce joueur est déjà dans une équipe Premier");

            preState.setString(1, name);
            preState.setString(2, idCapitaine);
            if(addCapitaineInDB(ctx,name,idCapitaine)<=0) throw new CapitaineException(ctx,"Impossible de crée le capitaine de l'équipe");
            return preState.executeUpdate();
        }catch (SQLException | CapitaineException | EquipeException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Erreur critique lors de la création de l'équipe : " + e.getMessage());
            return 0;
        }
    }

    public boolean teamNameExiste(String nomTeam){
        String requete = "SELECT nom FROM equipes_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
            java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("nom").equals(nomTeam)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification team Existe échoué : "+e);
            return true;
        }
    }

    public boolean estCapitaine(String capitaineId){
        String requete = "SELECT capitaine_id FROM equipes_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("capitaine_id").equals(capitaineId)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification est Deja Capitaine échoué : "+e);
            return true;
        }
    }

    public boolean estAdjoint(String adjointId){
        String requete = "SELECT discord_id FROM equipe_joueurs_bot WHERE role='adjoint'";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("discord_id").equals(adjointId)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification est adjoint échoué : "+e);
            return true;
        }
    }

    public boolean estDansUneEquipe(String joueurId){
        String requete = "SELECT discord_id FROM equipe_joueurs_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("discord_id").equals(joueurId)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Verification est Deja Dans Une Équipe échoué : "+e);
            return true;
        }
    }

    public boolean estDansUneEquipePrecise(int teamId,String joueurId){
        String requete = "SELECT discord_id FROM equipe_joueurs_bot where equipe_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1,teamId);
            java.sql.ResultSet rs = preState.executeQuery();
            while (rs.next()) {
                if(rs.getString("discord_id").equals(joueurId)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Verification est déjà dans cette équipe "+teamId+" échoué : "+e);
            return true;
        }
    }

    public int getTeamIdByTeamName(String teamName){
        if(!(teamNameExiste(teamName))) return 0;
        String requete = "SELECT id FROM equipes_bot WHERE nom=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, teamName);
            java.sql.ResultSet rs = preState.executeQuery();
            while (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"get Team id par nom d'équipe à échoué : "+e);
            return 0;
        }
    }

    private int addCapitaineInDB(CommandContext ctx, String name, String idCapitaine){
        String requete = "INSERT INTO equipe_joueur_bot(equipe_id, discord_id,pseudo,role) VALUES (?,?,?,?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, getTeamIdByTeamName(name));
            preState.setString(2, idCapitaine);
            preState.setString(2, ctx.getAuthorName());
            preState.setString(2, RoleTeamPremier.capitaine.toString());
            return preState.executeUpdate();
        }catch (SQLException | CapitaineException | EquipeException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"Erreur critique lors de la création du capitaine de l'équipe : " + e.getMessage());
            return 0;
        }
    }

    public String getTeamNameByCapitaineId(String capitaineId){
        String requete = "SELECT nom FROM equipes_bot WHERE capitaine_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setString(1, capitaineId);
            return rs.getString("nom");
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"get team nom par nom d'équipe à échoué : "+e);
            return null;
        }
    }

    public String getTeamNameByAdjointId(String adjointId){
        String requete = "select nom from equipe_joueurs_bot ej join equipes_bot eb on ej.equipe_id=eb.id where role='adjoint' and discord_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setString(1, adjointId);
            return rs.getString("nom");
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"get team nom par nom d'adjoint à échoué : "+e);
            return null;
        }
    }

    public ArrayList<JoueurPremier> getJoueurPremierListByTeamName(String teamName){
        ArrayList<JoueurPremier> listeJoueur = new ArrayList<>();
        String requete = "select nom,discord_id,pseudo,role from equipe_joueurs_bot ej join equipes_bot eb on ej.equipe_id=eb.id where nom=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setString(1, teamName);
            while (rs.next()){
                listeJoueur.add(new JoueurPremier(rs.getString("nom"),rs.getString("discord_id"),rs.getString("pseudo"),fromString(rs.getString("role"))));
            }
            return listeJoueur;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,equipe_DB.class.getName(),"get joueur liste par nom d'équipe à échoué : "+e);
            return null;
        }
    }

    public String getCapitaineIdByTeamName(String name){
        String requete = "SELECT capitaine_id FROM equipes_bot WHERE nom=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setString(1, name);
            return rs.getString("capitaine_id");
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"get capitaine id par nom d'équipe à échoué : "+e);
            return null;
        }
    }

    public boolean successionCapitaine(String teamName,String joueurId){
        return defNouveauRoleAncienCapitaine(getTeamIdByTeamName(teamName), getCapitaineIdByTeamName(teamName)) != 0 &&
                defNouveauCapitaine(teamName, joueurId) != 0 &&
                defNouveauCapitaineInEquipe(getTeamIdByTeamName(teamName), joueurId) != 0;
    }

    private int defNouveauCapitaine(String teamName,String joueurId){
        String requete = "UPDATE equipes_bot set capitaine_id=? WHERE nom=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, joueurId);
            preState.setString(2, teamName);
            return preState.executeUpdate();
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Définition du nouveau capitaine échoué : "+e);
            return 0;
        }
    }

    private int defNouveauCapitaineInEquipe(int teamId, String joueurId){
        String requete = "UPDATE equipe_joueurs_bot set discord_id=? WHERE equipe_id=? AND role='capitaine'";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, joueurId);
            preState.setInt(2, teamId);
            return preState.executeUpdate();
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Définition du nouveau capitaine dans l'équipe échoué : "+e);
            return 0;
        }
    }

    private int defNouveauRoleAncienCapitaine(int teamId,String ancienCapitaine){
        String requete = "UPDATE equipe_joueurs_bot set role='joueur' WHERE equipe_id=? AND discord_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, teamId);
            preState.setString(2, ancienCapitaine);
            return preState.executeUpdate();
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Définition du nouveau capitaine dans l'équipe a échoué : "+e);
            return 0;
        }
    }

    public int removeJoueurByTeamId(int teamId,String joueurId){
        String requete = "DELETE FROM equipe_joueurs_bot WHERE equipe_id=? AND discord_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, teamId);
            preState.setString(2, joueurId);
            return preState.executeUpdate();
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Suppression du joueur "+joueurId+" de l'équipe a échoué : "+e);
            return 0;
        }
    }

    public int deleteTeamByTeamName(String teamName){
    }
}

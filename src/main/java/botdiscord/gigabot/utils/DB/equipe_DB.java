package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.enumDB.RoleTeamPremier;
import botdiscord.gigabot.utils.exception.CapitaineException;
import botdiscord.gigabot.utils.exception.EquipeException;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.sql.PreparedStatement;
import java.sql.SQLException;



public class equipe_DB extends DataBaseManager{

    public equipe_DB() throws SQLException {
        super();
    }

    public int createTeam(CommandContext ctx, String name, String idCapitaine){
        String requete = "INSERT INTO equipes_bot(nom, capitaine_id) VALUES (?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            if(teamNameExiste(name)) throw new EquipeException(ctx,"Ce nom d'équipe existe déjà");
            if(estDejaCapitaine(idCapitaine)) throw new CapitaineException(ctx,"Ce joueur est déjà capitaine d'une autre équipe");
            if(getTeamIdByTeamName(name)<=0) throw new EquipeException(ctx,"Ce nom d'équipe existe déjà");
            if(estDejaDansUneEquipe(idCapitaine)) throw new EquipeException(ctx,"Ce joueur est déjà dans une équipe Premier");

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

    public boolean estDejaCapitaine(String capitaineId){
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

    public boolean estDejaDansUneEquipe(String joueurId){
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

    public int getTeamIdByTeamName(String teamName){
        if(!(teamNameExiste(teamName))) return 0;
        String requete = "SELECT id FROM equipes_bot WHERE nom=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            preState.setString(1, teamName);
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


}

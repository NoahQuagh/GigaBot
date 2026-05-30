package botdiscord.gigabot.commandsBot.sousCmdPremierValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandPremier;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.equipe_DB;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.exception.CapitaineException;
import botdiscord.gigabot.utils.exception.JoueurException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.entities.User;


import java.util.List;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;
import static botdiscord.gigabot.utils.success.success.EventSuccess;


public class CommandPremierNewCapitaine extends CommandPremier {

    log_DB logs;
    equipe_DB equipes;

    public CommandPremierNewCapitaine() {
        this.logs=new log_DB();
        this.equipes=new equipe_DB();
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            List<User> joueursMentioned = ctx.getMentionedUsers();
            String idJoueur = joueursMentioned.getFirst().getId();
            String idCapitaine = ctx.getAuthorId();
            String joueur = joueursMentioned.getFirst().getName();

            if (!(equipes.estCapitaine(idCapitaine)))
                throw new CapitaineException(ctx, "Vous ne posséder pas de team Premier");
            String team = equipes.getTeamNameByCapitaineId(idCapitaine);
            if (!(equipes.estDansUneEquipePrecise(equipes.getTeamIdByTeamName(team), idJoueur)))
                throw new JoueurException(ctx, "Ce joueur ne fait pas parti de votre team Premier");
            if (!(equipes.successionCapitaine(team, idJoueur)))
                throw new JoueurException(ctx, "Impossible de modifier le capitaine de votre team Premier");


            EventSuccess(ctx, "Succession  réussie", "**" + joueur + "** est désormais capitaine de la team Premier **" + team.toUpperCase() + "**");
            logs.writeLog(LevelLog.OK,getClass().getName(),"Le joueur " + joueur + " est maintenant capitaine de l'équipe Premier " + team);
        }catch (JoueurException | CapitaineException e){
            logs.writeLog(LevelLog.OK,getClass().getName(),"Syntaxe incorrecte : " + e);
        }catch (Exception e){
            ExceptionDefault(ctx,"Impossible de supprimer ce joueur de votre Team Premier");
            logs.writeLog(LevelLog.OK,getClass().getName(),"Échec de la commmande : " + e);
        }
    }
}

package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import com.sun.net.httpserver.Authenticator;
import org.javacord.api.entity.user.User;

import java.util.List;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Success.success.EventSuccess;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierSuprimerJoueur extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            List<User> joueursMentioned = ctx.getMentionedUsers();
            String idJoueur = joueursMentioned.getFirst().getIdAsString();
            String idCapitaine = ctx.getAuthorId();
            String joueur = joueursMentioned.getFirst().getName();

            if (CapitaineNaPasDEquipe(idCapitaine))
                throw new CapitaineException(ctx, "Vous ne posséder pas de team Premier");
            String team = getTeamNameByIdCapitaine(idCapitaine);
            if (!(estDansCetteEquipe(team, idJoueur)))
                throw new JoueurException(ctx, "Ce joueur ne fait pas parti de votre team Premier");
            if (!(removeJoueurByIdCapitaineIdJoueur(idCapitaine, idJoueur)))
                throw new JoueurException(ctx, "Impossible de supprimer ce joueur de votre team Premier");

            EventSuccess(ctx, "Suppression réussie", "**" + joueur + "** a été supprimé de la team Premier **" + team.toUpperCase() + "**");
            writeLogFile("logs.txt","Joueur " + joueur + " supprimé de la team Premier " + team);

        }catch (JoueurException | CapitaineException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        }catch (Exception e){
            ExceptionDefault(ctx,"Impossible de supprimer ce joueur de votre Team Premier");
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
        }
    }
}

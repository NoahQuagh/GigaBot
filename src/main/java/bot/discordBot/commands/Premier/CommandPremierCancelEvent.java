package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.DefaultException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.Exception.RappelException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import bot.discordBot.utils.commands.datamanager.DataStructure.Rappel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static bot.discordBot.commands.Premier.CommandPremierEvent.netoyageRappel;
import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Success.success.EventSuccess;
import static bot.discordBot.utils.commands.Code.AUCUNE_DONNEE_TROUVER;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierCancelEvent extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args){
        netoyageRappel();
        if (ctx.isSlash()) ctx.defer();
        try {
            if (CapitaineNaPasDEquipe(ctx.getAuthorId()))
                throw new EquipeException(ctx, "Il existe aucune team Premier dont vous êtes le capitaine");

            String team = getTeamNameByIdCapitaine(ctx.getAuthorId());
            Equipe equipe = getEquipeByEquipeName(team);

            ArrayList<Rappel> tousLesRappels = DataManager.loadRappels(); //
            if (tousLesRappels == null || tousLesRappels.isEmpty())
                throw new RappelException(ctx, "Aucun rappel n'est programmé.");

            execute(ctx, args[1] + ":" + args[2] + ":" + args[3], args[4] + ":" + args[5], getTeamNameByIdAdjoint(ctx.getAuthorId()), tousLesRappels, equipe,0);
        }catch (EquipeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+ AUCUNE_DONNEE_TROUVER);
        }catch (Exception e){
            ExceptionDefault(ctx, "Impossible de supprimer le rappel ( vérifiez la date ) ");
            writeLogFile("logs.txt", "Code : " + Code.ECHEC + " : " + e);
        }
    }

    public void execute(CommandContext ctx, String date, String heure, String team,ArrayList<Rappel> tousLesRappels,Equipe equipe,int n){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
        LocalDateTime dateTimeEvent = LocalDateTime.parse(heure+ " " + date, formatter);

        tousLesRappels.removeIf(rappel ->
                equipe.getJoueurIds().contains(rappel.getUserId()) &&
                        rappel.getDate().equals(dateTimeEvent)
        );

        for (String idJoueur : equipe.getJoueurIds()) {
            // 1. On cherche la tâche en mémoire
            String cle = idJoueur + ":" + dateTimeEvent;
            java.util.concurrent.ScheduledFuture<?> taskToCancel = CommandPremierEvent.tachesActives.get(cle);

            // 2. Si elle existe, on l'arrête
            if (taskToCancel != null) {
                taskToCancel.cancel(false); // false = ne pas interrompre si c'est déjà en train d'envoyer
                CommandPremierEvent.tachesActives.remove(cle);

            }
        }
        if(n==1){
            EventSuccess(ctx,"Évènement annulé","L'évènement du "+heure.replace(":","/")+" à "+date+" est annulé par manque de joueur");
        }else {
            EventSuccess(ctx,"Évènement annulé","L'évènement du "+heure.replace(":","/")+" à "+date+" est annulé");
        }
        DataManager.saveRappels(tousLesRappels);

    }
}

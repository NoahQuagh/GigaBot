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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static bot.discordBot.Main.jda;
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

            ArrayList<Rappel> tousLesRappels = DataManager.loadRappels();
            if (tousLesRappels == null || tousLesRappels.isEmpty())
                throw new RappelException(ctx, "Aucun rappel n'est programmé.");

            execute(ctx, args[1] + ":" + args[2] + ":" + args[3], args[4] + ":" + args[5], getTeamNameByIdAdjoint(ctx.getAuthorId()), equipe,0,"");
        }catch (EquipeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+ AUCUNE_DONNEE_TROUVER);
        }catch (Exception e){
            ExceptionDefault(ctx, "Impossible de supprimer le rappel ( vérifiez la date ) ");
            writeLogFile("logs.txt", "Code : " + Code.ECHEC + " : " + e);
        }
    }

    public void execute(CommandContext ctx, String date, String heure, String team,Equipe equipe,int n,String salonid){
        ArrayList<Rappel> rappels = DataManager.loadRappels();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
        LocalDateTime dateTimeEvent = LocalDateTime.parse(date+ " " + heure, formatter);

        rappels.removeIf(rappel ->
                equipe.getJoueurIds().contains(rappel.getUserId()) &&
                        rappel.getDate().equals(dateTimeEvent)
        );
        DataManager.saveRappels(rappels);

        for (String idJoueur : equipe.getJoueurIds()) {
            if (idJoueur == null) continue;

            String cle = idJoueur.trim() + ":" + dateTimeEvent;

            java.util.concurrent.ScheduledFuture<?> taskToCancel = CommandPremierEvent.tachesActives.get(cle);


            if (taskToCancel != null) {
                taskToCancel.cancel(false);
                CommandPremierEvent.tachesActives.remove(cle);

            }
        }
        if(ctx == null){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅ Évènement annulé")
                    .setDescription("L'évènement du "+date.replace(":","/")+" à "+heure+" de la team "+team+" est annulé par manque de joueur.")
                    .setColor(Color.GREEN);
            GuildMessageChannel channel = jda.getChannelById(GuildMessageChannel.class, salonid);
            channel.sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if(n==1){
            EventSuccess(ctx,"Évènement annulé","L'évènement du "+heure.replace(":","/")+" à "+date+" est annulé par manque de joueur");
        }else {
            EventSuccess(ctx,"Évènement annulé","L'évènement du "+heure.replace(":","/")+" à "+date+" est annulé");
        }
    }
}

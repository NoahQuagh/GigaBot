package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.Main.jda;
import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.commands.Code.*;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierTeamInvite extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {

            List<User> joueurs = ctx.getMentionedUsers();

            String team = getTeamNameByIdCapitaine(ctx.getAuthorId());

            if(team==null) throw new EquipeException(ctx,"Il existe aucune team Premier dont vous êtes le capitaine");

            if(NbJoueurMaxAtteint(ctx.getAuthorId())) throw new JoueurException(ctx, "Nombre de joueurs dans une team atteint");

            execute(ctx,joueurs,team);

        }catch (CapitaineException e){
            writeLogFile("logs.txt", "Code : " + ACCEE_REFUSE);
        }catch (SyntaxeException e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ SYNTAXE_INCORRECTE);
        }catch (EquipeException | JoueurException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        }catch (IndexOutOfBoundsException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible d'ajouter le joueur'.","@nomJoueur doit être une mention du joueur souhaité",false)
                    .setColor(Color.red);
            ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            ExceptionDefault(ctx, "Impossible d'ajouter le(s) joueur(s)");
        }
    }

    private void sendDemande(User user, String pseudo, String team, String idJoueur, CommandContext ctx) {
        user.openPrivateChannel().queue(pc -> {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("📩 Invitation d'équipe Premier")
                    .setDescription("Le capitaine de l'équipe **" + team + "** vous invite à rejoindre ses rangs !")
                    .setColor(Color.CYAN);

            pc.sendMessageEmbeds(eb.build())
                    .addActionRow(
                            net.dv8tion.jda.api.interactions.components.buttons.Button.success("invite_accept&" + team, "Accepter"),
                            net.dv8tion.jda.api.interactions.components.buttons.Button.danger("invite_refuse&" + team, "Refuser")
                    )
                    .queue();
        }, throwable -> {
            writeLogFile("logs.txt", "Impossible d'envoyer un MP à " + pseudo);
        });
    }

    public void validerInvitation(User user, String team, boolean accepte, net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent event, CommandContext ctx) {
        String pseudo = user.getName();
        String idJoueur = user.getId();

        if (accepte) {
            try {
                ArrayList<Equipe> curseur = DataManager.loadEquipes();
                boolean equipeTrouvee = false;

                for (Equipe equipe : curseur) {
                    // Vérifie si l'ID ou le nom correspond à 'team'
                    if (equipe.getEquipeId().equalsIgnoreCase(team)) {
                        if (!equipe.getJoueurIds().contains(idJoueur)) {
                            equipe.getJoueurIds().add(idJoueur);
                            equipeTrouvee = true;
                        }
                        break;
                    }
                }

                if (equipeTrouvee) {
                    DataManager.saveEquipes(curseur);
                    writeLogFile("logs.txt", pseudo + " est inscrit dans l'équipe : " + team);

                    event.editMessage("✅ Vous avez accepté l'invitation. Bienvenue dans l'équipe **" + team + "** !")
                            .setComponents(new ArrayList<>())
                            .queue();

                    // Notification au capitaine
                    String capitaineId = getEquipeByEquipeName(team).getChefId();

                    jda.retrieveUserById(capitaineId).queue(userCap -> {
                        user.openPrivateChannel().queue(channel -> {
                            channel.sendMessage("🔔 **" + pseudo + "** a accepté l'invitation et rejoint l'équipe **" + team + "** !").queue();
                        }, throwable -> {
                            writeLogFile("logs.txt","Impossible d'envoyer le MP : l'utilisateur a bloqué le bot.");
                        });
                    }, throwable -> {
                        writeLogFile("logs.txt","Utilisateur introuvable pour l'ID : " + capitaineId);
                    });
                }

            } catch (Exception e) {
                writeLogFile("logs.txt", "Erreur ajout équipe : " + e.getMessage());
            }
        } else {
            // Logique de refus : on modifie le message pour confirmer le refus et on retire les boutons
            event.editMessage("❌ Vous avez refusé l'invitation.")
                    .setComponents(new ArrayList<>())
                    .queue();

            // Notification au capitaine
            String capitaineId = getEquipeByEquipeName(team).getChefId();

            jda.retrieveUserById(capitaineId).queue(userCap -> {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage("🔔 rejet : **" + pseudo + "** a refusé de rejoindre l'équipe **" + team + "**.").queue();
                }, throwable -> {
                    writeLogFile("logs.txt","Impossible d'envoyer le MP : l'utilisateur a bloqué le bot.");
                });
            }, throwable -> {
                writeLogFile("logs.txt","Utilisateur introuvable pour l'ID : " + capitaineId);
            });

            writeLogFile("logs.txt", pseudo + " a refusé l'invitation pour " + team);
        }
    }

    public void execute(CommandContext ctx, List<User> joueurs, String team) {
        StringBuilder confirmation = new StringBuilder();
        for (User user : joueurs) {
            // Correction JDA : getId() au lieu de getIdAsString()
            String idJoueur = user.getId();

            if (joueurDejaDansUneEquipe(idJoueur)) {
                confirmation.append("⚠️ **").append(user.getName()).append("** fait déjà partie d'une équipe.\n");
                continue;
            }

            String pseudo = user.getName();
            if (!(ctx.getAuthorId().equals(idJoueur))) {
                confirmation.append("✅ Invitation envoyée à **").append(pseudo).append("**\n");
                writeLogFile("logs.txt", pseudo + " | Invitation envoyée pour rejoindre l'équipe " + team);
                sendDemande(user, pseudo, team, idJoueur, ctx);
            }
        }

        if (confirmation.length() == 0) {
            confirmation.append("❌ Aucun joueur valide n'a été trouvé.");
        }
        ctx.getEvent().getHook().sendMessage(confirmation.toString()).queue();
    }

}

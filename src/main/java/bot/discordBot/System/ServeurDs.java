package bot.discordBot.System;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.EnumSet;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class ServeurDs {
    public static TextChannel getOrCreateWelcomeChannel(Guild guild) {
        TextChannel channel = guild.getTextChannelsByName("accueil-gigabot", true)
                .stream().findFirst().orElse(null);

        if (channel == null) {
            channel = guild.createTextChannel("accueil-gigabot")
                    .addPermissionOverride(guild.getPublicRole(),
                            null,
                            EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                    .complete();

            writeLogFile("logs.txt", "Salon #arrivée créé sur le serveur : " + guild.getName());
        }
        return channel;
    }

    public static Role getOrCreateRole(Guild guild, String name, Color color) {
        // 1. Chercher le rôle (insensible à la casse)
        return guild.getRoles().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    // 2. Si non trouvé, on le crée SANS .complete()
                    guild.createRole()
                            .setName(name)
                            .setColor(color)
                            .setHoisted(true) // Affiche le rôle séparément dans la liste des membres
                            .queue(role -> writeLogFile("logs.txt","Rôle créé : " + name));
                    return null; // Retourne null le temps que Discord le crée
                });
    }
}

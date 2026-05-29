package botdiscord.gigabot.system;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.EnumSet;

public class ServeurDs {

    private log_DB logs;

    public ServeurDs() throws SQLException {
        this.logs = new log_DB();
    }

    public TextChannel getOrCreateChannel(Guild guild,String name) {
        TextChannel channel = guild.getTextChannelsByName(name, true)
                .stream().findFirst().orElse(null);

        if (channel == null) {
            channel = guild.createTextChannel(name)
                    .addPermissionOverride(guild.getPublicRole(),
                            null,
                            EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                    .complete();

            logs.writeLog(LevelLog.OK, ServeurDs.class.getName(),"Salon "+name+" créé sur le serveur : " + guild.getName());
        }
        return channel;
    }

    public Role getOrCreateRole(Guild guild, String name, Color color) {
        return guild.getRoles().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    guild.createRole()
                            .setName(name)
                            .setColor(color)
                            .setHoisted(true)
                            .queue(role -> logs.writeLog(LevelLog.OK, ServeurDs.class.getName(),"Rôle créé : " + name));
                    return null;
                });
    }
}

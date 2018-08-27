/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core;

import net.rising.core.api.Database;
import net.rising.core.api.Special;
import static net.rising.core.Commands.COMMANDS;
import static net.rising.ui.MainUI.bot;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static net.rising.core.Commands.COMMANDSADMIN;
import static net.rising.core.Commands.COMMANDSMOD;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import net.rising.ui.MainUI;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.shard.LoginEvent;
import sx.blah.discord.handle.impl.events.shard.ReconnectSuccessEvent;
import sx.blah.discord.handle.obj.Permissions;

/**
 *
 * @author crysi
 */
public class EventRunner {

    public static final long STARTTIME = System.currentTimeMillis() / 1000;
    public static final String VERSION = "2.02d Beta";
    public static final String FORMATTEDSTARTTIME = new Special().timeNow();

    private static int serverCount = 0;
    private static int messagesProcessed = 0;
    public static int processed = 0;
    private static String prefix = "!";
    protected static Special special = new Special();
    protected static Database database = new Database();

    private static final String[] COMMANDLIST = {"set admin", "set mod", "set cprefix", "set welcomeleave", "set welcome", "set leave", "role assign", "role remove", "clear", "restore", "prefix", "suffix", "set private", "poll", "analyze", "list role", "list settings", "stats server", "stats user", "stats", "commands", "latency", "help", "test", "suggest"};
    private static boolean checking = false;
    private static boolean reconnected = false;
    private static String messagePP;

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        long timeStart = System.nanoTime();

        messagesProcessed++;
        if (event.getClient().getGuilds().size() > serverCount || event.getClient().getGuilds().size() < serverCount) {
            serverCount = event.getClient().getGuilds().size();
            MainUI api = new MainUI();
            api.setStats(serverCount);
            RequestBuffer.request(() -> event.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, serverCount + " servers"));
        }
        if (Special.containsAny(event.getMessage().getContent(), COMMANDLIST) && !event.getChannel().isPrivate()) {
            if (!event.getAuthor().isBot()) {
                String id = event.getGuild().getStringID();
                String name = event.getGuild().getName();
                prefix = database.getPrefix(id, name, event.getGuild());
                String messageUP = event.getMessage().getContent();
                if (StringUtils.startsWith(messageUP, prefix) && !StringUtils.startsWith(messageUP, prefix + prefix)) {
                    messageUP = StringUtils.lowerCase(messageUP);
                    checking = false;

                    IUser messanger = event.getAuthor();
                    IUser owner = event.getGuild().getOwner();
                    String admin = database.getAdminRole(id, name);
                    String moderator = database.getModRole(id, name);

                    boolean guildOwner = messanger.equals(owner)
                            || messanger.equals(bot.getApplicationOwner())
                            || messanger.getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER);
                    boolean guildAdmin = messanger.getRolesForGuild(event.getGuild())
                            .parallelStream()
                            .anyMatch((role) -> role.getStringID().equals(admin));
                    boolean guildModerator = messanger.getRolesForGuild(event.getGuild())
                            .parallelStream()
                            .anyMatch((role) -> role.getStringID().equals(moderator));

                    messagePP = StringUtils.remove(messageUP, prefix);

                    Arrays.asList(COMMANDLIST)
                            .parallelStream()
                            .filter(command -> StringUtils.startsWith(messagePP, command))
                            .forEachOrdered(com -> {
                                if (StringUtils.startsWith(messagePP, com) && !checking) {
                                    messagePP = com;
                                    checking = true;
                                }
                            });

                    if (COMMANDSADMIN.containsKey(messagePP)) {
                        COMMANDSADMIN.get(messagePP).run(event, guildOwner, guildAdmin, guildModerator);
                        processed++;
                    } else if (COMMANDSMOD.containsKey(messagePP)) {
                        COMMANDSMOD.get(messagePP).run(event, guildOwner, guildAdmin, guildModerator);
                        processed++;
                    } else if (COMMANDS.containsKey(messagePP)) {
                        COMMANDS.get(messagePP).run(event, guildOwner, guildAdmin, guildModerator);
                        processed++;
                    }
                    String endTime = " (" + (Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0) + "ms)";
                    special.log(event.getMessage().getFormattedContent() + endTime, true, event.getChannel());
                }
            }
        } else if (event.getMessage().getMentions().contains(bot.getOurUser())) {
            if (StringUtils.startsWith(event.getMessage().getContent(), bot.getOurUser().mention())) {
                prefix = database.getPrefix(event.getGuild().getStringID(), event.getGuild().getName(), event.getGuild());
                special.sendMessage("Please type ``" + prefix + "commands`` for commands.\nType ``" + prefix + "analyze`` if there seems to be an issue.\nType ``" + prefix + "help`` for a tutorial video.", event);
                special.log(event.getMessage().getFormattedContent() + " (" + (Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0) + "ms)", true, event.getChannel());
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Bot Connecting and Disconnecting Events">      
    @EventSubscriber
    public void onBotServerJoin(GuildCreateEvent e) {
        if (checking == true) {
            database.createDatabase(e.getGuild(), true);
        }
    }

    @EventSubscriber
    public void onBotDisconnect(DisconnectedEvent event) {
        try {
            special.log(event.getReason().toString(), true, bot.getGuildByID(420933608358805536l).getDefaultChannel());
        } catch (NullPointerException e) {
        }
    }

    @EventSubscriber
    public void onBotConnect(ReconnectSuccessEvent event) {
        special.log("Bot Successfully Reconnected!", true, bot.getGuildByID(420933608358805536l).getDefaultChannel());
        reconnected = true;
    }

    @EventSubscriber
    public void onBotConnect(LoginEvent event) {
        if (reconnected) {
            RequestBuffer.request(() -> event.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, serverCount + " servers"));
        }
    }

    @EventSubscriber
    public void onBotJoin(ReadyEvent e) {
        e.getClient().getGuilds().forEach(guild -> {
            database.createDatabase(guild, true);
        });

        bot.getGuildByID(456402013945856027l).getChannelsByName("phoenix-bot-connect-log").get(0).sendMessage("**Bot Connected!**");

        serverCount = e.getClient().getGuilds().size();
        e.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, serverCount + " servers");
        checking = true;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="User Leave and Join Events">      
    @EventSubscriber
    public void onUserJoin(UserJoinEvent event) {
        if (database.isWLEnabled(event.getGuild().getStringID(), event.getGuild().getName())) {
            RequestBuffer.request(() -> {
                String message = database.getWelcomeMessage(event.getGuild().getStringID(), event.getGuild().getName());
                message = StringUtils.replace(message, "#mention#", event.getUser().mention());
                message = StringUtils.replace(message, "#user#", event.getUser().getName());
                try {
                    event.getGuild().getDefaultChannel().sendMessage(message);
                } catch (MissingPermissionsException e) {
                    special.log(e.getErrorMessage(), true, event.getGuild().getDefaultChannel());
                }
            });
        }
    }

    @EventSubscriber
    public void onUserLeave(UserLeaveEvent event) {
        if (database.isWLEnabled(event.getGuild().getStringID(), event.getGuild().getName())) {
            RequestBuffer.request(() -> {
                String message = database.getLeaveMessage(event.getGuild().getStringID(), event.getGuild().getName());
                message = StringUtils.replace(message, "#mention#", "");
                message = StringUtils.replace(message, "#user#", event.getUser().getName());
                try {
                    event.getGuild().getDefaultChannel().sendMessage(message);
                } catch (MissingPermissionsException | DiscordException e) {
                    special.log(e.getMessage(), true, event.getGuild().getDefaultChannel());
                }
            });
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Guild Related Events">
    @EventSubscriber
    public void onGuildNameChange(GuildUpdateEvent event) {
        if (!event.getNewGuild().getName().equals(event.getOldGuild().getName())) {
            new Database().recreateTable(event.getNewGuild(), event.getOldGuild());
        }
    }

    @EventSubscriber
    public void onRoleUpdate(UserRoleUpdateEvent event) {
        List<List<String>> listOfTitles = special.getSuffixPrefix(event.getNewRoles(), event.getGuild());
        StringBuilder builder = new StringBuilder();
        if (listOfTitles.get(3).size() > 0) {
            for (int j = 0; j < listOfTitles.get(3).size(); j++) {
                try {
                    if (!listOfTitles.get(3).get(j).equals("null")) {
                        builder.append(listOfTitles.get(3).get(j));
                    }
                } catch (NullPointerException e) {
                }
            }
        }
        builder.append(special.getUsername(event.getUser().getDisplayName(event.getGuild()), listOfTitles, listOfTitles));
        if (listOfTitles.get(4).size() > 0) {
            for (int j = 0; j < listOfTitles.get(4).size(); j++) {
                try {
                    if (!listOfTitles.get(4).get(j).equals("null")) {
                        builder.append(listOfTitles.get(4).get(j));
                    }
                } catch (NullPointerException e) {
                }
            }
        }
        RequestBuffer.request(() -> {
            try {
                event.getGuild().setUserNickname(event.getUser(), builder.toString());
                special.log("Username: " + builder.toString(), true, event.getGuild().getDefaultChannel());
                processed++;
            } catch (DiscordException | MissingPermissionsException e) {
                special.log(e.getMessage() + "  (Username: " + builder.toString() + ")", true, event.getGuild().getDefaultChannel());
            }

        });
    }

    // </editor-fold>
    
    public static int getServerCount() {
        return serverCount;
    }

    public static int getMessagesProcessed() {
        return messagesProcessed;
    }

    public static int getProcessed() {
        return processed;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static final String getVersion() {
        return VERSION;
    }

    public static final long getStartTime() {
        return STARTTIME;
    }

    public static void main(String args[]) throws IOException {
        java.awt.EventQueue.invokeLater(() -> new net.rising.ui.MainUI().setVisible(true));
    }
}

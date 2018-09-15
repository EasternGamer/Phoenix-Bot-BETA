/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core;

import net.rising.core.api.Database;
import net.rising.core.api.Utils;
import net.rising.ui.MainUI;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.GuildUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.shard.LoginEvent;
import sx.blah.discord.handle.impl.events.shard.ReconnectSuccessEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static net.rising.core.Commands.*;
import static net.rising.ui.MainUI.bot;

/**
 * @author crysi
 */
public class EventRunner {

    static final long START_TIME = System.currentTimeMillis() / 1000;
    static final String VERSION = "2.1b Beta";

    private static final Database DATABASE = new Database();
    private static final Utils UTILS = new Utils();
    private static final String[] COMMAND_LIST
            = {
            "logs", "set admin", "set mod", "set cprefix", "set autorole", "autorole", "set welcomeleave", "set welcome",
            "set leave", "role assign", "role remove", "clear", "restore", "set single", "prefix", "suffix", "set private",
            "poll", "analyze", "list role", "list settings", "stats server", "stats user", "stats", "commands", "latency",
            "help", "test", "suggest", "bot leave", "donate"
    };
    public static int processed = 0;
    static int messagesProcessed = 0;
    static String prefix = "!";
    private static int serverCount = 0;
    private static boolean checking = false;
    private static boolean reconnected = false;
    private static String messagePP;

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        long timeStart = System.nanoTime();
        messagesProcessed++;
        if (event.getClient().getGuilds().size() > serverCount || event.getClient().getGuilds().size() < serverCount) {
            serverCount = event.getClient().getGuilds().size();
            RequestBuffer.request(() -> event.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, serverCount + " servers"));
            new MainUI().setStats(serverCount);
        }

        if (Utils.containsAny(event.getMessage().getContent(), COMMAND_LIST) && !event.getChannel().isPrivate()) {
            if (!event.getAuthor().isBot()) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                prefix = DATABASE.getSingleEntry(tableName, "CPREFIX");

                String messageUP = event.getMessage().getContent();
                if (StringUtils.startsWith(messageUP, prefix) && !StringUtils.startsWith(messageUP, prefix + prefix)) {
                    IGuild guild = event.getGuild();

                    messageUP = StringUtils.lowerCase(messageUP);
                    checking = false;

                    IUser author = event.getAuthor();
                    IUser owner = guild.getOwner();
                    String adminId = DATABASE.getSingleEntry(tableName, "ADMIN_ID");
                    String modId = DATABASE.getSingleEntry(tableName, "MOD_ID");

                    boolean isOwner = author.equals(owner)
                            || author.equals(bot.getApplicationOwner())
                            || author.getPermissionsForGuild(guild).contains(Permissions.MANAGE_SERVER);

                    boolean isAdmin = author.getRolesForGuild(guild)
                            .stream()
                            .anyMatch(role -> role.getStringID().equals(adminId));

                    boolean isModerator = author.getRolesForGuild(guild)
                            .stream()
                            .anyMatch(role -> role.getStringID().equals(modId));

                    messagePP = StringUtils.remove(messageUP, prefix);
                    Arrays.stream(COMMAND_LIST)
                            .filter(command -> StringUtils.startsWith(messagePP, command))
                            .forEachOrdered(com -> {
                                if (StringUtils.startsWith(messagePP, com) && !checking) {
                                    messagePP = com;
                                    checking = true;
                                }
                            });

                    if (COMMANDSADMIN.containsKey(messagePP)) {
                        COMMANDSADMIN.get(messagePP).run(event, DATABASE, UTILS, isOwner, isAdmin, isModerator);
                        processed++;
                        String endTime = " (" + Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0 + "ms)";
                        UTILS.log(UTILS.getFormattedContent(event.getMessage()) + endTime, event.getChannel(), author, DATABASE);
                    } else if (COMMANDSMOD.containsKey(messagePP)) {
                        COMMANDSMOD.get(messagePP).run(event, DATABASE, UTILS, isOwner, isAdmin, isModerator);
                        processed++;
                        String endTime = " (" + Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0 + "ms)";
                        UTILS.log(UTILS.getFormattedContent(event.getMessage()) + endTime, event.getChannel(), author, DATABASE);
                    } else if (COMMANDS.containsKey(messagePP)) {
                        COMMANDS.get(messagePP).run(event, DATABASE, UTILS, isOwner, isAdmin, isModerator);
                        processed++;
                        String endTime = " (" + Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0 + "ms)";
                        UTILS.log(UTILS.getFormattedContent(event.getMessage()) + endTime, event.getChannel(), author, DATABASE);
                    }

                }
            }
        } else if (event.getMessage().getMentions().contains(bot.getOurUser())) {
            if (StringUtils.startsWith(event.getMessage().getContent(), bot.getOurUser().mention())) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                prefix = DATABASE.getSingleEntry(tableName, "CPREFIX");
                UTILS.sendEmbed("Please Type ``" + prefix + "commands`` for commands.\nType ``" + prefix + "analyze`` if there seems to be an issue.\nType ``" + prefix + "help`` for a tutorial video.", event, DATABASE);
                UTILS.log(UTILS.getFormattedContent(event.getMessage()) + " (" + Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0 + "ms)", event.getChannel(), event.getAuthor(), DATABASE);
            }
        } else if (event.getMessage().getContent().equals(DATABASE.getSingleEntry(DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()), "CPREFIX") + "bot-leave confirm!") && event.getAuthor().equals(event.getGuild().getOwner())) {
            event.getGuild().leave();
            DATABASE.deleteTable(event.getGuild());
        } else if (event.getMessage().getContent().equals("!phoenix-reset") && event.getAuthor().equals(event.getGuild().getOwner())) {
            UTILS.sendEmbed(":warning: **WARNING** :warning:\nProceeding will reset server settings!\nTo proceed, type exactly: **!phoenix-confirm!**", event, DATABASE, true);

        } else if (event.getMessage().getContent().equals("!phoenix-confirm!") && event.getAuthor().equals(event.getGuild().getOwner())) {
            UTILS.sendEmbed("Resetting Settings...", event, DATABASE, true);
            DATABASE.deleteTable(event.getGuild());
            DATABASE.createDatabase(event.getGuild(), false);
            UTILS.sendEmbed("Reset Complete!", event, DATABASE, true);

            UTILS.log("Server Settings Restored!", event.getChannel(), event.getAuthor(), DATABASE);
        }

    }

    @EventSubscriber
    public void onBotServerJoin(GuildCreateEvent e) {
        e.getClient().getGuilds().forEach(guild -> DATABASE.addColumn(DATABASE.getTableName(guild.getStringID(), guild.getName()), ""));
        if (checking) {
            DATABASE.createDatabase(e.getGuild(), true);
        }
    }

    @EventSubscriber
    public void onBotServerLeave(GuildLeaveEvent e) {
        UTILS.log("Bot has left a Server!", e.getGuild().getDefaultChannel(), e.getGuild().getOwner(), DATABASE);
    }

    @EventSubscriber
    public void onBotDisconnect(DisconnectedEvent event) {
        try {
            UTILS.log(event.getReason().toString(), true, bot.getGuildByID(420933608358805536L).getDefaultChannel());
        } catch (NullPointerException ignored) {
        }
    }

    @EventSubscriber
    public void onBotConnect(ReconnectSuccessEvent event) {
        UTILS.log("Bot Successfully Reconnected!", true, bot.getGuildByID(420933608358805536L).getDefaultChannel());
        reconnected = true;
    }

    @EventSubscriber
    public void onBotConnect(LoginEvent event) {
        if (reconnected) {
            RequestBuffer.request(() -> event.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, serverCount + " servers"));
        }
    }
//</editor-fold>
//<editor-folddefaultstate="collapsed"desc="UserLeaveandJoinEvents">

    @EventSubscriber
    public void onBotJoin(ReadyEvent e) {
        e.getClient().getGuilds().forEach(guild -> DATABASE.createDatabase(guild, true));
        try {
            bot.getGuildByID(456402013945856027L).getChannelsByName("phoenix-bot-connect-log").get(0).sendMessage("**Bot Connected!**");
        } catch (NullPointerException ignored) {
        }
        serverCount = e.getClient().getGuilds().size();
        e.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, serverCount + " servers");
        checking = true;
    }

    @EventSubscriber
    public void onUserJoin(UserJoinEvent event) {
        String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
        if (DATABASE.isEnabled(tableName, "WELCOME_LEAVE")) {
            RequestBuffer.request(() -> {
                AtomicReference<String> message = new AtomicReference<>(DATABASE.getSingleEntry(DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()), "WELCOME_MESSAGE"));
                message.set(StringUtils.replace(message.get(), "#mention#", event.getUser().mention()));
                message.set(StringUtils.replace(message.get(), "#user#", event.getUser().getName()));
                try {
                    event.getGuild().getDefaultChannel().sendMessage(message.get());
                } catch (MissingPermissionsException e) {
                    UTILS.log(e.getErrorMessage(), true, event.getGuild().getDefaultChannel());
                }
            });
        }
        if (DATABASE.isEnabled(tableName, "AUTOROLE_ENABLED")) {
            List<List<String>> autoRoles = DATABASE.getAllEntries(tableName, "AUTOROLE_IDS", "AUTOROLE_IDS_ENABLED");
            List<IRole> serverRoles = event.getGuild().getRoles();
            System.out.println(autoRoles);
            serverRoles
                    .forEach(role -> {
                        String isThere = autoRoles.get(0)
                                .stream()
                                .filter(id -> !StringUtils.isEmpty(id))
                                .filter(idRole -> role.getStringID().equals(idRole))
                                .findFirst()
                                .orElse("null");
                        System.out.println(isThere);
                        if (!isThere.equals("null")) {
                            RequestBuffer.request(() -> event.getUser().addRole(role));
                        }

                    });
        }
    }

    @EventSubscriber
    public void onUserLeave(UserLeaveEvent event) {
        if (DATABASE.isEnabled(DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()), "WELCOME_LEAVE")) {
            RequestBuffer.request(() -> {
                String message = DATABASE.getSingleEntry(DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()), "LEAVE_MESSAGE");
                message = StringUtils.replace(message, "#mention#", "");
                message = StringUtils.replace(message, "#user#", event.getUser().getName());
                try {
                    event.getGuild().getDefaultChannel().sendMessage(message);
                } catch (MissingPermissionsException | DiscordException e) {
                    UTILS.log(e.getMessage(), true, event.getGuild().getDefaultChannel());
                }
            });
        }
    }

    //</editor-fold>
//<editor-folddefaultstate="collapsed"desc="GuildRelatedEvents">
    @EventSubscriber
    public void onGuildNameChange(GuildUpdateEvent event) {
        if (!event.getNewGuild().getName().equals(event.getOldGuild().getName())) {
            DATABASE.renameTable(event.getOldGuild().getName(), event.getNewGuild().getName());
        }
    }

    @EventSubscriber
    public void onRoleUpdate(UserRoleUpdateEvent event) {
        if (!event.getUser().equals(event.getGuild().getOwner())) {
            long timeStart = System.nanoTime();
            String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
            List<List<String>> listOfTitles = DATABASE.getSuffixPrefix(event.getNewRoles(), event.getGuild());
            StringBuilder builder = new StringBuilder();
            List<IRole> newRoles = event.getNewRoles();
            if (!DATABASE.isEnabled(tableName, "ONE_ROLE")) {
                listOfTitles.get(3)
                        .stream()
                        .filter(title -> !StringUtils.isBlank(title) && !StringUtils.equals(title, "null"))
                        .forEachOrdered(builder::append);
            } else {
                List<IRole> roleList = UTILS.getRoleList(listOfTitles, newRoles);
                IRole role = event.getGuild().getEveryoneRole();
                int size = 0;
                for (IRole aRoleList : roleList) {
                    if (aRoleList.getPosition() > size) {
                        size = aRoleList.getPosition();
                        role = aRoleList;
                    }
                }
                String roleId = role.getStringID();
                for (int i = 0; i < listOfTitles.get(5).size(); i++) {
                    if (listOfTitles.get(5).get(i).equals(roleId)) {
                        builder.append(listOfTitles.get(3).get(i));
                    }
                }
            }
            builder.append(UTILS.getUsername(event.getUser().getDisplayName(event.getGuild()), listOfTitles, listOfTitles));
            if (!DATABASE.isEnabled(tableName, "ONE_ROLE")) {
                listOfTitles.get(4)
                        .stream()
                        .filter(title -> !StringUtils.isBlank(title) && !StringUtils.equals(title, "null"))
                        .forEachOrdered(builder::append);
            } else {
                List<IRole> roleList = new ArrayList<>();
                for (int i = 0; i < listOfTitles.get(5).size(); i++) {
                    if (!StringUtils.isBlank(listOfTitles.get(4).get(i))) {
                        for (IRole newRole : newRoles) {
                            if (listOfTitles.get(5).get(i).equals(newRole.getStringID())) {
                                roleList.add(newRole);
                            }
                        }
                    }
                }
                IRole role = event.getGuild().getEveryoneRole();
                int size = 0;
                for (IRole aRoleList : roleList) {
                    if (aRoleList.getPosition() > size) {
                        size = aRoleList.getPosition();
                        role = aRoleList;
                    }
                }
                String roleId = role.getStringID();
                for (int i = 0; i < listOfTitles.get(5).size(); i++) {
                    if (listOfTitles.get(5).get(i).equals(roleId)) {
                        builder.append(listOfTitles.get(4).get(i));
                    }
                }
            }
            if (!event.getUser().getDisplayName(event.getGuild()).equals(builder.toString())) {
                RequestBuffer.request(() -> {
                    try {
                        event.getGuild().setUserNickname(event.getUser(), builder.toString());
                        String endTime = " (" + (Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0) + "ms)";
                        UTILS.log("Username:  " + builder.toString() + endTime, true, event.getGuild().getDefaultChannel());
                        processed++;
                    } catch (DiscordException | MissingPermissionsException e) {
                        String endTime = " (" + (Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100) / 100.0) + "ms)";
                        UTILS.log(e.getMessage() + " (Username:  " + builder.toString() + ")" + endTime, true, event.getGuild().getDefaultChannel());
                    }
                });
            }
        }
    }
    public static void main(String args[]) {
        EventQueue.invokeLater(() -> new net.rising.ui.MainUI().setVisible(true));
    }
}

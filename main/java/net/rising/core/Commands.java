/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core;

import java.io.FileNotFoundException;
import net.rising.core.api.Database;
import net.rising.core.api.Special;
import static net.rising.ui.MainUI.bot;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.rising.file.EFile;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

/**
 *
 * @author crysi
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class Commands {

    public static final Map<String, Command> COMMANDS = new HashMap();
    public static final Map<String, Command> COMMANDSADMIN = new HashMap();
    public static final Map<String, Command> COMMANDSMOD = new HashMap();

    public static Boolean pictures = false;
    public static Boolean poll = false;
    public static List<Boolean> options = new ArrayList<>();
    public static List<String> emoteArray = new ArrayList<>();
    public static String[] arguements;
    public static String title;
    public static String pictureString;
    public static IMessage finalMessage;

    private static List<IRole> finalRoles = new ArrayList<>();
    private static int i;
    private static int z;
    private static int m = 0;
    private static Special special = new Special();
    private static Database database = new Database();
    private static EventRunner EventRunner = new EventRunner();

    static {
        COMMANDSADMIN.put("set admin", (event, owner, admin, mod) -> {

            if (owner == true || event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER)) {
                database.setAdmin(event);
                try {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.appendDesc("Administrator Role is now: " + event.getMessage().getRoleMentions().get(0).mention());
                    builder.withColor(16729620);

                    RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                } catch (IndexOutOfBoundsException e) {
                }
            } else {
                special.log("Requires Owner or ``Manage Server`` permission to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set mod", (event, owner, admin, mod) -> {

            if (owner == true || admin == true) {
                database.setMod(event);
                try {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.appendDesc("Moderator Role is now: " + event.getMessage().getRoleMentions().get(0).mention());
                    builder.withColor(16729620);

                    RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                } catch (IndexOutOfBoundsException e) {
                }
            } else {
                special.log("Requires Admin Role and higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set cprefix", (event, owner, admin, mod) -> {

            if (owner == true || admin == true) {
                database.setPrefix(event);
                EmbedBuilder builder = new EmbedBuilder();
                builder.appendDesc("__**All**__ commands use the following prefix: **" + event.getMessage().getContent().split(" ")[2] + "**");
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                special.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set welcomeleave", (event, owner, admin, mod) -> {
            if (owner == true || admin == true) {
                database.setJoinLeave(event);
                EmbedBuilder builder = new EmbedBuilder();
                builder.appendDesc("Welcome and Leave messages is now: **" + database.isWLEnabled(event.getGuild().getStringID(), event.getGuild().getName()) + "**");
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                special.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set leave", (event, owner, admin, mod) -> {
            if (owner == true || admin == true) {
                database.setLeaveMessage(event);
                String message = database.getLeaveMessage(event.getGuild().getStringID(), event.getGuild().getName());
                message = StringUtils.replace(message, "#mention#", "");
                message = StringUtils.replace(message, "#user#", event.getAuthor().getName());
                EmbedBuilder builder = new EmbedBuilder();
                builder.withTitle("Leave Message");
                builder.appendDesc(message);
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                special.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set welcome", (event, owner, admin, mod) -> {
            if (owner == true || admin == true) {
                database.setJoinMessage(event);
                String message = database.getWelcomeMessage(event.getGuild().getStringID(), event.getGuild().getName());
                message = StringUtils.replace(message, "#mention#", event.getAuthor().mention());
                message = StringUtils.replace(message, "#user#", event.getAuthor().getName());
                EmbedBuilder builder = new EmbedBuilder();
                builder.withTitle("Welcome Message");
                builder.appendDesc(message);
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                special.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("clear", (event, owner, admin, mod) -> {

            if (owner == true || admin == true || event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_MESSAGES)) {
                RequestBuffer.request(() -> {
                    try {
                        event.getMessage().delete();
                    } catch (MissingPermissionsException ex) {
                    }
                });

                StringBuilder builder = new StringBuilder();
                IChannel channel = event.getChannel();
                String arguement[] = StringUtils.split(event.getMessage().getContent(), " ");
                if (arguement[1].contains("-")) {
                    special.processClear(arguement, channel);
                } else {
                    try {
                        int numOne = Integer.parseInt(arguement[1]) + 1;
                        if (numOne > 100) {
                            special.log("Number too large! Max value is 100.", false, channel);
                        } else {
                            RequestBuffer.request(() -> {
                                try {
                                    channel.bulkDelete(channel.getMessageHistory(numOne));

                                } catch (DiscordException e) {
                                    special.log("Messages are likely older than 2 weeks. Unable to be deleted.", false, event.getChannel());
                                }
                            });
                            MessageHistory mh = channel.getMessageHistory(numOne + 1);
                            for (int j = 0; j < numOne; j++) {
                                try {
                                    builder.append("[").append(mh.get(j).getCreationDate().toString()).append("]").append(mh.get(j).getAuthor().getName()).append(": ").append(mh.get(j).getContent()).append('\n');
                                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                                }
                            }
                            EFile file = new EFile("F:\\Server\\Database\\Phoenix Bot\\Restores\\", database.getTableName(event.getGuild().getStringID(), event.getGuild().getName()) + ".txt");
                            file.getWriter().setContent(builder.toString());
                        }
                    } catch (NumberFormatException e) {
                        special.log("An error occured while decoding your numbers!", false, event.getChannel());
                    }
                }
            } else {
                special.log("You lack the required permissions!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("restore", (event, owner, admin, mod) -> {
            if (owner == true || admin == true) {
                RequestBuffer.request(() -> {
                    try {
                        EFile file = new EFile("F:\\Server\\Database\\Phoenix Bot\\Restores\\", database.getTableName(event.getGuild().getStringID(), event.getGuild().getName()) + ".txt");

                        event.getAuthor().getOrCreatePMChannel().sendFile("File contains all saved messages", file.getFile());
                    } catch (FileNotFoundException ex) {
                        special.log("No stored messages!", false, event.getChannel());
                    }
                });
            } else {
                special.log("Requires Admin Role or higher to access!", false, event.getChannel());
            }
        });

        COMMANDSADMIN.put("role assign", (event, owner, admin, mod) -> {

            if (owner == true || admin == true) {
                RequestBuilder request = new RequestBuilder(bot);
                request.shouldBufferRequests(true);
                request.setAsync(true);

                List<IRole> roles = event.getMessage().getRoleMentions();
                List<IUser> users = event.getMessage().getMentions();
                request.doAction(() -> {
                    for (int j = i; j < roles.size(); j++) {
                        for (int s = z; s < users.size(); s++) {
                            try {
                                users.get(s).addRole(roles.get(j));
                            } catch (MissingPermissionsException e) {
                                special.log(e.getErrorMessage(), false, event.getChannel());
                            }
                            z++;
                        }
                        z = 0;
                        i++;
                    }
                    i = 0;
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.withColor(16729620);
                    embed.withTitle("Successfully Applied All Roles!");
                    RequestBuffer.request(() -> event.getChannel().sendMessage(embed.build()));
                    return true;
                }).execute();
            } else {
                special.log("Requires Admin Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("role remove", (event, owner, admin, mod) -> {

            if (owner == true || admin == true) {
                RequestBuilder request = new RequestBuilder(bot);
                request.shouldBufferRequests(true);
                request.setAsync(true);

                List<IRole> roles = event.getMessage().getRoleMentions();
                List<IUser> users = event.getMessage().getMentions();
                request.doAction(() -> {
                    for (int j = i; j < roles.size(); j++) {
                        for (int s = z; s < users.size(); s++) {
                            try {
                                users.get(s).removeRole(roles.get(j));
                            } catch (MissingPermissionsException e) {
                                special.log(e.getErrorMessage(), false, event.getChannel());
                            }
                            z++;
                        }
                        z = 0;
                        i++;
                    }
                    i = 0;
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.withTitle("Successfully Removed All Roles!");
                    embed.withColor(16729620);
                    RequestBuffer.request(() -> event.getChannel().sendMessage(embed.build()));
                    return true;
                }).execute();
            } else {
                special.log("Requires Admin Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("set private", (event, owner, admin, mod) -> {
            if (owner == true || admin == true) {
                database.setPM(event);
                EmbedBuilder builder = new EmbedBuilder();
                builder.appendDesc("Private messages are now: **" + database.isPMEnabled(event.getGuild().getStringID(), event.getGuild().getName()) + "**");
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                special.log("Requires Admin Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("poll", (event, owner, admin, mod) -> {

            if (owner == true || admin == true || mod == true) {
                options.clear();

                String option = event.getMessage().getContent();
                String[] emote = {"", "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
                String pollOptions = "";
                if (option.contains("<")) {
                    arguements = StringUtils.split(option, "<");
                    for (int num = 2; num < arguements.length; num++) {
                        try {
                            arguements[num] = StringUtils.replace(arguements[num], ">", "");
                            pollOptions = pollOptions + ":" + emote[num] + ":" + arguements[num] + "\n";
                            options.add(true);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                    try {
                        String[] picture = StringUtils.split(option, "(");
                        pictureString = picture[1].replace(")", "").replace("(", "");
                        pictures = true;
                    } catch (Exception e) {
                        pictures = false;
                    }
                    title = StringUtils.replace(arguements[1], ">", "");
                    EmbedBuilder builder = new EmbedBuilder();
                    if (pictures == true) {
                        builder.withImage(pictureString);
                    }
                    builder.withColor(16729620);
                    builder.withTitle("**" + title + "**");
                    builder.withDescription(pollOptions);
                    try {
                        event.getMessage().delete();
                    } catch (MissingPermissionsException e) {
                    }
                    builder.withFooterIcon("https://i.imgur.com/ghrEWh8.png");
                    builder.withFooterText("Poll | " + EventRunner.getVersion());
                    builder.withTimestamp(Instant.now());

                    emoteArray.add("\u0031\u20E3");
                    emoteArray.add("\u0032\u20E3");
                    emoteArray.add("\u0033\u20E3");
                    emoteArray.add("\u0034\u20E3");
                    emoteArray.add("\u0035\u20E3");
                    emoteArray.add("\u0036\u20E3");
                    emoteArray.add("\u0037\u20E3");
                    emoteArray.add("\u0038\u20E3");
                    emoteArray.add("\u0039\u20E3");
                    RequestBuffer.request(() -> {
                        finalMessage = event.getMessage().getChannel().sendMessage(builder.build());
                        for (int pollNumber = 0; pollNumber < options.size(); pollNumber++) {
                            finalMessage.addReaction(ReactionEmoji.of(emoteArray.get(pollNumber)));
                        }

                        poll = false;
                        emoteArray.removeAll(emoteArray);
                    });
                } else {
                    special.log("!poll <Title> <Option 1> <Option 2>\nNote the **<>** as the seperator", false, event.getChannel());
                }
            } else {
                special.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("prefix", (event, owner, admin, mod) -> {

            if (owner == true || admin == true || mod == true) {
                List<List<String>> old = special.getSuffixPrefix(event.getAuthor().getRolesForGuild(event.getGuild()), event.getGuild());
                database.rolePrefixAdd(event);
                EmbedBuilder builder = new EmbedBuilder();
                builder.appendDesc("**__Prefix Addition for__ " + event.getMessage().getRoleMentions().get(0).mention() + "**\n\nExample: **" + StringUtils.removeStart(event.getMessage().getContent().split("<")[2].replace(">", ""), " ") + "**EasternGamer");
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                event.getGuild().getUsersByRole(event.getMessage().getRoleMentions().get(0)).forEach((user) -> {
                    special.updatePrefixSuffix(user, event.getGuild(), old);
                });
            } else {
                special.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("suffix", (event, owner, admin, mod) -> {

            if (owner == true || admin == true || mod == true) {
                List<List<String>> old = special.getSuffixPrefix(event.getAuthor().getRolesForGuild(event.getGuild()), event.getGuild());
                database.roleSuffixAdd(event);
                EmbedBuilder builder = new EmbedBuilder();
                builder.appendDesc("**__Suffix Addition for__ " + event.getMessage().getRoleMentions().get(0).mention() + "**\n\nExample: " + "EasternGamer**" + event.getMessage().getContent().split("<")[2].replace(">", "") + "**");
                builder.withColor(16729620);
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                event.getGuild().getUsersByRole(event.getMessage().getRoleMentions().get(0)).forEach((user) -> {
                    special.updatePrefixSuffix(user, event.getGuild(), old);
                });
            } else {
                special.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("analyze", (event, owner, admin, mod) -> {
            if (owner == true || admin == true || mod == true) {

                StringBuilder build = new StringBuilder();
                List<Boolean> boolList = new ArrayList<>();
                for (int k = 0; k < 11; k++) {
                    boolList.add(false);
                }
                bot.getOurUser().getPermissionsForGuild(event.getGuild())
                        .parallelStream()
                        .forEachOrdered(permission -> {
                            switch (permission) {
                                case MANAGE_NICKNAMES:
                                    boolList.set(1, true);
                                    break;
                                case MANAGE_MESSAGES:
                                    boolList.set(2, true);
                                    break;
                                case MENTION_EVERYONE:
                                    boolList.set(3, true);
                                    break;
                                case SEND_MESSAGES:
                                    boolList.set(4, true);
                                    break;
                                case EMBED_LINKS:
                                    boolList.set(5, true);
                                    break;
                                case READ_MESSAGE_HISTORY:
                                    boolList.set(6, true);
                                    break;
                                case KICK:
                                    boolList.set(7, true);
                                    break;
                                case ADD_REACTIONS:
                                    boolList.set(8, true);
                                    break;
                                case BAN:
                                    boolList.set(9, true);
                                    break;
                                case CHANGE_NICKNAME:
                                    boolList.set(10, true);
                                    break;
                                default:
                                    break;
                            }
                        });
                build.append("**__Missing Permissions:__**\n\n");
                boolean check = false;
                boolean sendPM = false;
                for (int g = 0; g < 11; g++) {
                    if (boolList.get(g) == false) {
                        switch (g) {
                            case 1:
                                build.append("\u200B \u200B -MANAGE_NICKNAMES **(CRITICAL)**\n");
                                check = true;
                                break;
                            case 2:
                                build.append("\u200B \u200B -MANAGE_MESSAGES **(CRITICAL)**\n");
                                check = true;
                                break;
                            case 3:
                                build.append("\u200B \u200B -MENTION_EVERYONE **(CRITICAL)**\n");
                                check = true;
                                break;
                            case 4:
                                build.append("\u200B \u200B -SEND_MESSAGES **(CRITICAL)**\n");
                                check = true;
                                sendPM = true;
                                break;
                            case 5:
                                build.append("\u200B \u200B -EMBED_LINKS **(OPTIONAL)**\n");
                                check = true;
                                break;
                            case 6:
                                build.append("\u200B \u200B -READ_MESSAGE_HISTORY **(CRITICAL)**\n");
                                check = true;
                                break;
                            case 7:
                                build.append("\u200B \u200B -KICK **(OPTIONAL)**\n");
                                check = true;
                                break;
                            case 8:
                                build.append("\u200B \u200B -ADD_REACTIONS **(CRITICAL)**\n");
                                check = true;
                                break;
                            case 9:
                                build.append("\u200B \u200B -BAN **(OPTIONAL)**\n");
                                check = true;
                                break;
                            case 10:
                                build.append("\u200B \u200B -CHANGE_NICKNAME **(OPTIONAL)**\n");
                                check = true;
                                break;
                            default:
                                break;
                        }
                    }

                }
                if (!check) {
                    build.append("\u200B \u200B *None!* \u200B \u200B \u200B \u200B :white_check_mark:\n");
                }
                if (new Database().isPMEnabled(event.getGuild().getStringID(), event.getGuild().getName())) {
                    sendPM = true;
                }
                build.append("\n**__Can Send Welcome and Leave Messages?__**\n\n");
                List<Permissions> perm = new ArrayList<>();

                event.getGuild().getDefaultChannel().getModifiedPermissions(bot.getOurUser())
                        .parallelStream()
                        .filter(permission -> permission.equals(Permissions.SEND_MESSAGES))
                        .forEach(perms -> perm.add(perms));
                if (perm.contains(Permissions.SEND_MESSAGES)) {
                    build.append("\u200B \u200B *Yes!* \u200B \u200B \u200B \u200B :white_check_mark:\n");
                } else {
                    build.append("\u200B \u200B *No!* \u200B \u200B \u200B \u200B :negative_squared_cross_mark: \n \u200B \u200B Allow the bot to send messages!\n");
                }
                build.append("\n**__Inaccessible Roles__**\n\n");

                for (int a = 0; a < bot.getOurUser().getRolesForGuild(event.getGuild()).size(); a++) {
                    if (bot.getOurUser().getRolesForGuild(event.getGuild()).get(a).getPosition() > m) {
                        m = bot.getOurUser().getRolesForGuild(event.getGuild()).get(a).getPosition();
                    }
                }
                if (!sendPM) {
                    event.getGuild().getRoles()
                            .parallelStream()
                            .filter(role -> role.getPosition() > m)
                            .forEachOrdered(role -> build.append("\u200B \u200B").append(role.mention()).append("\n"));
                } else {
                    event.getGuild().getRoles()
                            .parallelStream()
                            .filter(role -> role.getPosition() > m)
                            .forEachOrdered(role -> build.append("\u200B \u200B").append(role.getName()).append("\n"));
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.withTitle("Analyzation Complete!");

                embed.withColor(16729620);
                embed.appendDesc(build.toString() + "\n\n\n **NOTE:**\n 'Inaccessible Roles' means that the bot cannot apply the roles, cannot change a users name who has these roles or remove those roles.\n This is based on the role hierarchy. The greater the position the more roles it can influence.\n Any further issues contact the support server!");
                if (!sendPM) {
                    special.sendMessage(embed, event);
                } else {
                    RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(embed.build()));
                }
            } else {
                special.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDS.put("list role", (event, owner, admin, mod) -> {

            List<IRole> roles = event.getGuild().getRoles();

            List<List<String>> superRole = new ArrayList<>();
            superRole.add(new ArrayList<>());
            superRole.add(new ArrayList<>());
            superRole.add(new ArrayList<>());

            try (Connection connect = DriverManager.getConnection("jdbc:derby://localhost:1527/PhoenixDatabase", "pro", "profit")) {
                Statement statement = connect.createStatement();
                ResultSet rs = statement.executeQuery("select * from " + database.getTableName(event.getGuild().getStringID(), event.getGuild().getName()));
                while (rs.next()) {
                    superRole.get(0).add(rs.getString(1));
                    superRole.get(1).add(rs.getString(2));
                    superRole.get(2).add(rs.getString(3));
                }
                roles.parallelStream()
                        .filter(role -> special.equalsAny(role.getStringID(), superRole.get(0)))
                        .forEach(role -> finalRoles.add(role));
                EmbedBuilder build = new EmbedBuilder();
                for (int l = 0; l < finalRoles.size(); l++) {
                    StringBuilder name = new StringBuilder();
                    for (int k = 0; k < finalRoles.size() + 1; k++) {
                        if (superRole.get(0).get(k).equals(finalRoles.get(l).getStringID())) {
                            try {
                                if (!superRole.get(1).get(k).contains("null")) {
                                    name.append(superRole.get(1).get(k));
                                }
                            } catch (NullPointerException e) {
                            }
                            name.append("EasternGamer");
                            try {
                                if (!superRole.get(2).get(k).contains("null")) {
                                    name.append(superRole.get(2).get(k));
                                }
                            } catch (NullPointerException e) {
                            }
                        }
                    }
                    try {
                        build.appendField(name.toString(), finalRoles.get(l).mention(), true);
                    } catch (IllegalArgumentException e) {

                    }
                }
                build.withTitle("**Prefixes and Suffixes**");
                build.withColor(16729620);
                special.sendMessage(build, event);
                finalRoles.clear();
            } catch (SQLException e) {
            }

        });
        COMMANDS.put("list settings", (event, owner, admin, mod) -> {

            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String prefix = database.getPrefix(id, name, event.getGuild());
            String adminId = database.getAdminRole(id, name);
            String modId = database.getModRole(id, name);
            String welcomeMessage = database.getWelcomeMessage(id, name);
            String leaveMessage = database.getLeaveMessage(id, name);
            boolean enabled = database.isWLEnabled(id, name);
            boolean pm = database.isPMEnabled(id, name);
            String adminS;
            try {
                adminS = event.getGuild().getRoleByID(Long.parseLong(adminId)).mention();
            } catch (NumberFormatException e) {
                adminS = "Not Set";
            }
            String modS;
            try {
                modS = event.getGuild().getRoleByID(Long.parseLong(modId)).mention();
            } catch (NumberFormatException e) {
                modS = "Not Set";
            }
            StringBuilder build = new StringBuilder();
            build.append("__**Administration Settings**__\n");
            build.append("--> Admin Role -> ").append(adminS).append("\n");
            build.append("--> Mod Role -> ").append(modS).append("\n");
            build.append("\n");
            build.append("__**General Settings**__\n");
            build.append("--> Command Prefix -> ``").append(prefix).append("``\n");
            build.append("--> Welcome & Leave -> ``").append(enabled).append("``\n");
            build.append("--> PM's Enabled -> ``").append(pm).append("``\n");
            build.append("\n");
            build.append("----> Welcome Message -> ``").append(welcomeMessage).append("``\n");
            build.append("----> Leave Message -> ``").append(leaveMessage).append("``\n");
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(16729620);
            builder.appendDescription(build.toString());
            special.sendMessage(builder, event);
        });
        COMMANDS.put("commands", (event, owner, admin, mod) -> {
            EmbedBuilder build = new EmbedBuilder();
            StringBuilder builder = new StringBuilder();

            if (owner == true) {
                builder.append("```css\nOwner\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set admin** @role\n");
                builder.append("```css\nAdministrator\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set cprefix** $$\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set mod** @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set welcome** <#mention# #user# is cool>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set leave** <#user# is leaving>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set welcomeleave** true/false\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("clear** 1..100\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("restore**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("role assign** @user @user @role @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("role remove** @user @user @role @role\n");
                builder.append("```css\nModerator\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set private** true/false\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("poll** <Title> <Option 1>...<Option 9>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("prefix** <content> @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suffix** <content> @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("analyze**\n");
                builder.append("```css\nGeneral\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list settings**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list role**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats server**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats user**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats**\n");
                StringBuilder append = builder.append(" - **").append(EventRunner.getPrefix()).append("latency**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suggest**\n");
            } else if (admin == true) {
                builder.append("```css\nAdministrator\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set cprefix** $$\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set mod** @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set welcome** <#mention# #user# is cool>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set leave** <#user# is leaving>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set welcomeleave** true/false\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("clear** 1..100\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("restore**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("role assign** @user @user @role @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("role remove** @user @user @role @role\n");
                builder.append("```css\nModerator\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set private** true/false\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("poll** <Title> <Option 1>...<Option 9>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("prefix** <content> @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suffix** <content> @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("analyze**\n");
                builder.append("```css\nGeneral\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list settings**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list role**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats server**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats user**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("latency**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suggest**\n");
            } else if (mod == true) {
                builder.append("```css\nModerator\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("set private** true/false\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("poll** <Title> <Option 1>...<Option 9>\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("prefix** <content> @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suffix** <content> @role\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("analyze**\n");
                builder.append("```css\nGeneral\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list settings**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list role**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats server**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats user**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("latency**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suggest**\n");
            } else {
                builder.append("```css\nGeneral\n```");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list settings**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("list role**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats server**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats user**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("stats**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("latency**\n");
                builder.append(" - **").append(EventRunner.getPrefix()).append("suggest**\n");
            }
            build.withColor(16729620);
            build.withTitle("Command List");
            build.appendDesc(builder.toString());
            special.sendMessage(build, event);
        });
        COMMANDS.put("latency", (event, owner, admin, mod) -> {
            RequestBuffer.request(() -> {

                long timeStart = System.nanoTime();

                IChannel channel = event.getChannel();
                IMessage message = channel.sendMessage("Ping");
                message.edit("Pong!");

                double timeEnd = (System.nanoTime() - timeStart) / 2000000.0;
                timeEnd = Math.round(timeEnd * 100) / 100.0;

                message.edit(":alarm_clock:**Response Time:** ``" + timeEnd + "ms``");
            });
        });
        COMMANDS.put("stats server", (event, owner, admin, mod) -> {

            String guildName = event.getGuild().getOwner().getName();
            String guildID = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String iconURL = event.getGuild().getIconURL();

            int memberCount = event.getGuild().getTotalMemberCount();

            long creationDate = event.getGuild().getCreationDate().getEpochSecond();
            long now = Instant.now().getEpochSecond();

            double timeFinal = Math.round(((now - creationDate) / 60 / 60 / 24.0) / 100.0) * 100.0;

            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(16729620);
            embed.withTitle(name);
            embed.withThumbnail(iconURL);
            embed.appendDesc(":pager: **Guild ID:** " + guildID + "\n:mens: **Member Count:** " + memberCount + "\n:calendar: **Creation Date:** " + StringUtils.remove(event.getGuild().getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z") + "\n:floppy_disk: **Guild Owner: **" + guildName);
            special.sendMessage(embed, event);
        });
        COMMANDS.put("stats user", (event, owner, admin, mod) -> {

            EmbedBuilder embed = new EmbedBuilder();
            StringBuilder builder = new StringBuilder();
            if (!event.getMessage().getMentions().isEmpty()) {
                embed.withColor(16729620);
                IUser user = event.getMessage().getMentions().get(0);
                embed.withTitle(user.getDisplayName(event.getGuild()));
                builder.append(":pager: **User ID:** ").append(user.getStringID()).append("\n");
                builder.append(":computer: **Status:** ").append(user.getPresence().getStatus()).append("\n");
                builder.append(":calendar: **Creation Date:** ").append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z")).append("\n");

                embed.appendDesc(builder.toString());
                special.sendMessage(embed, event);
            } else {
                embed.withColor(16729620);
                IUser user = event.getAuthor();
                embed.withTitle(user.getDisplayName(event.getGuild()));
                builder.append(":pager: **User ID:** ").append(user.getStringID()).append("\n");
                builder.append(":computer: **Status:** ").append(user.getPresence().getStatus()).append("\n");
                builder.append(":calendar: **Creation Date:** ").append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z")).append("\n");
                embed.appendDesc(builder.toString());
                special.sendMessage(embed, event);
            }
        });
        COMMANDS.put("stats", (event, owner, admin, mod) -> {
            Runtime runtime = Runtime.getRuntime();

            double memoryTotal = runtime.totalMemory() / 1024 / 1024;
            double memoryFree = runtime.freeMemory() / 1024 / 1024;
            double memory = memoryTotal - memoryFree;

            long time = EventRunner.getStartTime();
            long timeEnd = System.currentTimeMillis() / 1000;

            int days = (int) ((timeEnd - time) / 60 / 60 / 24);
            int hours = (int) ((timeEnd - time) / 60 / 60) - (days * 24);
            int minutes = (int) (((timeEnd - time) / 60) - (hours * 60) - (days * 24 * 60));
            int seconds = (int) ((timeEnd - time) - (minutes * 60) - (hours * 60 * 60) - (days * 24 * 60 * 60));

            String collection = days + " day(s), " + hours + " hour(s), " + minutes + "minute(s) and " + seconds + " second(s).";

            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(16729620);
            embed.appendDesc(":computer: **Version:** " + EventRunner.getVersion() + "\n:floppy_disk: **Memory Usage:** " + memory + " MB" + "\n:stopwatch: **Uptime:** " + collection + "\n:file_cabinet: **Messages Proccessed: **" + EventRunner.getMessagesProcessed() + "\n:file_cabinet: **Commands & Names Processed: **" + EventRunner.getProcessed() + "\n\n:ballot_box: **Upvote This bot:** https://discordbots.org/bot/439454842071547905/vote \n:telephone: **Support Server:** https://discord.gg/RuK3Sw6");
            special.sendMessage(embed, event);
        });
        COMMANDS.put("help", (event, owner, admin, mod) -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(16729620);
            embed.appendDesc("Tutorial Video: https://youtu.be/5mZIA1zqxuo \nCommand List can be found with ``" + EventRunner.getPrefix() + "commands``\n\nDisclaimer: If bot is not working as advertised type " + EventRunner.getPrefix() + "analyze");
            special.sendMessage(embed, event);
        });
        COMMANDS.put("suggest", (event, owner, admin, mod) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorName(event.getAuthor().getName());
            builder.withAuthorIcon(event.getAuthor().getAvatarURL());
            builder.withTitle(event.getGuild().getName());
            builder.withThumbnail(event.getGuild().getIconURL());
            builder.appendDesc(event.getMessage().getContent().replace(EventRunner.getPrefix() + "suggest", ""));
            builder.withColor(16729620);
            RequestBuffer.request(() -> bot.getApplicationOwner().getOrCreatePMChannel().sendMessage(builder.build()));

            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(16729620);
            embed.appendDesc("Feedback successfully sent!");

            special.sendMessage(embed, event);
        });
        COMMANDS.put("test", (event, owner, admin, mod) -> {
            if (event.getAuthor().equals(bot.getApplicationOwner())) {
                EmbedBuilder embed = new EmbedBuilder();
                String[] tempList1 = {"Public | ", "[Admin] "};
                String[] tempList2 = {"Public // ", "Admin | "};
                String[] tempList3 = {" | T"};
                String[] tempList4 = {" // T"};
                List<List<String>> newRole = new ArrayList<>();
                List<List<String>> oldRole = new ArrayList<>();
                newRole.add(new ArrayList<>());
                oldRole.add(new ArrayList<>());
                newRole.add(new ArrayList<>());
                oldRole.add(new ArrayList<>());
                newRole.add(new ArrayList<>());
                oldRole.add(new ArrayList<>());
                newRole.add(new ArrayList<>());
                oldRole.add(new ArrayList<>());
                newRole.add(new ArrayList<>());
                oldRole.add(new ArrayList<>());
                newRole.set(3, Arrays.asList(tempList2));
                newRole.set(4, Arrays.asList(tempList4));
                oldRole.set(3, Arrays.asList(tempList1));
                oldRole.set(4, Arrays.asList(tempList3));
                embed.withColor(16729620);
                embed.appendDesc("Test Result: " + special.getUsername("Public | EasternGamerYT // T", oldRole, newRole));
                special.sendMessage(embed, event);
            }
        });

    }

}

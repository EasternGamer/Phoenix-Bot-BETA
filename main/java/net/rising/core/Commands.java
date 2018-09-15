package net.rising.core;

import jdk.nashorn.internal.runtime.arrays.ArrayIndex;
import net.rising.file.EFile;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static net.rising.ui.MainUI.bot;

/**
 * @author crysi
 */

class Commands {

    static final Map<String, Command> COMMANDS = new HashMap<>();
    static final Map<String, Command> COMMANDSADMIN = new HashMap<>();
    static final Map<String, Command> COMMANDSMOD = new HashMap<>();

    static {
        COMMANDSADMIN.put("bot leave", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER)) {
                UTILS.sendEmbed(":warning: **WARNING** :warning:\n"
                        + "Proceeding will result in deletion of server settings!\n"
                        + "To proceed, type exactly: **" + EventRunner.prefix + "bot-leave confirm!**", event, DATABASE, true);
            }
        });

        COMMANDSADMIN.put("set admin", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER)) {
                try {
                    String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                    String roleId = event.getMessage().getRoleMentions().get(0).getStringID();
                    DATABASE.updateEntry(roleId, tableName, "ADMIN_ID");

                    UTILS.sendEmbed("Administrator Role is now: " + event.getMessage().getRoleMentions().get(0).mention(), event, DATABASE);
                } catch (IndexOutOfBoundsException e) {
                    if (!event.getMessage().getContent().contains("{")) {
                        UTILS.log("Please mention a role __OR__ the exact name of the role in '<>'", false, event.getChannel());
                    } else {
                        String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                        String roleId = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild()).getStringID();
                        if (!StringUtils.isBlank(roleId)) {
                            DATABASE.updateEntry(roleId, tableName, "ADMIN_ID");

                            UTILS.sendEmbed("Administrator Role is now: " + UTILS.getRole(UTILS.getArguement(event.getMessage(), "<", ">"), event.getGuild()).mention(), event, DATABASE);
                        } else {
                            UTILS.log("Please mention a role __OR__ the exact name of the role in '<>'", false, event.getChannel());
                        }
                    }
                }
            } else {
                UTILS.log("Requires Owner or ``Manage Server`` permission to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set mod", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin) {
                try {
                    String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                    String roleId = event.getMessage().getRoleMentions().get(0).getStringID();
                    DATABASE.updateEntry(roleId, tableName, "MOD_ID");

                    UTILS.sendEmbed("Moderator Role is now: " + event.getMessage().getRoleMentions().get(0).mention(), event, DATABASE);

                } catch (IndexOutOfBoundsException e) {
                    if (!event.getMessage().getContent().contains("{")) {
                        UTILS.log("Please mention a role __OR__ the exact name of the role in '{}'", false, event.getChannel());
                    } else {
                        String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                        try {
                            String roleId = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild()).getStringID();

                            DATABASE.updateEntry(roleId, tableName, "MOD_ID");
                            UTILS.sendEmbed("Moderator Role is now: " + UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild()).mention(), event, DATABASE);
                        } catch (IndexOutOfBoundsException ex) {

                            UTILS.log("Please mention a role __OR__ the exact name of the role in '{}'", false, event.getChannel());
                        }
                    }
                }
            } else {
                UTILS.log("Requires Admin Role and higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set cprefix", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin) {
                try {
                    String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                    String roleId = Arrays.asList(event.getMessage().getContent().split(" ")).get(2);
                    DATABASE.updateEntry(roleId, tableName, "CPREFIX");

                    UTILS.sendEmbed("__**All**__ commands use the following prefix: **" + event.getMessage().getContent().split(" ")[2] + "**", event, DATABASE);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    UTILS.log("Please indicate a command prefix\nE.g. " + EventRunner.prefix + "set cprefix !!", false, event.getChannel());
                }
            } else {
                UTILS.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }

        });
        COMMANDSADMIN.put("autorole", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                IRole role;
                try {
                    role = event.getMessage().getRoleMentions().get(0);
                } catch (IndexOutOfBoundsException ex) {
                    try {
                        role = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild());
                    } catch (IndexOutOfBoundsException e) {
                        role = event.getGuild().getEveryoneRole();
                        UTILS.log("Please indicate a role!\nE.g. " + EventRunner.prefix + "autorole @role false __OR__ " + EventRunner.prefix + "autorole <Role> false", false, event.getChannel());
                    }
                }

                if (event.getMessage().getContent().toLowerCase().contains("true")) {
                    DATABASE.addEntry(tableName, "AUTOROLE_IDS", "true" + role.getStringID());
                    UTILS.sendEmbed("Successfully add/editted** " + role.mention() + "(True)**", event, DATABASE);
                } else if (event.getMessage().getContent().toLowerCase().contains("false")) {
                    DATABASE.addEntry(tableName, "AUTOROLE_IDS", "false" + role.getStringID());
                    UTILS.sendEmbed("Successfully add/editted** " + role.mention() + "(False)**", event, DATABASE);
                } else {
                    UTILS.log("Please indicate if it is false or true!\nE.g. " + EventRunner.prefix + "autorole @role false __OR__ " + EventRunner.prefix + "autorole <Role> false", false, event.getChannel());
                }
            } else {
                UTILS.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set autorole", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                if (event.getMessage().getContent().toLowerCase().contains("true")) {
                    DATABASE.updateEntry("true", tableName, "AUTOROLE_ENABLED");
                    UTILS.sendEmbed("Autoroles are now: **Enabled**", event, DATABASE);
                } else if (event.getMessage().getContent().toLowerCase().contains("false")) {
                    UTILS.sendEmbed("Autoroles are now: **Disabled**", event, DATABASE);
                } else {
                    UTILS.log("Please indicate if it is false or true\nE.g. " + EventRunner.prefix + "set autorole false", false, event.getChannel());
                }

            } else {
                UTILS.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set welcomeleave", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                EmbedBuilder builder = new EmbedBuilder();
                builder.withColor(16729620);

                if (event.getMessage().getContent().toLowerCase().contains("true")) {
                    DATABASE.updateEntry("true", tableName, "WELCOME_LEAVE");
                    builder.appendDesc("Welcome and Leave messages is now: **Enabled**");
                    UTILS.sendMessage(builder, event, DATABASE);
                } else if (event.getMessage().getContent().toLowerCase().contains("false")) {
                    DATABASE.updateEntry("false", tableName, "WELCOME_LEAVE");
                    builder.appendDesc("Welcome and Leave messages is now: **Disabled**");
                    UTILS.sendMessage(builder, event, DATABASE);
                } else {
                    UTILS.log("Please indicate if it is false or true\nE.g. " + EventRunner.prefix + "set welcomeleave false", false, event.getChannel());
                }

            } else {
                UTILS.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set leave", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                DATABASE.updateEntry(UTILS.getArguement(event.getMessage(), "<", ">"), tableName, "LEAVE_MESSAGE");

                String message = UTILS.getArguement(event.getMessage(), "<", ">");
                message = StringUtils.replace(message, "#mention#", "");
                message = StringUtils.replace(message, "#user#", event.getAuthor().getName());

                EmbedBuilder builder = new EmbedBuilder();
                builder.withTitle("Leave Message");
                builder.appendDesc(message);
                builder.withColor(16729620);

                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                UTILS.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("set welcome", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                DATABASE.updateEntry(UTILS.getArguement(event.getMessage(), "<", ">"), tableName, "WELCOME_MESSAGE");

                String message = UTILS.getArguement(event.getMessage(), "<", ">");
                message = StringUtils.replace(message, "#mention#", event.getAuthor().mention());
                message = StringUtils.replace(message, "#user#", event.getAuthor().getName());

                EmbedBuilder builder = new EmbedBuilder();
                builder.withTitle("Welcome Message");
                builder.appendDesc(message);
                builder.withColor(16729620);

                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            } else {
                UTILS.log("Requires Admin Role or higher to change!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("clear", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin || event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_MESSAGES)) {

                StringBuilder builder = new StringBuilder();
                IChannel channel = event.getChannel();
                String arguement[] = StringUtils.split(event.getMessage().getContent(), " ");
                if (arguement[1].contains("-")) {
                    UTILS.processClear(arguement, event, channel, DATABASE);
                } else {
                    try {
                        int numOne = Integer.parseInt(arguement[1]) + 1;
                        if (numOne > 101) {
                            UTILS.log("Number too large! Max value is 100.", false, channel);
                        } else {
                            RequestBuffer.request(() -> {
                                try {
                                    MessageHistory mh = channel.getMessageHistory(numOne + 1);
                                    for (int j = 0; j < numOne; j++) {
                                        try {
                                            builder.append("[").append(mh.get(j).getCreationDate().toString()).append("]").append(mh.get(j).getAuthor().getName()).append(": ").append(mh.get(j).getContent()).append('\n');
                                        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
                                        }
                                    }
                                    EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Restores\\", DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()) + ".txt");
                                    file.getWriter().setContent(builder.toString());
                                    channel.bulkDelete(channel.getMessageHistory(numOne));

                                } catch (DiscordException e) {
                                    UTILS.log("Messages are likely older than 2 weeks. Unable to be deleted.", false, event.getChannel());
                                }
                            });
                            RequestBuffer.request(() -> channel.getMessageHistory(1).bulkDelete());
                        }
                    } catch (NumberFormatException e) {
                        UTILS.log("An error occured while decoding your numbers!", false, event.getChannel());
                    }
                }
            } else {
                UTILS.log("You lack the required permissions!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("restore", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                RequestBuffer.request(() -> {
                    try {
                        EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Restores\\", DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()) + ".txt");

                        event.getAuthor().getOrCreatePMChannel().sendFile("File contains all saved messages", file.getFile());
                    } catch (FileNotFoundException ex) {
                        UTILS.log("No stored messages!", false, event.getChannel());
                    }
                });
            } else {
                UTILS.log("Requires Admin Role or higher to access!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("logs", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                RequestBuffer.request(() -> {
                    try {
                        EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Server_Logs\\", DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()) + ".log");

                        event.getAuthor().getOrCreatePMChannel().sendFile("File contains all saved commands used.", file.getFile());
                    } catch (FileNotFoundException ex) {
                        UTILS.log("No stored messages!", false, event.getChannel());
                    }
                });
            } else {
                UTILS.log("Requires Admin Role or higher to access!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("role assign", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin) {
                RequestBuilder request = new RequestBuilder(bot);
                request.shouldBufferRequests(true);
                UTILS.sendEmbed("Operation is Expensive! May take some time to complete.", event, DATABASE);

                List<IRole> roles = event.getMessage().getRoleMentions();
                List<IUser> users = event.getMessage().getMentions();
                request.doAction(() -> {
                    roles.forEach(role -> users.parallelStream()
                            .filter(user -> !user.hasRole(role))
                            .forEach(user -> RequestBuffer.request(() -> {
                                try {
                                    user.addRole(role);
                                } catch (MissingPermissionsException e) {
                                    UTILS.log(e.getErrorMessage(), false, event.getChannel());
                                }
                            })));
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.withColor(16729620);
                    embed.withTitle("Successfully Queue!");
                    UTILS.sendMessage(embed, event, DATABASE);
                    return true;
                }).execute();
            } else {
                UTILS.log("Requires Admin Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSADMIN.put("role remove", (event, DATABASE, UTILS, owner, admin, mod) -> {
            UTILS.sendEmbed("Operation is Expensive! May take some time to complete.", event, DATABASE);
            if (owner || admin) {
                RequestBuilder request = new RequestBuilder(bot);
                request.shouldBufferRequests(true);

                List<IRole> roles = event.getMessage().getRoleMentions();
                List<IUser> users = event.getMessage().getMentions();
                request.doAction(() -> {
                    roles
                            .forEach(role -> users.parallelStream()
                                    .filter(user -> user.hasRole(role))
                                    .forEach(user -> RequestBuffer.request(() -> {
                                        try {
                                            user.removeRole(role);
                                        } catch (MissingPermissionsException e) {
                                            UTILS.log(e.getErrorMessage(), false, event.getChannel());
                                        }
                                    })));
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.withTitle("Successfully Removed All Roles!");
                    embed.withColor(16729620);
                    UTILS.sendMessage(embed, event, DATABASE);
                    return true;
                }).execute();
            } else {
                UTILS.log("Requires Admin Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("set private", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                if (event.getMessage().getContent().toLowerCase().contains("true")) {
                    DATABASE.updateEntry("true", tableName, "PRIVATE_MESSAGE_ENABLED");
                    UTILS.sendEmbed("Private messages are now: **Enabled**", event, DATABASE);
                } else if (event.getMessage().getContent().toLowerCase().contains("false")) {
                    DATABASE.updateEntry("false", tableName, "PRIVATE_MESSAGE_ENABLED");
                    UTILS.sendEmbed("Private messages are now: **Disabled**", event, DATABASE);
                }
            } else {
                UTILS.log("Requires Admin Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("poll", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin || mod) {

                if (event.getMessage().getFormattedContent().contains("<")) {
                    String[] textBasedEmote = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};
                    String[] unicodeBasedEmote = {"\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3", "\uD83D\uDD1F"};

                    int l;

                    List<String> optionArray = Arrays.asList(RegExUtils.removeAll(event.getMessage().getFormattedContent(), ">").split("<"));
                    StringBuilder build = new StringBuilder();
                    if (optionArray.size() < 13) {
                        for (l = 2; l < optionArray.size(); l++) {
                            build.append(textBasedEmote[l - 2]).append(optionArray.get(l)).append('\n');
                        }
                    } //else {
                    // for (l = 2; l < optionArray.size(); l++) {
                    // }
                    //}

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.withTitle(optionArray.get(1));
                    embed.appendDesc(build.toString());
                    embed.withColor(16729620);

                    RequestBuffer.request(() -> {
                        IMessage pollMessage = event.getChannel().sendMessage(embed.build());
                        for (String option : optionArray) {
                            RequestBuilder requestBuilder = new RequestBuilder(bot);
                            requestBuilder.shouldBufferRequests(true);
                            requestBuilder.setAsync(false);
                            requestBuilder.doAction(() -> {
                                try {
                                    pollMessage.addReaction(ReactionEmoji.of(unicodeBasedEmote[optionArray.indexOf(option)]));
                                } catch (ArrayIndexOutOfBoundsException ignore) {
                                }
                                return true;
                            }).build();
                        }

                    });
                } else {
                    UTILS.log("!poll <Title> <Option 1> <Option 2>\nNote the **<>** as the seperator", false, event.getChannel());
                }
            } else {
                UTILS.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("set single", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin || mod) {
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

                if (event.getMessage().getContent().toLowerCase().contains("true")) {
                    DATABASE.updateEntry("true", tableName, "ONE_ROLE");
                    UTILS.sendEmbed("Single Prefix & Suffixes are now: **Enabled**", event, DATABASE);
                } else if (event.getMessage().getContent().toLowerCase().contains("false")) {
                    DATABASE.updateEntry("false", tableName, "ONE_ROLE");
                    UTILS.sendEmbed("Single Prefix & Suffixes are now: **Disabled**", event, DATABASE);
                } else {
                    UTILS.log("Please indicate if it is false or true\nE.g. " + EventRunner.prefix + "set single false", false, event.getChannel());
                }
            } else {
                UTILS.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("prefix", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin || mod) {
                boolean isValid = true;

                IRole role;
                try {
                    role = event.getMessage().getRoleMentions().get(0);
                } catch (IndexOutOfBoundsException ex) {
                    try {
                        role = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild());
                    } catch (IndexOutOfBoundsException e) {
                        isValid = false;
                        role = event.getGuild().getEveryoneRole();
                        UTILS.log("Please indicate a role!\nE.g. " + EventRunner.prefix + "prefix @role <Prefix> __OR__ " + EventRunner.prefix + "prefix {role} <>", false, event.getChannel());

                    }
                }
                if (isValid) {
                    List<List<String>> old = DATABASE.getSuffixPrefix(event.getAuthor().getRolesForGuild(event.getGuild()), event.getGuild());
                    DATABASE.rolePrefixAdd(event);
                    UTILS.sendEmbed("**__Prefix Addition for__ " + role.mention() + "**\n\nExample: **" + UTILS.getArguement(event.getMessage(), "<", ">") + "**EasternGamer", event, DATABASE);

                    event.getGuild().getUsersByRole(role).forEach((user) -> UTILS.updatePrefixSuffix(user, event.getGuild(), old, DATABASE));
                }
            } else {
                UTILS.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("suffix", (event, DATABASE, UTILS, owner, admin, mod) -> {

            if (owner || admin || mod) {

                boolean isValid = true;

                IRole role;
                try {
                    role = event.getMessage().getRoleMentions().get(0);
                } catch (IndexOutOfBoundsException ex) {
                    try {
                        role = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild());
                    } catch (IndexOutOfBoundsException e) {
                        isValid = false;
                        role = event.getGuild().getEveryoneRole();
                        UTILS.log("Please indicate a role!\nE.g. " + EventRunner.prefix + "suffix @role <Suffix> __OR__ " + EventRunner.prefix + "suffix {role} <>", false, event.getChannel());
                    }
                }
                if (isValid) {

                    List<List<String>> old = DATABASE.getSuffixPrefix(event.getAuthor().getRolesForGuild(event.getGuild()), event.getGuild());
                    DATABASE.roleSuffixAdd(event);
                    UTILS.sendEmbed("**__Suffix Addition for__ " + role.mention() + "**\n\nExample: " + "EasternGamer**" + UTILS.getArguement(event.getMessage(), "<", ">") + "**", event, DATABASE);

                    event.getGuild().getUsersByRole(role).forEach((user) -> UTILS.updatePrefixSuffix(user, event.getGuild(), old, DATABASE));
                }
            } else {
                UTILS.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDSMOD.put("analyze", (event, DATABASE, UTILS, owner, admin, mod) -> {
            if (owner || admin || mod) {

                int m = UTILS.getMaxPosition(bot, event);

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
                    if (boolList.get(g)) {
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
                String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());
                if (DATABASE.isEnabled(tableName, "PRIVATE_MESSAGE")) {
                    sendPM = true;
                }
                build.append("\n**__Can Send Welcome and Leave Messages?__**\n\n");
                List<Permissions> perm = new ArrayList<>();

                event.getGuild().getDefaultChannel().getModifiedPermissions(bot.getOurUser())
                        .parallelStream()
                        .filter(permission -> permission.equals(Permissions.SEND_MESSAGES))
                        .forEach(perm::add);
                if (perm.contains(Permissions.SEND_MESSAGES)) {
                    build.append("\u200B \u200B *Yes!* \u200B \u200B \u200B \u200B :white_check_mark:\n");
                } else {
                    build.append("\u200B \u200B *No!* \u200B \u200B \u200B \u200B :negative_squared_cross_mark: \n \u200B \u200B Allow the bot to send messages!\n");
                }
                build.append("\n**__Inaccessible Roles__**\n\n");

                if (!sendPM) {
                    event.getGuild().getRoles()
                            .stream()
                            .filter(role -> role.getPosition() > m)
                            .forEachOrdered(role -> build.append("\u200B \u200B").append(role.mention()).append("\n"));
                } else {
                    event.getGuild().getRoles()
                            .stream()
                            .filter(role -> role.getPosition() > m)
                            .forEachOrdered(role -> build.append("\u200B \u200B").append(role.getName()).append("\n"));
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.withTitle("Analyzation Complete!");

                embed.withColor(16729620);
                embed.appendDesc(build.toString() + "\n\n\n **NOTE:**\n``Inaccessible Roles`` means that the bot cannot apply the roles, cannot change a users name who has these roles or remove those roles.\n This is based on the role hierarchy. The greater the position the more roles it can influence.\n Any further issues contact the support server!");
                if (!sendPM) {
                    UTILS.sendMessage(embed, event, DATABASE);
                } else {
                    RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(embed.build()));
                }
            } else {
                UTILS.log("Requires Mod Role or higher to use!", false, event.getChannel());
            }
        });
        COMMANDS.put("list role", (event, DATABASE, UTILS, owner, admin, mod) -> {
            List<IRole> finalRoles = new ArrayList<>();
            try {
                List<IRole> roles = event.getGuild().getRoles();

                List<List<String>> superRole = new ArrayList<>();
                superRole.add(new ArrayList<>());
                superRole.add(new ArrayList<>());
                superRole.add(new ArrayList<>());

                Statement statement = DATABASE.getConnection().createStatement();
                ResultSet rs = statement.executeQuery("select * from " + DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()));
                while (rs.next()) {
                    superRole.get(0).add(rs.getString(1));
                    superRole.get(1).add(rs.getString(2));
                    superRole.get(2).add(rs.getString(3));
                }
                roles.stream()
                        .filter(role -> UTILS.equalsAny(role.getStringID(), superRole.get(0)))
                        .forEach(finalRoles::add);
                EmbedBuilder build = new EmbedBuilder();
                for (int l = 0; l < finalRoles.size(); l++) {
                    StringBuilder name = new StringBuilder();
                    for (int k = 0; k < finalRoles.size() + 1; k++) {
                        if (superRole.get(0).get(k).equals(finalRoles.get(l).getStringID())) {
                            try {
                                if (!superRole.get(1).get(k).contains("null")) {
                                    name.append(superRole.get(1).get(k));
                                }
                            } catch (NullPointerException ignored) {
                            }
                            name.append("EasternGamer");
                            try {
                                if (!superRole.get(2).get(k).contains("null")) {
                                    name.append(superRole.get(2).get(k));
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }
                    try {
                        build.appendField(name.toString(), finalRoles.get(l).mention(), true);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                build.withTitle("**Prefixes and Suffixes**");
                build.withColor(16729620);
                UTILS.sendMessage(build, event, DATABASE);
                finalRoles.clear();
            } catch (SQLException ignored) {
            }
        });
        COMMANDS.put("list settings", (event, DATABASE, UTILS, owner, admin, mod) -> {

            String tableName = DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName());

            String prefix = DATABASE.getSingleEntry(tableName, "CPREFIX");
            String adminId = DATABASE.getSingleEntry(tableName, "ADMIN_ID");
            String modId = DATABASE.getSingleEntry(tableName, "MOD_ID");
            String welcomeMessage = DATABASE.getSingleEntry(tableName, "WELCOME_MESSAGE");
            String leaveMessage = DATABASE.getSingleEntry(tableName, "LEAVE_MESSAGE");
            boolean enabled = DATABASE.isEnabled(tableName, "WELCOME_LEAVE");
            boolean pm = DATABASE.isEnabled(tableName, "PRIVATE_MESSAGE_ENABLED");
            boolean autorole = DATABASE.isEnabled(tableName, "AUTOROLE_ENABLED");
            boolean onerole = DATABASE.isEnabled(tableName, "ONE_ROLE");
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
            String build = "```css\nAdministration Settings\n```" +
                    "\u200B \u200B \u2022 **Admin Role** ( " + adminS + ")\n" +
                    "\u200B \u200B \u2022 **Mod Role** (" + modS + ")\n" +
                    "\n" +
                    "```css\nGeneral Settings\n```" +
                    "\u200B \u200B \u2022 **Command Prefix** (``" + prefix + "``)\n" +
                    "\u200B \u200B \u2022 **Welcome & Leave** (``" + enabled + "``)\n" +
                    "\u200B \u200B \u2022 **PM's Enabled** (``" + pm + "``)\n" +
                    "\u200B \u200B \u2022 **Autoroles Enabled** (``" + autorole + "``)\n" +
                    "\u200B \u200B \u2022 **Single Prefixes and Suffixes Enabled** (``" + onerole + "``)\n" +
                    "\n" +
                    "\u200B \u200B \u200B - **Welcome Message** (``" + welcomeMessage + "``)\n" +
                    "\u200B \u200B \u200B - **Leave Message** (``" + leaveMessage + "``)\n";
            UTILS.sendEmbed(build, event, DATABASE);
        });
        COMMANDS.put("commands", (event, DATABASE, UTILS, owner, admin, mod) -> {
            EmbedBuilder build = new EmbedBuilder();
            StringBuilder builder = new StringBuilder();

            if (owner) {
                builder.append("```css\nOwner\n```");
                builder.append(" - **!").append("phoenix-reset**\n");
                builder.append(" - **").append(EventRunner.prefix).append("set admin** @role\n");
                builder.append(" - **").append(EventRunner.prefix).append("bot leave**\n");
                admin = true;
            }
            if (admin) {
                builder.append("```css\nAdministrator\n```");
                builder.append(" - **").append(EventRunner.prefix).append("set cprefix** $$\n");
                builder.append(" - **").append(EventRunner.prefix).append("set mod** @role\n");
                builder.append(" - **").append(EventRunner.prefix).append("set welcome** <#mention# #user# is cool>\n");
                builder.append(" - **").append(EventRunner.prefix).append("set leave** <#user# is leaving>\n");
                builder.append(" - **").append(EventRunner.prefix).append("set welcomeleave** true/false\n");
                builder.append(" - **").append(EventRunner.prefix).append("set autorole** true/false\n");
                builder.append(" - **").append(EventRunner.prefix).append("autorole** @role true/false\n");
                builder.append(" - **").append(EventRunner.prefix).append("role assign** @user @user @role @role\n");
                builder.append(" - **").append(EventRunner.prefix).append("role remove** @user @user @role @role\n");
                builder.append(" - **").append(EventRunner.prefix).append("clear** 1..100\n");
                builder.append(" - **").append(EventRunner.prefix).append("restore**\n");
                builder.append(" - **").append(EventRunner.prefix).append("logs**\n");
                mod = true;
            }
            if (mod) {
                builder.append("```css\nModerator\n```");
                builder.append(" - **").append(EventRunner.prefix).append("set single** true/false\n");
                builder.append(" - **").append(EventRunner.prefix).append("set private** true/false\n");
                builder.append(" - **").append(EventRunner.prefix).append("poll** <Title> <Option 1>...<Option 9>\n");
                builder.append(" - **").append(EventRunner.prefix).append("prefix** <content> @role\n");
                builder.append(" - **").append(EventRunner.prefix).append("suffix** <content> @role\n");
                builder.append(" - **").append(EventRunner.prefix).append("analyze**\n");
            }
            builder.append("```css\nGeneral\n```");
            builder.append(" - **").append(EventRunner.prefix).append("list settings**\n");
            builder.append(" - **").append(EventRunner.prefix).append("list role**\n");
            builder.append(" - **").append(EventRunner.prefix).append("stats server**\n");
            builder.append(" - **").append(EventRunner.prefix).append("stats user**\n");
            builder.append(" - **").append(EventRunner.prefix).append("stats**\n");
            builder.append(" - **").append(EventRunner.prefix).append("latency**\n");
            builder.append(" - **").append(EventRunner.prefix).append("suggest**\n");
            builder.append(" - **").append(EventRunner.prefix).append("donate**\n");

            build.withColor(16729620);
            build.withTitle("Command List");
            build.appendDesc(builder.toString());
            UTILS.sendMessage(build, event, DATABASE);
        });
        COMMANDS.put("latency", (event, DATABASE, UTILS, owner, admin, mod) -> RequestBuffer.request(() -> {

            EmbedBuilder ping = new EmbedBuilder();
            ping.withColor(3553598);
            ping.appendDescription("Ping!");

            EmbedBuilder pong = new EmbedBuilder();
            pong.withColor(3553598);
            pong.appendDescription("Pong!");

            long timeStart = System.nanoTime();
            IMessage message = event.getChannel().sendMessage(ping.build());
            message.edit(pong.build());

            double timeEnd = (System.nanoTime() - timeStart) / 2000000.0;
            timeEnd = Math.round(timeEnd * 100) / 100.0;

            EmbedBuilder build = new EmbedBuilder();
            build.withColor(3553598);
            build.appendDescription(":signal_strength: ``" + timeEnd + " ms``");
            UTILS.sendMessage(build, event, DATABASE);
            message.delete();
        }));
        COMMANDS.put("stats server", (event, DATABASE, UTILS, owner, admin, mod) -> {

            String guildName = event.getGuild().getOwner().getName();
            String guildID = event.getGuild().getStringID();

            String iconURL = event.getGuild().getIconURL();

            int memberCount = event.getGuild().getTotalMemberCount();

            StringBuilder builder = new StringBuilder();
            IRole donRole = bot.getRoleByID(481452254067556352L);
            IRole ultDonRole = bot.getRoleByID(488060037483200561L);

            IUser user = event.getGuild().getOwner();
            if (user.getRolesForGuild(bot.getGuildByID(420933608358805536L)).contains(donRole)) {
                builder.append("<:donator:488281487611789322> **").append(event.getGuild().getName()).append("**\n");
            } else if (user.getRolesForGuild(bot.getGuildByID(420933608358805536L)).contains(ultDonRole)) {
                builder.append("<:ultradonator:488281488002121738> **").append(event.getGuild().getName()).append("**\n");
            } else {
                builder.append("**").append(event.getGuild().getName()).append("**\n");
            }
            builder.append("\u200B \u200B \u2022 **Guild ID:** ").append(guildID)
                    .append("\n\u200B \u200B \u2022 **Member Count:** ").append(memberCount)
                    .append("\n\u200B \u200B \u2022 **Creation Date:** ").append(StringUtils.remove(event.getGuild().getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[2])
                    .append('/')
                    .append(StringUtils.remove(event.getGuild().getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[1])
                    .append('/')
                    .append(StringUtils.remove(event.getGuild().getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[0])
                    .append("\n\u200B \u200B \u2022 **Guild Owner: **").append(guildName);
            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(16729620);
            embed.withThumbnail(iconURL);
            embed.appendDesc(builder.toString());
            UTILS.sendMessage(embed, event, DATABASE);
        });
        COMMANDS.put("stats user", (event, DATABASE, UTILS, owner, admin, mod) -> {

            EmbedBuilder embed = new EmbedBuilder();
            StringBuilder builder = new StringBuilder();
            if (!event.getMessage().getMentions().isEmpty()) {
                embed.withColor(16729620);

                IUser user = event.getMessage().getMentions().get(0);
                embed.withThumbnail(user.getAvatarURL());

                IRole donRole = bot.getRoleByID(481452254067556352L);
                IRole ultDonRole = bot.getRoleByID(488060037483200561L);

                if (user.getRolesForGuild(bot.getGuildByID(420933608358805536L)).contains(donRole)) {
                    builder.append("<:donator:488281487611789322> **").append(user.getName()).append("**\n");
                } else if (user.getRolesForGuild(bot.getGuildByID(420933608358805536L)).contains(ultDonRole)) {
                    builder.append("<:ultradonator:488281488002121738> **").append(user.getName()).append("**\n");
                } else {
                    builder.append("**").append(user.getName()).append("**\n");
                }
                builder.append("\u200B \u200B \u2022 **User ID:** ").append(user.getStringID()).append("\n");
                builder.append("\u200B \u200B \u2022 **Status:** ").append(user.getPresence().getStatus()).append("\n");
                builder.append("\u200B \u200B \u2022 **Creation Date:** ")
                        .append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[2])
                        .append('/')
                        .append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[1])
                        .append('/')
                        .append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[0]);

                embed.appendDesc(builder.toString());
                UTILS.sendMessage(embed, event, DATABASE);
            } else {
                embed.withColor(16729620);

                IUser user = event.getAuthor();
                embed.withThumbnail(user.getAvatarURL());

                IRole donRole = bot.getRoleByID(481452254067556352L);
                IRole ultDonRole = bot.getRoleByID(488060037483200561L);

                if (user.getRolesForGuild(bot.getGuildByID(420933608358805536L)).contains(donRole)) {
                    builder.append("<:donator:488281487611789322> **").append(user.getName()).append("**\n");
                } else if (user.getRolesForGuild(bot.getGuildByID(420933608358805536L)).contains(ultDonRole)) {
                    builder.append("<:ultradonator:488281488002121738> **").append(user.getName()).append("**\n");
                } else {
                    builder.append("**").append(user.getName()).append("**\n");
                }
                builder.append("\u200B \u200B \u2022 **User ID:** ").append(user.getStringID()).append("\n");
                builder.append("\u200B \u200B \u2022 **Status:** ").append(user.getPresence().getStatus()).append("\n");
                builder.append("\u200B \u200B \u2022 **Creation Date:** ")
                        .append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[2])
                        .append('/')
                        .append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[1])
                        .append('/')
                        .append(StringUtils.remove(user.getCreationDate().truncatedTo(ChronoUnit.DAYS).toString(), "T00:00:00Z").split("-")[0]);

                embed.appendDesc(builder.toString());
                UTILS.sendMessage(embed, event, DATABASE);
            }
        });
        COMMANDS.put("stats", (event, DATABASE, UTILS, owner, admin, mod) -> {
            Runtime runtime = Runtime.getRuntime();

            double memoryTotal = runtime.totalMemory() / 1024.0 / 1024.0;
            double memoryFree = runtime.freeMemory() / 1024.0 / 1024.0;
            double memory = memoryTotal - memoryFree;

            long time = EventRunner.START_TIME;
            long timeEnd = System.currentTimeMillis() / 1000;

            String dayS;
            String hourS;
            String minuteS;
            String secondS;

            int days = (int) ((timeEnd - time) / 60 / 60 / 24);
            dayS = days + "";
            if (days < 10) {
                dayS = "0" + days;
            }

            int hours = (int) ((timeEnd - time) / 60 / 60) - (days * 24);
            hourS = hours + "";
            if (hours < 10) {
                hourS = "0" + hours;
            }

            int minutes = (int) (((timeEnd - time) / 60) - (hours * 60) - (days * 24 * 60));
            minuteS = minutes + "";
            if (minutes < 10) {
                minuteS = "0" + minutes;
            }

            int seconds = (int) ((timeEnd - time) - (minutes * 60) - (hours * 60 * 60) - (days * 24 * 60 * 60));
            secondS = seconds + "";
            if (seconds < 10) {
                secondS = "0" + seconds;
            }

            String collection = dayS + ":" + hourS + ":" + minuteS + ":" + secondS;

            UTILS.sendEmbed(":computer: **Version:** " + EventRunner.VERSION
                    + "\n:floppy_disk: **Memory Usage:** " + memory + " MB"
                    + "\n:file_folder: **Messages Proccessed: **" + EventRunner.messagesProcessed
                    + "\n:open_file_folder: **Commands & Names Processed: **" + EventRunner.processed
                    + "\n:stopwatch: **Uptime:** " + collection
                    + "\n\n\u200B \u2022  **Server Count: **" + event.getClient().getGuilds().size()
                    + "\n\u200B \u2022 **User Count: **" + event.getClient().getUsers().size()
                    + "\n\n\n:ballot_box: **Upvote This bot:** https://discordbots.org/bot/439454842071547905/vote"
                    + "\n:telephone: **Support Server:** https://discord.gg/RuK3Sw6", event, DATABASE);
        });
        COMMANDS.put("help", (event, DATABASE, UTILS, owner, admin, mod) -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.withColor(16729620);
            embed.appendDesc("Tutorial Video: https://youtu.be/5mZIA1zqxuo \nCommand List can be found with ``" + EventRunner.prefix + "commands``\n\nDisclaimer: If bot is not working as advertised type " + EventRunner.prefix + "analyze");
            UTILS.sendMessage(embed, event, DATABASE);
        });
        COMMANDS.put("suggest", (event, DATABASE, UTILS, owner, admin, mod) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorName(event.getAuthor().getName());
            builder.withAuthorIcon(event.getAuthor().getAvatarURL());
            builder.withTitle(event.getGuild().getName());
            builder.withThumbnail(event.getGuild().getIconURL());
            builder.appendDesc(event.getMessage().getContent().replace(EventRunner.prefix + "suggest", ""));
            builder.withColor(16729620);
            RequestBuffer.request(() -> bot.getApplicationOwner().getOrCreatePMChannel().sendMessage(builder.build()));

            UTILS.sendEmbed("Feedback Successfully Sent!", event, DATABASE);
        });
        COMMANDS.put("donate", (event, DATABASE, UTILS, owner, admin, mod) -> UTILS.sendEmbed("Head to my Patreon to donate or to DiscordBot.io!"
                + "\n\u200B \u2022**Patreon:** https://www.patreon.com/phoenixbot"
                + "\n\u200B \u2022**Discord Bot:** https://donatebot.io/checkout/420933608358805536", event, DATABASE));
        COMMANDS.put("test", (event, DATABASE, UTILS, owner, admin, mod) -> {
            new EventRunner().onUserJoin(new UserJoinEvent(event.getGuild(), event.getAuthor(), Instant.now()));
        });

    }
}

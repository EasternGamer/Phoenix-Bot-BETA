/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core.api;

import net.rising.core.EventRunner;
import net.rising.file.EFile;
import net.rising.ui.MainUI;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @author crysi
 */
public class Utils {

    private static final String[] ALPHABET_ARRAY = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            ".", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "%"};

    private static final String[] ERROR_ARRAY = {"[", "]", "{", "}", "|", "/", "\\", ";", ":", "`", "!", "@", "#", "%", "^", ">", "<", "?", "*", ",", "(", ")", "&", "$", "~"};

    public static boolean containsAny(final CharSequence cs, final CharSequence... searchCharSequences) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchCharSequences)) {
            return false;
        }
        for (final CharSequence searchCharSequence : searchCharSequences) {
            if (StringUtils.contains(cs.toString().toLowerCase(), searchCharSequence)) {
                return true;
            }
        }
        return false;
    }

    String abbreviate(String name) {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.isAsciiPrintable(name)) {
            String[] abbreviation = name.split(" ");
            char thing;
            for (String anAbbreviation : abbreviation) {
                if (equalsAny(String.valueOf(anAbbreviation.charAt(0)), Arrays.asList(ERROR_ARRAY))) {
                    thing = 'n';
                } else {
                    thing = anAbbreviation.charAt(0);
                }
                builder.append(thing);
            }
        }
        return builder.toString();
    }

    public String getArguement(IMessage message, String splitter, String removeSplit) {
        String arguement;
        if (!message.getRoleMentions().isEmpty()) {
            try {
                arguement = Arrays.asList(StringUtils.split(Arrays.asList(StringUtils.split(message.getContent(), splitter)).get(2), removeSplit)).get(0);
            } catch (IndexOutOfBoundsException ex) {
                arguement = "";
            }
        } else {
            try {
                arguement = Arrays.asList(StringUtils.split(Arrays.asList(StringUtils.split(message.getContent(), splitter)).get(1), removeSplit)).get(0);
            } catch (IndexOutOfBoundsException ex) {
                arguement = "";
            }
        }
        return arguement;
    }

    public IRole getRole(String roleName, IGuild guild) throws IndexOutOfBoundsException {
        return guild.getRolesByName(roleName).get(0);
    }

    String getRandomKey() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int j = 0; j < 30; j++) {
            builder.append(ALPHABET_ARRAY[random.nextInt(46)]);
        }
        return builder.toString();
    }

    private String timeNow() {
        Date dateBase = new Date();
        String[] dataDate = dateBase.toString().split(" ");
        return dataDate[3];
    }

    private String dateNow() {
        Date date = new Date();
        String[] dataDate = date.toString().split(" ");
        StringBuilder time = new StringBuilder();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        time.append(dataDate[2]);
        time.append("-");
        for (int z = 1; z <= 12; z++) {
            if (dataDate[1].equals(months[z - 1])) {
                if (z < 10) {
                    time.append(0);
                    time.append(z);
                } else {
                    time.append(z);
                }
            }
        }
        time.append("-");
        time.append(dataDate[5]);
        return time.toString();
    }

    public void log(String object, boolean bool, IChannel channel) {
        if (bool) {
            System.out.println("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][INTERNAL] " + object);
            EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Logs\\", dateNow() + ".log");
            file.getWriter().append("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][INTERNAL] " + object);
        } else {
            EmbedBuilder build = new EmbedBuilder();
            build.appendDesc("**ERROR: **" + object);
            build.withColor(3553598);
            RequestBuffer.request(() -> channel.sendMessage(build.build()));
            System.out.println("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][EXTERNAL] " + object);
            EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Logs\\", dateNow() + ".log");
            file.getWriter().append("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][EXTERNAL] " + object);
        }
    }

    public String getFormattedContent(IMessage message) {
        IDiscordClient bot = MainUI.bot;
        StringBuilder formattedMessage = new StringBuilder();

        char[] character = message.getContent().toCharArray();
        for (int j = 0; j < character.length; j++) {
            if (character[j] == '<' && character[j + 1] == '@') {
                if (character[j + 2] == '&') {
                    StringBuilder roleID = new StringBuilder();
                    int h = j;
                    for (int i = 1; i < 19; i++) {
                        try {
                            roleID.append(character[h + 2 + i]);
                            j++;
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                    formattedMessage.append('@').append(bot.getRoleByID(Long.parseLong(roleID.toString())).getName());
                    j += 3;
                } else if (character[j + 2] == '!') {
                    StringBuilder userID = new StringBuilder();
                    int h = j;
                    for (int i = 1; i < 19; i++) {
                        try {
                            userID.append(character[h + 2 + i]);
                            j++;
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                    formattedMessage.append('@').append(bot.getUserByID(Long.parseLong(userID.toString())).getName());
                    j += 3;
                }
            } else {
                formattedMessage.append(character[j]);
            }
        }
        return formattedMessage.toString();
    }

    public void log(String object, IChannel channel, IUser user, Database database) {

        System.out.println("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + user.getName() + "][INTERNAL] " + object);
        EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Server_Logs\\", database.getTableName(channel.getGuild().getStringID(), channel.getGuild().getName()) + ".log");
        file.getWriter().append("[" + dateNow() + "][" + timeNow() + "][" + channel.getGuild().getName() + "][" + user.getName() + "][INTERNAL] " + object);
    }

    public void processClear(String[] arguements, MessageReceivedEvent event, IChannel channel, Database database) {
        String[] argStep = StringUtils.split(arguements[1], "-");
        try {
            int numOne = Integer.parseInt(argStep[0]);
            int numTwo = Integer.parseInt(argStep[1]);

            MessageHistory msgHist = channel.getMessageHistory(numTwo + numOne);
            List<IMessage> msgHistP = msgHist.subList(numOne, numTwo + 1);
            RequestBuffer.request(() -> channel.bulkDelete(msgHistP));

            RequestBuffer.request(() -> event.getMessage().delete());

            StringBuilder builder = new StringBuilder();
            MessageHistory mh = new MessageHistory(msgHistP);

            for (int j = 0; j < numOne; j++) {
                try {
                    builder.append("[").append(mh.get(j).getCreationDate().toString()).append("]").append(mh.get(j).getAuthor().getName()).append(": ").append(mh.get(j).getContent()).append('\n');
                } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
                }
            }
            EFile file = new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Phoenix Bot\\Restores\\", database.getTableName(channel.getGuild().getStringID(), channel.getGuild().getName()) + ".txt");
            file.getWriter().setContent(builder.toString());
        } catch (IllegalArgumentException e) {
            log("An error occured while decoding your numbers!", false, channel);
        }
    }

    public int getMaxPosition(IDiscordClient bot, MessageReceivedEvent event) {
        int m = 0;
        for (int a = 0; a < bot.getOurUser().getRolesForGuild(event.getGuild()).size(); a++) {
            if (bot.getOurUser().getRolesForGuild(event.getGuild()).get(a).getPosition() > m) {
                m = bot.getOurUser().getRolesForGuild(event.getGuild()).get(a).getPosition();
            }
        }
        return m;
    }

    public boolean sendEmbed(String message, MessageReceivedEvent event, Database database, boolean isWarn) {
        isWarn = false;
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(3553598);
        embed.appendDescription(message);
        sendMessage(embed, event, database);
        return isWarn;
    }

    public void sendEmbed(String message, MessageReceivedEvent event, Database database) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(16729620);
        embed.appendDescription(message);
        sendMessage(embed, event, database);
    }

    public List<IRole> getRoleList(List<List<String>> listOfTitles, List<IRole> newRoles) {
        List<IRole> roleList = new ArrayList<>();
        for (int i = 0; i < listOfTitles.get(5).size(); i++) {
            if (!StringUtils.isBlank(listOfTitles.get(3).get(i))) {
                for (IRole newRole : newRoles) {
                    if (listOfTitles.get(5).get(i).equals(newRole.getStringID())) {
                        roleList.add(newRole);
                    }
                }
            }
        }
        return roleList;
    }

    public void updatePrefixSuffix(IUser user, IGuild guild, List<List<String>> listOfOldTitles,Database database) {
        if (!user.equals(guild.getOwner())) {
            String tableName = database.getTableName(guild.getStringID(), guild.getName());
            List<List<String>> listOfTitles = database.getSuffixPrefix(user.getRolesForGuild(guild), guild);
            StringBuilder builder = new StringBuilder();

            List<IRole> newRoles = user.getRolesForGuild(guild);
            if (!database.isEnabled(tableName, "ONE_ROLE")) {
                listOfTitles.get(3)
                        .stream()
                        .filter(title -> !StringUtils.isBlank(title) && !StringUtils.equals(title, "null"))
                        .forEachOrdered(builder::append);
            } else {
                List<IRole> roleList = getRoleList(listOfTitles, newRoles);
                AtomicReference<IRole> role = new AtomicReference<>(guild.getEveryoneRole());
                AtomicInteger size = new AtomicInteger();
                for (IRole aRoleList : roleList) {
                    if (aRoleList.getPosition() > size.get()) {
                        size.set(aRoleList.getPosition());
                        role.set(aRoleList);
                    }
                }

                String roleId = role.get().getStringID();
                for (int i = 0; i < listOfTitles.get(5).size(); i++) {
                    if (listOfTitles.get(5).get(i).equals(roleId)) {
                        if (!StringUtils.isEmpty(listOfTitles.get(3).get(i)) && !listOfTitles.get(3).get(i).equals("null")) {
                            builder.append(listOfTitles.get(3).get(i));
                        }
                    }
                }
            }
            builder.append(getUsername(user.getDisplayName(guild), listOfOldTitles, listOfTitles));
            if (!database.isEnabled(tableName, "ONE_ROLE")) {
                listOfTitles.get(4)
                        .stream()
                        .filter(title -> !StringUtils.isBlank(title) && !StringUtils.equals(title, "null"))
                        .forEachOrdered(builder::append);
            } else {
                List<IRole> roleList = getRoleList(listOfTitles, newRoles);
                AtomicReference<IRole> role = new AtomicReference<>(guild.getEveryoneRole());
                AtomicInteger size = new AtomicInteger();
                for (IRole aRoleList : roleList) {
                    if (aRoleList.getPosition() > size.get()) {
                        size.set(aRoleList.getPosition());
                        role.set(aRoleList);
                    }
                }

                String roleId = role.get().getStringID();
                for (int i = 0; i < listOfTitles.get(5).size(); i++) {
                    if (listOfTitles.get(5).get(i).equals(roleId)) {
                        if (!StringUtils.isEmpty(listOfTitles.get(4).get(i)) && !listOfTitles.get(4).get(i).equals("null")) {
                            builder.append(listOfTitles.get(4).get(i));
                        }
                    }
                }
            }
            RequestBuffer.request(() -> {
                try {
                    guild.setUserNickname(user, builder.toString());
                    EventRunner.processed++;
                } catch (DiscordException | MissingPermissionsException e) {
                    log(e.getMessage(), true, guild.getDefaultChannel());
                }
            });
        }

    }

    public boolean equalsAny(String searchString, List<String> listOfItems) {
        boolean done = false;
        for (String listOfItem : listOfItems) {
            if (searchString.contains(listOfItem)) {
                done = true;
            }
        }
        return done;
    }

    /*
        public void sendMessage(String message, MessageReceivedEvent event) {

            if (DATABASE.isEnabled(DATABASE.getTableName(event.getGuild().getStringID(), event.getGuild().getName()), "PRIVATE_MESSAGE_ENABLED")) {
                RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(message));
            } else {

                RequestBuffer.request(() -> event.getChannel().sendMessage(message));
            }
        }
    */
    public String getUsername(String name, List<List<String>> listOfOldTitles, List<List<String>> listOfNewTitles) {
        for (int k = 1; k < listOfOldTitles.size(); k++) {
            for (int i = 0; i < listOfOldTitles.get(k).size(); i++) {
                name = StringUtils.remove(name, listOfOldTitles.get(k).get(i));
            }
        }
        for (int k = 1; k < listOfNewTitles.size(); k++) {
            for (int i = 0; i < listOfNewTitles.get(k).size(); i++) {
                name = StringUtils.remove(name, listOfNewTitles.get(k).get(i));
            }
        }
        return name;
    }

    public void sendMessage(EmbedBuilder message, MessageReceivedEvent event, Database database) {
        try {
            if (database.isEnabled(database.getTableName(event.getGuild().getStringID(), event.getGuild().getName()), "PRIVATE_MESSAGE_ENABLED")) {
                if (!StringUtils.contains(message.build().description, "<@") && !StringUtils.contains(message.build().description, "<!")) {
                    RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(message.build()));
                } else {
                    RequestBuffer.request(() -> {
                        try {
                            event.getChannel().sendMessage(message.build());
                        } catch (MissingPermissionsException | DiscordException ee) {
                            try {
                                event.getAuthor().getOrCreatePMChannel().sendMessage(message.build());
                            } catch (DiscordException eee) {
                                log(eee.getMessage(), true, event.getChannel());
                            }
                        }
                    });
                }
            } else {
                RequestBuffer.request(() -> {
                    try {
                        event.getChannel().sendMessage(message.build());
                    } catch (MissingPermissionsException | DiscordException ee) {
                        try {
                            event.getAuthor().getOrCreatePMChannel().sendMessage(message.build());
                        } catch (DiscordException eee) {
                            log(eee.getMessage(), true, event.getChannel());
                        }
                    }
                });
            }
        } catch (NullPointerException ex) {
            message.withFooterText("An Error occured! Please reset settings!");
            RequestBuffer.request(() -> {
                try {
                    event.getChannel().sendMessage(message.build());
                } catch (MissingPermissionsException | DiscordException ee) {
                    try {
                        event.getAuthor().getOrCreatePMChannel().sendMessage(message.build());
                    } catch (DiscordException eee) {
                        log(eee.getMessage(), true, event.getChannel());
                    }
                }
            });
        }
    }
}

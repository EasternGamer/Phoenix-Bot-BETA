/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.rising.core.EventRunner;
import net.rising.file.EFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author crysi
 */
public class Special {

    private static final String[] ALPHABETARRAY = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        ".", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "%"};

    private static final String[] ERRORARRAY = {"[", "]", "{", "}", "|", "/", "\\", ";", ":", "`", "!", "@", "#", "%", "^", ">", "<", "?", "*", ",", "(", ")", "&", "$", "~"};

    public String abbreviate(String name) {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.isAsciiPrintable(name)) {
            List<String> abbreviation = Arrays.asList(name.split(" "));
            char thing;
            for (int z = 0; z < abbreviation.size(); z++) {
                if (equalsAny(String.valueOf(abbreviation.get(z).charAt(0)), Arrays.asList(ERRORARRAY))) {
                    thing = 'n';
                } else {
                    thing = abbreviation.get(z).charAt(0);
                }
                builder.append(thing);
            }
        }
        return builder.toString();
    }

    public String getRandomKey() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int j = 0; j < 30; j++) {
            builder.append(ALPHABETARRAY[random.nextInt(46) + 0]);
        }
        return builder.toString();
    }

    public String timeNow() {
        Date dateBase = new Date();
        String[] dataDate = dateBase.toString().split(" ");
        StringBuilder time = new StringBuilder();
        time.append(dataDate[3]);
        return time.toString();
    }
    
    public String dateNow() {
        Date date = new Date();
        String[] dataDate = date.toString().split(" ");
        StringBuilder time = new StringBuilder();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
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
            EFile file = new EFile("F:\\Server\\Database\\Phoenix Bot\\Logs\\", dateNow() + ".log");
            file.getWriter().append("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][INTERNAL] " + object);
        } else {
            EmbedBuilder build = new EmbedBuilder();
            build.appendDesc("**ERROR: **" + object);
            build.withColor(3553598);
            RequestBuffer.request(() -> channel.sendMessage(build.build()));
            System.out.println("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][EXTERNAL] " + object);
            EFile file = new EFile("F:\\Server\\Database\\Phoenix Bot\\Logs\\", dateNow() + ".log");
            file.getWriter().append("[" + timeNow() + "][" + channel.getGuild().getName() + "][" + channel.getName() + "][EXTERNAL] " + object);
        }
    }

    public void processClear(String[] arguements, IChannel channel) {
        String[] argStep = StringUtils.split(arguements[1], "-");
        try {
            int numOne = Integer.parseInt(argStep[0]);
            int numTwo = Integer.parseInt(argStep[1]);
            MessageHistory msgHist = channel.getMessageHistory(numTwo + numOne);
            List<IMessage> msgHistP = msgHist.subList(numOne - 1, numTwo);
            RequestBuffer.request(() -> channel.bulkDelete(msgHistP));
        } catch (IllegalArgumentException e) {
            log("An error occured while decoding your numbers!", false, channel);
        }
    }

    public void updatePrefixSuffix(IUser user, IGuild guild, List<List<String>> listOfOldTitles) {
        List<List<String>> listOfTitles = getSuffixPrefix(user.getRolesForGuild(guild), guild);
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
        builder.append(getUsername(user.getDisplayName(guild), listOfOldTitles, listOfTitles));
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
                guild.setUserNickname(user, builder.toString());
                EventRunner.processed++;
            } catch (DiscordException | MissingPermissionsException e) {
                log(e.getMessage(), true, guild.getDefaultChannel());
            }
        });

    }

    public List<List<String>> getSuffixPrefix(List<IRole> roles, IGuild guild) {
        Database db = new Database();

        List<List<String>> superRole = new ArrayList<>();
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());

        try (Connection connect = DriverManager.getConnection("jdbc:derby://localhost:1527/PhoenixDatabase", "pro", "profit")) {
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery("select * from " + db.getTableName(guild.getStringID(), guild.getName()));
            while (rs.next()) {
                superRole.get(0).add(rs.getString(1));
                superRole.get(1).add(rs.getString(2));
                superRole.get(2).add(rs.getString(3));
            }

        } catch (SQLException e) {
        }
        for (int i = 0; i < superRole.get(0).size(); i++) {
            for (int z = 0; z < roles.size(); z++) {
                if (superRole.get(0).get(i).equals(roles.get(z).getStringID())) {
                    superRole.get(3).add(superRole.get(1).get(i));
                    superRole.get(4).add(superRole.get(2).get(i));
                }
            }
        }
        return superRole;
    }

    public boolean equalsAny(String searchString, List<String> listOfItems) {
        boolean done = false;
        for (int z = 0; z < listOfItems.size(); z++) {
            if (searchString.contains(listOfItems.get(z))) {
                done = true;
            }
        }
        return done;
    }

    public static boolean containsAny(final CharSequence cs, final CharSequence... searchCharSequences) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchCharSequences)) {
            return false;
        }
        for (final CharSequence searchCharSequence : searchCharSequences) {
            if (contains(cs.toString().toLowerCase(), searchCharSequence)) {
                return true;
            }
        }
        return false;
    }

    public void sendMessage(String message, MessageReceivedEvent e) {
        Database data = new Database();

        if (data.isPMEnabled(e.getGuild().getStringID(), e.getGuild().getName())) {
            e.getAuthor().getOrCreatePMChannel().sendMessage(message);
        } else {
            e.getChannel().sendMessage(message);
        }
    }

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

    public void sendMessage(EmbedBuilder message, MessageReceivedEvent e) {
        Database data = new Database();

        if (data.isPMEnabled(e.getGuild().getStringID(), e.getGuild().getName())) {
            RequestBuffer.request(() -> e.getAuthor().getOrCreatePMChannel().sendMessage(message.build()));
        } else {
            RequestBuffer.request(() -> {
                try {
                    e.getChannel().sendMessage(message.build());
                } catch (MissingPermissionsException | DiscordException ee) {
                    try {
                        e.getAuthor().getOrCreatePMChannel().sendMessage(message.build());
                    } catch (DiscordException eee) {
                        log(eee.getMessage(), true, e.getChannel());
                    }
                }
            });
        }
    }
}

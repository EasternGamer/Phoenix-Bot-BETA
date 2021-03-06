/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core.api;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.RequestBuffer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author crysi
 */
public class Database {

    private static final String URL = "jdbc:hsqldb:file:/Users/Administrator/Desktop/Phoenix Bot Data/Database/PhoenixDatabase;ifexists=true";
    private static final String USER = "pro";
    private static final String PASSWORD = "profit";
    private static final Utils UTILS = new Utils();
    private static Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.exit(0);
        }
    }

    public synchronized void createDatabase(IGuild guild, boolean done) {

        try {
            String key = UTILS.getRandomKey();
            String name = guild.getName();
            String guildID = guild.getStringID();
            String tableName = getTableName(guildID, name);
            String tableCreation = "CREATE TABLE PUBLIC." + tableName + " (ROLE VARCHAR(60), ROLE_PREFIX VARCHAR(100), ROLE_SUFFIX VARCHAR(100), ADMIN_ID VARCHAR(60), MOD_ID VARCHAR(60), WELCOME_LEAVE VARCHAR(5), CPREFIX VARCHAR(50), PRIVATE_MESSAGE_ENABLED VARCHAR(5), WELCOME_MESSAGE VARCHAR(1000), LEAVE_MESSAGE VARCHAR(1000), AUTOROLE_IDS VARCHAR(100), AUTOROLE_ENABLED VARCHAR(5), ONE_ROLE VARCHAR(5), AUTOROLE_IDS_ENABLED VARCHAR(5))";
            String execute = "INSERT INTO PUBLIC." + tableName + " (ROLE, ROLE_PREFIX, ROLE_SUFFIX, ADMIN_ID, MOD_ID, PRIVATE_MESSAGE_ENABLED, CPREFIX, WELCOME_MESSAGE, LEAVE_MESSAGE, WELCOME_LEAVE, AUTOROLE_IDS, AUTOROLE_ENABLED, ONE_ROLE, AUTOROLE_IDS_ENABLED) VALUES ('null','null','null', 'null', 'null', 'false', '!', 'Welcome to the server #mention#!', 'Goodbye #user#', 'true', 'null', 'false', 'false', 'null')";
            Statement statement = connection.createStatement();
            statement.setMaxRows(0);
            statement.execute("INSERT INTO PUBLIC.LOGINUSER (SERVERID, SERVERKEY) VALUES ('" + guildID + " ', '" + key + "')");
            statement.execute(tableCreation);
            statement.execute(execute);
            connection.commit();
            if (done) {
                RequestBuffer.request(() -> guild.getOwner().getOrCreatePMChannel().sendMessage("```css\nThanks for adding Phoenix Bot!\n```**__IMPORTANT__**\n*The following information is dangerous in the wrong hands!*\n **-** Server ID: __" + guildID + "__\n **-** Login Key: __" + key + "__\n\n**How to setup the bot?**\n **-** !set admin @role *(Admin Role to enable certain Commands)*\n **-** !set mod @role *(Mod Role to enable certain Commands)\n **-** !set cprefix ! *('!' can be replaced with any character(s))* \n**Tutorial: ** https://youtu.be/5mZIA1zqxuo \n\n\n **IF you have recieved this 2 times for the same server please contact EasternGamer at the support Discord!**"));
                UTILS.log("Server Successfully Added: " + guild.getName(), true, guild.getDefaultChannel());
            }
        } catch (SQLException ignored) {
        }
    }

    public void deleteTable(IGuild guild) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE " + getTableName(guild.getStringID(), guild.getName()));
            connection.commit();
            UTILS.log("Server Successfully Removed: " + guild.getName(), true, guild.getDefaultChannel());
        } catch (SQLException ignored) {
        }
    }

    public Database addEntry(String tableName, String columnName, String entry) {
        try {
            boolean used = false;
            boolean isTrue = false;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select " + columnName + " from PUBLIC." + tableName);
            String oldItem = "";

            if (entry.contains("true")) {
                isTrue = true;
            }
            while (rs.next()) {
                try {
                    String search = RegExUtils.removeAll(rs.getString(1), "true");
                    search = RegExUtils.removeAll(search, "false");
                    entry = RegExUtils.removeAll(entry, "true");
                    entry = RegExUtils.removeAll(entry, "false");
                    if (search.equals(entry)) {
                        oldItem = rs.getString(1);
                        used = true;
                    }
                } catch (NullPointerException ignored) {
                }
            }
            if (!used) {
                statement.execute("INSERT INTO PUBLIC." + tableName + " (" + columnName + ", AUTOROLE_IDS_ENABLED) VALUES ('" + entry + "', ' " + isTrue + "')");
                connection.commit();
            }
            if (used) {
                String execution = "UPDATE PUBLIC." + tableName + " SET AUTOROLE_IDS_ENABLED = '" + isTrue + "' WHERE "+ columnName + " = '" + oldItem + "'";
                statement.execute(execution);
                connection.commit();
            }
        } catch (SQLException ignored) {
        }
        return this;
    }

    public Database addColumn(String tableName, String columnName) {
       // try {
           // connection.createStatement().execute("ALTER TABLE PUBLIC." +  tableName + " ADD AUTOROLE_IDS_ENABLED VARCHAR(5)");
       // } catch (SQLException e) {
           // e.printStackTrace();
       // }
        return this;
    }

    public Database renameColumn(String tableName, String oldColumnName, String newColumnName) {

        try {
            connection.createStatement().execute("RENAME COLUMN PUBLIC." + tableName + "." + oldColumnName + " TO " + newColumnName);
        } catch (SQLException ignored) {
        }

        return this;
    }

    public Database renameTable(String oldTable, String newTable) {
        try {
            connection.createStatement().execute("RENAME TABLE PUBLIC." + oldTable + " TO APP." + newTable);
        } catch (SQLException ignored) {
        }
        return this;
    }

    public List<String> getAllEntries(String tableName, String column) {
        List<String> entries = new ArrayList<>();
        try {
            ResultSet result = connection.createStatement().executeQuery("select " + column + " from PUBLIC." + tableName);
            while (result.next()) {
                if (! StringUtils.isEmpty(result.getString(1))) {
                    entries.add(result.getString(1));
                } else {
                    entries.add(null);
                }
            }
        } catch (SQLException ignored) {
        }
        return entries;
    }

    public List<List<String>> getAllEntries(String tableName, String column1, String column2) {
        List<List<String>> entries = new ArrayList<>();
        try {
            entries.add(new ArrayList<>());
            entries.add(new ArrayList<>());
            ResultSet result = connection.createStatement().executeQuery("select " + column1 +", " + column2 + " from PUBLIC." + tableName + " WHERE " + column2 + " = 'true'");
            while (result.next()) {
                if (!StringUtils.isEmpty(result.getString(1))) {
                    entries.get(0).add(result.getString(1));
                    entries.get(1).add(result.getString(2));
                } else {
                    entries.get(0).add(null);
                    entries.get(1).add(null);
                }
            }
        } catch (SQLException ignored) {
        }
        return entries;
    }

    public String getSingleEntry(String tableName, String column) {
        String entry = "";
        try {
            boolean done = false;
            ResultSet result = connection.createStatement().executeQuery("select " + column + " from PUBLIC." + tableName + " WHERE ROLE = 'null'");
            while (result.next() && ! done) {
                entry = result.getString(1);
                done = true;
            }
        } catch (SQLException ex) {
            entry = "";
        }
        return entry;
    }

    public String getTableName(String id, String name) {

        name = UTILS.abbreviate(name);

        String[] searchNumbers = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        String[] replaceNumbers = { "ZERO", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE" };

        String guildID = StringUtils.replaceEachRepeatedly(id, searchNumbers, replaceNumbers);
        guildID = guildID + "___" + name;
        return guildID;
    }

    private String getPreviousItem(Statement st, String tableName, String columnName) {

        String item = null;
        boolean done = false;
        try {
            ResultSet rs = st.executeQuery("select " + columnName + " from PUBLIC." + tableName);
            while (rs.next() && ! done) {
                done = true;
                item = rs.getString(1);
            }
        } catch (SQLException ignored) {
        }
        return item;
    }

    public Database updateEntry(String newInfo, String tableName, String columnName) {
        try {
            Statement statement = connection.createStatement();
            String execution = "UPDATE PUBLIC." + tableName + " SET " + columnName + " = '" + newInfo + "' WHERE ROLE = 'null'";

            statement.execute(execution);
            connection.commit();
        } catch (SQLException ignored) {
        }
        return this;
    }

    public boolean isEnabled(String tableName, String column) {

        String bool = "";
        boolean done = false;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + column + " FROM PUBLIC." + tableName + " WHERE ROLE = 'null'");
            while (resultSet.next() && ! done) {
                bool = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException ignored) {
        }
        done = bool.equals("true");
        return done;
    }

    public List<List<String>> getSuffixPrefix(List<IRole> roles, IGuild guild) {

        List<List<String>> superRole = new ArrayList<>();
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());
        superRole.add(new ArrayList<>());

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from PUBLIC." + getTableName(guild.getStringID(), guild.getName()));
            while (rs.next()) {
                superRole.get(0).add(rs.getString(1));
                superRole.get(1).add(rs.getString(2));
                superRole.get(2).add(rs.getString(3));
            }

        } catch (SQLException ignored) {
        }
        for (int i = 0; i < superRole.get(0).size(); i++) {
            for (IRole role : roles) {
                try {
                    if (superRole.get(0).get(i).equals(role.getStringID())) {
                        superRole.get(5).add(superRole.get(0).get(i));
                        superRole.get(3).add(superRole.get(1).get(i));
                        superRole.get(4).add(superRole.get(2).get(i));
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
        return superRole;
    }

    public void rolePrefixAdd(MessageReceivedEvent event) {
        try {
            boolean used = false;
            String prefixToAssign = UTILS.getArguement(event.getMessage(), "<", ">");
            IRole role;
            try {
                role = event.getMessage().getRoleMentions().get(0);
            } catch (IndexOutOfBoundsException ex) {
                role = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild());
            }
            String databaseName = getTableName(event.getGuild().getStringID(), event.getGuild().getName());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from PUBLIC." + databaseName);
            while (resultSet.next()) {
                try {
                    if (resultSet.getString(1).equals(role.getStringID())) {
                        used = true;
                    }
                } catch (NullPointerException ignored) {
                }
            }
            if (! used) {
                String execution = "INSERT INTO PUBLIC." + databaseName + " (ROLE, ROLE_PREFIX) VALUES ('" + role.getStringID() + "', '" + prefixToAssign + "')";
                statement.execute(execution);
                connection.commit();
            }
            if (used) {
                String execution = "UPDATE PUBLIC." + databaseName + " SET ROLE_PREFIX = '" + prefixToAssign + "' WHERE ROLE = '" + role.getStringID() + "'";
                statement.execute(execution);
                connection.commit();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void roleSuffixAdd(MessageReceivedEvent event) {
        try {
            boolean used = false;
            String suffixToAssign = UTILS.getArguement(event.getMessage(), "<", ">");
            IRole role;
            try {
                role = event.getMessage().getRoleMentions().get(0);
            } catch (IndexOutOfBoundsException ex) {
                role = UTILS.getRole(UTILS.getArguement(event.getMessage(), "{", "}"), event.getGuild());
            }
            String databaseName = getTableName(event.getGuild().getStringID(), event.getGuild().getName());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from PUBLIC." + databaseName);
            while (resultSet.next()) {
                try {
                    if (resultSet.getString(1).equals(role.getStringID())) {
                        used = true;
                    }
                } catch (NullPointerException ignored) {
                }
            }
            if (!used) {
                String execution = "INSERT INTO PUBLIC." + databaseName + " (ROLE, ROLE_SUFFIX) VALUES ('" + role.getStringID() + "', '" + suffixToAssign + "')";
                statement.execute(execution);
                connection.commit();
            }
            if (used) {
                String execution = "UPDATE PUBLIC." + databaseName + " SET ROLE_SUFFIX = '" + suffixToAssign + "' WHERE ROLE = '" + role.getStringID() + "'";
                statement.execute(execution);
                connection.commit();
            }
        } catch (SQLException ignored) {
        }
    }

    public void save() {

        try {
            Statement statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE PUBLIC.SAVEINFO");
            connection.commit();
            Date dateBase = new Date();

            String[] dataDate = dateBase.toString().split(" ");
            String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec" };
            StringBuilder date = new StringBuilder();
            StringBuilder time = new StringBuilder();
            date.append(dataDate[5]);
            date.append("-");
            for (int z = 1; z <= 12; z++) {
                if (dataDate[1].equals(months[z - 1])) {
                    if (z < 10) {
                        date.append(0);
                        date.append(z);
                    } else {
                        date.append(z);
                    }
                }
            }
            date.append("-");
            date.append(dataDate[2]);

            time.append(dataDate[3]);
            statement.execute("INSERT INTO PUBLIC.SaveInfo (DATE, TIME) VALUES ('" + date.toString() + "', '" + time.toString() + "')");
        } catch (SQLException ignored) {
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

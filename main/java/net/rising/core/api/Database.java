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
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author crysi
 */
public class Database {

    private static final String URL = "jdbc:derby://localhost:1527/PhoenixDatabase";
    private static final String USER = "pro";
    private static final String PASSWORD = "profit";
    private static final Special SPECIAL = new Special();

    private static Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            SPECIAL.log("SQL Has failed to connect: Please connect SQL", true, new Channel(null, "Base-Bot", 0, null, "Code", 0, true, 0, null, null));
        }
    }

    public void createDatabase(IGuild guild, boolean done) {

        try {
            String key = SPECIAL.getRandomKey();
            String name = guild.getName();
            String guildID = guild.getStringID();
            String tableName = getTableName(guildID, name);
            String tableCreation = "CREATE TABLE " + tableName + " (ROLE VARCHAR(60), ROLEPREFIX VARCHAR(100), ROLESUFFIX VARCHAR(100), ADMINROLE VARCHAR(60), MODROLE VARCHAR(60), JOINLEAVE VARCHAR(5), CPREFIX VARCHAR(10), PRIVATEMESSAGE VARCHAR(200), JOINMESSAGE VARCHAR(1000), LEAVEMESSAGE VARCHAR(1000))";
            String execute = "INSERT INTO " + tableName + " (ROLE, ROLEPREFIX, ROLESUFFIX, ADMINROLE, MODROLE, PRIVATEMESSAGE, CPREFIX, JOINMESSAGE, LEAVEMESSAGE, JOINLEAVE) VALUES ('null','null','null', 'null', 'null', 'false', '!', 'Welcome to the server #mention#!', 'Goodbye #user#', 'true')";
            Statement statement = connection.createStatement();
            statement.setMaxRows(0);
            statement.execute("INSERT INTO LOGINUSER (SERVERNAME, SERVERKEY) VALUES ('" + guildID + " ', '" + key + "')");
            statement.execute(tableCreation);
            statement.execute(execute);
            if (done == true) {
                RequestBuffer.request(() -> guild.getOwner().getOrCreatePMChannel().sendMessage("```css\nThanks for adding Phoenix Bot!\n```**__IMPORTANT__**\n*The following information is dangerous in the wrong hands!*\n **-** Server ID: __" + guildID + "__\n **-** Login Key: __" + key + "__\n\n**How to setup the bot?**\n **-** !set admin @role *(Admin Role to enable certain Commands)*\n **-** !set mod @role *(Mod Role to enable certain Commands)\n **-** !set cprefix ! *('!' can be replaced with any character(s))* \n**Tutorial: ** https://youtu.be/5mZIA1zqxuo \n\n\n **IF you have recieved this 2 times for the same server please contact EasternGamer at the support Discord!**"));
                System.out.println(guild.getName());
            }
        } catch (SQLException e) {
        }
    }

    public void recreateTable(IGuild newGuild, IGuild oldGuild) {
        try {
            createDatabase(newGuild, false);
            String table = getTableName(oldGuild.getStringID(), oldGuild.getName());
            Statement st = connection.createStatement();
            String admin = getPreviousItem(st, table, 4);
            String mod = getPreviousItem(st, table, 5);
            String joinLeave = getPreviousItem(st, table, 6);
            String join = getPreviousItem(st, table, 9);
            String leave = getPreviousItem(st, table, 10);
            String prefix = getPreviousItem(st, table, 7);
            setAdmin(admin, newGuild);
            setMod(mod, newGuild);
            setJoinLeave(joinLeave, newGuild);
            setJoinMessage(join, newGuild);
            setLeaveMessage(leave, newGuild);
            setPrefix(prefix, newGuild);
            ResultSet rs = st.executeQuery("select * from " + table);
            while (rs.next()) {
                rolePrefixAdd(rs.getString(1), rs.getString(2), newGuild);
                roleSuffixAdd(rs.getString(1), rs.getString(3), newGuild);
            }
        } catch (SQLException ex) {
        }

    }

    public String getTableName(String id, String name) {

        name = SPECIAL.abbreviate(name);

        String[] searchNumbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] replaceNumbers = {"ZERO", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"};

        String guildID = StringUtils.replaceEachRepeatedly(id, searchNumbers, replaceNumbers);
        guildID = guildID + "___" + name;
        return guildID;
    }

    public String getPrefix(String id, String name, IGuild guild) {

        String tableName = getTableName(id, name);
        String prefix = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select CPREFIX from " + tableName);
            boolean done = false;
            while (resultSet.next() && done == false) {
                prefix = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {
        }

        return prefix;
    }

    public String getAdminRole(String id, String name) {

        String adminRole = null;
        boolean done = false;
        String tableName = getTableName(id, name);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select ADMINROLE from " + tableName);
            while (resultSet.next() && done == false) {
                adminRole = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {
        }
        return adminRole;
    }

    public String getModRole(String id, String name) {

        String modRole = null;
        boolean done = false;
        String tableName = getTableName(id, name);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select MODROLE from " + tableName);
            while (resultSet.next() && done == false) {
                modRole = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {
        }
        return modRole;
    }

    public boolean isWLEnabled(String id, String name) {

        String modRole = "";
        boolean done = false;
        String tableName = getTableName(id, name);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select JOINLEAVE from " + tableName);
            while (resultSet.next() && done == false) {
                modRole = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {

        }
        try {
            done = modRole.equals("true");
        } catch (NullPointerException e) {
            done = false;
        }
        return done;
    }

    public boolean isPMEnabled(String id, String name) {

        String bool = "";
        boolean done = false;
        String tableName = getTableName(id, name);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select PRIVATEMESSAGE from " + tableName);
            while (resultSet.next() && done == false) {
                bool = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {
        }
        done = bool.equals("true");
        return done;
    }

    public String getWelcomeMessage(String id, String name) {

        String modRole = null;
        boolean done = false;
        String tableName = getTableName(id, name);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select JOINMESSAGE from " + tableName);
            while (resultSet.next() && done == false) {
                modRole = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {
        }
        return modRole;
    }

    public String getLeaveMessage(String id, String name) {

        String modRole = null;
        boolean done = false;
        String tableName = getTableName(id, name);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select LEAVEMESSAGE from " + tableName);
            while (resultSet.next() && done == false) {
                modRole = resultSet.getString(1);
                done = true;
            }
        } catch (SQLException e) {
        }
        return modRole;
    }

    public String getPreviousItem(Statement st, String tableName, int i) {

        String item = null;
        boolean done = false;
        try {
            ResultSet rs = st.executeQuery("select * from " + tableName);
            while (rs.next() && done == false) {
                done = true;
                item = rs.getString(i);
            }
        } catch (SQLException ex) {
        }
        return item;
    }

    public void setAdmin(MessageReceivedEvent event) {
        try {
            String roleId = event.getMessage().getRoleMentions().get(0).getStringID();
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET ADMINROLE = '" + roleId + "' WHERE ADMINROLE = '" + getPreviousItem(statement, databaseName, 4) + "'";
            statement.execute(execution);
        } catch (IndexOutOfBoundsException e) {
            SPECIAL.log("Please mention a role!", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void setMod(MessageReceivedEvent event) {
        try {
            String roleId = event.getMessage().getRoleMentions().get(0).getStringID();
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET MODROLE = '" + roleId + "' WHERE MODROLE = '" + getPreviousItem(statement, databaseName, 5) + "'";
            statement.execute(execution);
        } catch (IndexOutOfBoundsException e) {
            SPECIAL.log("Please mention a role!", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void setJoinLeave(MessageReceivedEvent event) {

        try {
            String bool = "";
            if (event.getMessage().getContent().contains("true")) {
                bool = "true";
            } else if (event.getMessage().getContent().contains("false")) {
                bool = "false";
            }
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            // try (Connection connect = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET JOINLEAVE = '" + bool + "' WHERE JOINLEAVE = '" + getPreviousItem(statement, databaseName, 6) + "'";
            statement.execute(execution);
            //  }
        } catch (SQLException ex) {
        }
    }

    public void setPM(MessageReceivedEvent event) {

        try {
            String bool = "";
            if (event.getMessage().getContent().contains("true")) {
                bool = "true";
            } else if (event.getMessage().getContent().contains("false")) {
                bool = "false";
            }
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET PRIVATEMESSAGE = '" + bool + "' WHERE PRIVATEMESSAGE = '" + getPreviousItem(statement, databaseName, 8) + "'";
            statement.execute(execution);
        } catch (SQLException ex) {
        }
    }

    public void setPrefix(MessageReceivedEvent event) {

        try {
            String prefix = Arrays.asList(event.getMessage().getContent().split(" ")).get(2);
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET CPREFIX = '" + prefix + "' WHERE CPREFIX = '" + getPreviousItem(statement, databaseName, 7) + "'";
            statement.execute(execution);
        } catch (ArrayIndexOutOfBoundsException e) {
            SPECIAL.log("Format Error\nExample - !set prefix !!", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void setJoinMessage(MessageReceivedEvent event) {

        try {
            String joinMessage = Arrays.asList(event.getMessage().getContent().replace(">", "").split("<")).get(1);
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET JOINMESSAGE = '" + joinMessage + "' WHERE JOINMESSAGE = '" + getPreviousItem(statement, databaseName, 9) + "'";
            statement.execute(execution);
        } catch (ArrayIndexOutOfBoundsException e) {
            SPECIAL.log(" Format Error\nExample - !set join **<**Message**>**", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void setLeaveMessage(MessageReceivedEvent event) {

        try {
            String leaveMessage = Arrays.asList(event.getMessage().getContent().replace(">", "").split("<")).get(1);
            String id = event.getGuild().getStringID();
            String name = event.getGuild().getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET LEAVEMESSAGE = '" + leaveMessage + "' WHERE LEAVEMESSAGE = '" + getPreviousItem(statement, databaseName, 10) + "'";
            statement.execute(execution);
        } catch (ArrayIndexOutOfBoundsException e) {
            SPECIAL.log("Format Error\nExample - !set leave **<**Message**>**", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void setAdmin(String roleId, IGuild guild) {

        try {
            String id = guild.getStringID();
            String name = guild.getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET ADMINROLE = '" + roleId + "' WHERE ADMINROLE = '" + getPreviousItem(statement, databaseName, 4) + "'";
            statement.execute(execution);
        } catch (SQLException ex) {
        }
    }

    public void setMod(String roleId, IGuild guild) {

        try {
            String id = guild.getStringID();
            String name = guild.getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET MODROLE = '" + roleId + "' WHERE MODROLE = '" + getPreviousItem(statement, databaseName, 5) + "'";
            statement.execute(execution);
        } catch (SQLException ex) {
        }
    }

    public void setJoinLeave(String bool, IGuild guild) {

        try {
            String id = guild.getStringID();
            String name = guild.getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET JOINLEAVE = '" + bool + "' WHERE JOINLEAVE = '" + getPreviousItem(statement, databaseName, 6) + "'";
            statement.execute(execution);
        } catch (SQLException ex) {
        }
    }

    public void setPrefix(String prefix, IGuild guild) {
        try {
            String id = guild.getStringID();
            String name = guild.getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET CPREFIX = '" + prefix + "' WHERE CPREFIX = '" + getPreviousItem(statement, databaseName, 7) + "'";
            statement.execute(execution);
        } catch (SQLException ex) {
        }
    }

    public void setJoinMessage(String joinMessage, IGuild guild) {

        try {
            String id = guild.getStringID();
            String name = guild.getName();
            String databaseName = getTableName(id, name);
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET JOINMESSAGE = '" + joinMessage + "' WHERE JOINMESSAGE = '" + getPreviousItem(statement, databaseName, 9) + "'";
            statement.execute(execution);
        } catch (SQLException e) {
        }
    }

    public void setLeaveMessage(String leaveMessage, IGuild guild) {

        try {
            String id = guild.getStringID();
            String name = guild.getName();
            String databaseName = getTableName(id, name);
            //try (Connection connect = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            String execution = "UPDATE " + databaseName + " SET LEAVEMESSAGE = '" + leaveMessage + "' WHERE LEAVEMESSAGE = '" + getPreviousItem(statement, databaseName, 10) + "'";
            statement.execute(execution);
            // }
        } catch (SQLException e) {
        }
    }

    public void rolePrefixAdd(MessageReceivedEvent event) {
        try {
            boolean used = false;
            String prefixToAssign = StringUtils.removeStart(event.getMessage().getContent().split("<")[2].replace(">", ""), " ");
            String role = event.getMessage().getRoleMentions().get(0).getStringID();
            String databaseName = getTableName(event.getGuild().getStringID(), event.getGuild().getName());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + databaseName);
            while (resultSet.next()) {
                try {
                    if (resultSet.getString(1).equals(role)) {
                        used = true;
                    }
                } catch (NullPointerException e) {
                }
            }
            if (used == false) {
                String execution = "INSERT INTO " + databaseName + " (ROLE, ROLEPREFIX) VALUES ('" + role + "', '" + prefixToAssign + "')";
                statement.execute(execution);
            }
            if (used == true) {
                String execution = "UPDATE " + databaseName + " SET ROLEPREFIX = '" + prefixToAssign + "' WHERE ROLE = '" + role + "'";
                statement.execute(execution);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            new Special().log("Format Error\nExample - __!prefix @role <Prefix>__ \n(Note the **<>** are required for the suffix!)", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void roleSuffixAdd(MessageReceivedEvent event) {
        try {
            boolean used = false;
            String suffixToAssign = StringUtils.removeEnd(event.getMessage().getContent().split("<")[2].replace(">", ""), " ");
            String role = event.getMessage().getRoleMentions().get(0).getStringID();
            String databaseName = getTableName(event.getGuild().getStringID(), event.getGuild().getName());
            // try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + databaseName);
            while (resultSet.next()) {
                try {
                    if (resultSet.getString(1).equals(role)) {
                        used = true;
                    }
                } catch (NullPointerException e) {
                }
            }
            if (used == false) {
                String execution = "INSERT INTO " + databaseName + " (ROLE, ROLESUFFIX) VALUES ('" + role + "', '" + suffixToAssign + "')";
                statement.execute(execution);
            }
            if (used == true) {
                String execution = "UPDATE " + databaseName + " SET ROLESUFFIX = '" + suffixToAssign + "' WHERE ROLE = '" + role + "'";
                statement.execute(execution);
            }

            // }
        } catch (ArrayIndexOutOfBoundsException e) {
            new Special().log("Format Error\nExample - __!suffix @role <Suffix>__ \n(Note the **<>** are required for the suffix!)", false, event.getChannel());
        } catch (SQLException ex) {
        }
    }

    public void rolePrefixAdd(String role, String prefixToAssign, IGuild guild) {

        try {
            boolean used = false;
            String databaseName = getTableName(guild.getStringID(), guild.getName());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + databaseName);
            while (resultSet.next()) {
                try {
                    if (resultSet.getString(1).equals(role)) {
                        used = true;
                    }
                } catch (NullPointerException e) {
                }
            }
            if (used == false) {
                String execution = "INSERT INTO " + databaseName + " (ROLE, ROLEPREFIX) VALUES ('" + role + "', ' " + prefixToAssign + "')";
                statement.execute(execution);
            }
            if (used == true) {
                String execution = "UPDATE " + databaseName + " SET ROLEPREFIX = '" + prefixToAssign + "' WHERE ROLE = '" + role + "'";
                statement.execute(execution);
            }
        } catch (SQLException ex) {
        }
    }

    public void roleSuffixAdd(String role, String suffixToAssign, IGuild guild) {

        try {
            boolean used = false;
            String databaseName = getTableName(guild.getStringID(), guild.getName());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + databaseName);
            while (resultSet.next()) {
                try {
                    if (resultSet.getString(1).equals(role)) {
                        used = true;
                    }
                } catch (NullPointerException e) {
                }
            }
            if (used == false) {
                String execution = "INSERT INTO " + databaseName + " (ROLE, ROLESUFFIX) VALUES ('" + role + "', ' " + suffixToAssign + "')";
                statement.execute(execution);
            }
            if (used == true) {
                String execution = "UPDATE " + databaseName + " SET ROLESUFFIX = '" + suffixToAssign + "' WHERE ROLE = '" + role + "'";
                statement.execute(execution);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void save() {

        try {
            // try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE SAVEINFO");
            Date dateBase = new Date();

            String[] dataDate = dateBase.toString().split(" ");
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
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
            statement.execute("INSERT INTO SaveInfo (DATE, TIME) VALUES ('" + date.toString() + "', '" + time.toString() + "')");
            // } catch (SQLException e) {
            // }
        } catch (SQLException ex) {
        }
    }
}

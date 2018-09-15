/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.ui;

import net.rising.core.EventRunner;
import net.rising.core.api.Database;
import net.rising.file.EFile;
import org.discordbots.api.client.DiscordBotListAPI;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.RequestBuffer;

import javax.imageio.ImageIO;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author crysi
 */
public class MainUI extends javax.swing.JFrame {

    public static IDiscordClient bot;
    public static DiscordBotListAPI api;
    private static int count = 0;
    private static int num = 0;
    // Variables declaration - do not modify
    private javax.swing.ButtonGroup buttonGroupSelection;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAnnounce;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonInfo;
    private javax.swing.JButton jButtonInvite;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JButton jButtonUtility;
    private javax.swing.JComboBox<String> jComboBoxServerSelector;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButtonDev;
    private javax.swing.JRadioButton jRadioButtonPublic;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    public MainUI() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        buttonGroupSelection = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButtonStart = new javax.swing.JButton();
        jRadioButtonDev = new javax.swing.JRadioButton();
        jRadioButtonPublic = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButtonAnnounce = new javax.swing.JButton();
        jComboBoxServerSelector = new javax.swing.JComboBox<>();
        jButtonInfo = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonInvite = new javax.swing.JButton();
        jButtonUtility = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Control Panel");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }

            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Bahnschrift", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PHOENIX BOT CONTROL PANEL");
        jLabel1.setName("jLabel1"); // NOI18N

        jButtonStart.setFont(new java.awt.Font("Bahnschrift", 0, 40)); // NOI18N
        jButtonStart.setText("Start");
        jButtonStart.setName("jButtonStart"); // NOI18N
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 859, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 54, Short.MAX_VALUE)
                                .add(jButtonStart)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(jButtonStart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonGroupSelection.add(jRadioButtonDev);
        jRadioButtonDev.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jRadioButtonDev.setSelected(true);
        jRadioButtonDev.setText("Developer Bot");
        jRadioButtonDev.setName("jRadioButtonDev"); // NOI18N

        buttonGroupSelection.add(jRadioButtonPublic);
        jRadioButtonPublic.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        jRadioButtonPublic.setText("Public Bot");
        jRadioButtonPublic.setName("jRadioButtonPublic"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jButtonAnnounce.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButtonAnnounce.setText("Announce");
        jButtonAnnounce.setName("jButtonAnnounce"); // NOI18N
        jButtonAnnounce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnnounceActionPerformed(evt);
            }
        });

        jComboBoxServerSelector.setFont(new java.awt.Font("Segoe UI Emoji", 0, 13)); // NOI18N
        jComboBoxServerSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{ "All" }));
        jComboBoxServerSelector.setToolTipText("");
        jComboBoxServerSelector.setName("jComboBoxServerSelector"); // NOI18N

        jButtonInfo.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButtonInfo.setText("Get Info");
        jButtonInfo.setName("jButtonInfo"); // NOI18N
        jButtonInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInfoActionPerformed(evt);
            }
        });

        jButtonClear.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButtonClear.setText("Clear");
        jButtonClear.setName("jButtonClear"); // NOI18N
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jButtonRefresh.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButtonRefresh.setText("Refresh");
        jButtonRefresh.setName("jButtonRefresh"); // NOI18N
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jButtonInvite.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButtonInvite.setText("Get Invite");
        jButtonInvite.setName("jButtonInvite"); // NOI18N
        jButtonInvite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInviteActionPerformed(evt);
            }
        });

        jButtonUtility.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButtonUtility.setText("Utility");
        jButtonUtility.setName("jButtonUtility"); // NOI18N
        jButtonUtility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUtilityActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jButton1.setText("Disconnect");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jScrollPane1)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                                        .add(jRadioButtonDev, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(jRadioButtonPublic, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                                        .add(jComboBoxServerSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(layout.createSequentialGroup()
                                                                .add(jButtonRefresh)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .add(jButtonClear))))
                                        .add(layout.createSequentialGroup()
                                                .add(jButtonAnnounce, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(jButtonUtility)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                                .add(jButtonInvite, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jButtonInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jRadioButtonDev)
                                        .add(jComboBoxServerSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jRadioButtonPublic)
                                        .add(jButtonRefresh)
                                        .add(jButtonClear))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 85, Short.MAX_VALUE)
                                .add(jButton1)
                                .add(24, 24, 24)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(jButtonAnnounce, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                                .add(org.jdesktop.layout.GroupLayout.LEADING, jButtonInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(org.jdesktop.layout.GroupLayout.LEADING, jButtonInvite, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                                        .add(jButtonUtility, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(12, 12, 12)
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    public void setStats(int servers) {
        api.setStats(servers);
    }

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {

        jTextArea1.append("Connecting...");
        String token;
        if (jRadioButtonDev.isSelected()) {
            token = "";
        } else {
            token = "";
        }
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.set5xxRetryCount(500);
        clientBuilder.setMaxReconnectAttempts(5000);
        clientBuilder.withRecommendedShardCount();

        clientBuilder.withToken(token);

        bot = clientBuilder.login();

        EventDispatcher dis = bot.getDispatcher();
        dis.registerListener(new EventRunner());
        api = new DiscordBotListAPI.Builder()
                .token("")
                .botId("439454842071547905")
                .build();

        jTextArea1.append("\nConnected...");
    }

    private void jButtonAnnounceActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        String item = jComboBoxServerSelector.getSelectedItem().toString();
        String message = jTextArea1.getText();
        jTextArea1.setText("");

        if (item.equals("All")) {
            bot.getGuilds()
                    .parallelStream()
                    .forEach(g -> {
                        RequestBuffer.request(() -> g.getDefaultChannel().sendMessage(message));
                        num++;
                        jTextArea1.append("\n" + num + " of " + bot.getGuilds().size() + " completed");
                    });
            num = 0;
        } else {
            IGuild guildling = bot.getGuilds()
                    .parallelStream()
                    .filter(guild -> guild.getName().equals(item))
                    .findFirst()
                    .get();
            RequestBuffer.request(() -> guildling.getDefaultChannel().sendMessage(message));
        }
    }

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        long timeStart = System.nanoTime();
        int guildCount = (int) bot.getGuilds().stream().parallel().count();
        double timeEnd = Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100.0) / 100.0;
        jTextArea1.append("\nGuild Count: " + guildCount + "   [" + timeEnd + " ms]");
        timeStart = System.nanoTime();
        jComboBoxServerSelector.removeAllItems();
        bot.getGuilds().stream().forEach(guild -> {
            count = (int) (count + guild.getUsers().stream().parallel().count());
            jComboBoxServerSelector.addItem(guild.getName());
        });
        timeEnd = Math.round(((System.nanoTime() - timeStart) / 1000000.0) * 100.0) / 100.0;
        jTextArea1.append("\nPlayer Count: " + count + "    [" + timeEnd + " ms]");
        count = 0;
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        // TODO add your handling code here:

        jTextArea1.append("\nDisconnecting...");
        jTextArea1.append("\nDisconnected...");
        Database database = new Database();
        database.save();
    }

    private void jButtonInfoActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        String item = jComboBoxServerSelector.getSelectedItem().toString();
        bot.getGuilds().parallelStream()
                .filter(g -> g.getName().equals(item))
                .forEach(guild -> {
                    StringBuilder build = new StringBuilder();
                    guild.getUsers().forEach(u -> build.append(u.getName()).append("\n"));

                    jTextArea1.setText("Guild Name: " + guild.getName() + "\n"
                            + "Region: " + guild.getRegion().getName() + "\n"
                            + "Owner: " + guild.getOwner().getName() + "\n"
                            + "Creation Date: " + guild.getCreationDate().toString() + "\n"
                            + "String ID: " + guild.getStringID() + "\n"
                            + "User:\n " + build.toString() + "\n"
                    );
                });
    }

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        jTextArea1.setText("");
    }

    private void jButtonInviteActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        String item = jComboBoxServerSelector.getSelectedItem().toString();
        bot.getGuilds().parallelStream()
                .filter(g -> g.getName().equals(item))
                .forEach(g -> jTextArea1.setText(g.getDefaultChannel().createInvite(0, 0, false, false).getCode()));
    }

    private void jButtonUtilityActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(() -> new UtilityUI().setVisible(true));
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        bot.getGuildByID(456402013945856027l).getChannelsByName("phoenix-bot-connect-log").get(0).sendMessage("**Bot Disconnected!**");
        formWindowClosing(new WindowEvent(this, 1));
        System.exit(0);
        dispose();
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        // TODO add your handling code here:
        try {
            setIconImage(ImageIO.read(new EFile("C:\\Users\\Administrator\\Desktop\\Phoenix Bot Data\\Images\\", "images.png").getFile()));
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // End of variables declaration                   
}

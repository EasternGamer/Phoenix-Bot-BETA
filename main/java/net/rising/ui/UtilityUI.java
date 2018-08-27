/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.ui;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import static net.rising.ui.MainUI.bot;

/**
 *
 * @author crysi
 */
public class UtilityUI extends javax.swing.JFrame {

    /**
     * Creates new form UtilityUI
     */
    public UtilityUI() {
        initComponents();
    }
    private static IUser userling;
    private static IGuild guildling;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldUser = new javax.swing.JTextField();
        jTextFieldServer = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButtonGuildSearch = new javax.swing.JButton();
        jButtonUserSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea = new javax.swing.JTextArea();
        jButtonDiscard = new javax.swing.JButton();
        jButtonMessage = new javax.swing.JButton();
        jButtonPerm = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("User");

        jLabel2.setText("Server");

        jButtonGuildSearch.setText("Guild Search");
        jButtonGuildSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuildSearchActionPerformed(evt);
            }
        });

        jButtonUserSearch.setText("User Search");
        jButtonUserSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUserSearchActionPerformed(evt);
            }
        });

        jTextArea.setColumns(20);
        jTextArea.setFont(new java.awt.Font("Segoe UI Emoji", 0, 13)); // NOI18N
        jTextArea.setRows(5);
        jScrollPane1.setViewportView(jTextArea);

        jButtonDiscard.setText("Discard");
        jButtonDiscard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscardActionPerformed(evt);
            }
        });

        jButtonMessage.setText("Private Message Player: NULL");
        jButtonMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMessageActionPerformed(evt);
            }
        });

        jButtonPerm.setText("Get Permissions");
        jButtonPerm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPermActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel1))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldUser)
                                    .addComponent(jButtonGuildSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                                    .addComponent(jButtonUserSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jButtonDiscard))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButtonMessage)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                                        .addComponent(jButtonPerm))))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(22, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldServer, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonDiscard))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonGuildSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUserSearch)
                    .addComponent(jButtonMessage)
                    .addComponent(jButtonPerm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonGuildSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuildSearchActionPerformed
        // TODO add your handling code here:

        String item = jTextFieldServer.getText();
        bot.getGuilds().parallelStream()
                .filter(g -> g.getName().equals(item))
                .forEach(g -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Server Name: ").append(g.getName()).append("\n");
                    builder.append("Owner Name: ").append(g.getOwner().getName()).append("\n");
                    jTextArea.setText(builder.toString());
                    guildling = g;
                });
    }//GEN-LAST:event_jButtonGuildSearchActionPerformed

    private void jButtonDiscardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscardActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButtonDiscardActionPerformed

    private void jButtonUserSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUserSearchActionPerformed
        // TODO add your handling code here:|
        String item = jTextFieldUser.getText();
        jButtonMessage.setText("Private Message Player: " + item);
        bot.getGuilds().parallelStream()
                .forEach(g -> {
                    StringBuilder builder = new StringBuilder();
                    g.getUsers().parallelStream()
                            .filter(user -> user.getName().equals(item))
                            .forEach(user -> {
                                builder.append("Server Name: ").append(g.getName()).append("\n");
                                builder.append("Owner Name: ").append(g.getOwner().getName()).append("\n");
                                builder.append("User: ").append(user.getName()).append("\n");
                                builder.append("User Permissions: ").append(user.getPermissionsForGuild(g)).append("\n");
                                jTextArea.setText(builder.toString());
                                userling = user;
                            });
                });

    }//GEN-LAST:event_jButtonUserSearchActionPerformed

    private void jButtonMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMessageActionPerformed
        // TODO add your handling code here:
        userling.getOrCreatePMChannel().sendMessage("**Developer: ** " + jTextArea.getText());
    }//GEN-LAST:event_jButtonMessageActionPerformed

    private void jButtonPermActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPermActionPerformed
        // TODO add your handling code here:
        bot.getGuilds().parallelStream()
                .filter(guild -> guild.getName().equals(jTextFieldServer.getText()))
                .forEach(guild -> {
                    StringBuilder build = new StringBuilder();
                    build.append(bot.getOurUser().getPermissionsForGuild(guild));

                    jTextArea.setText(build.toString());
                });

    }//GEN-LAST:event_jButtonPermActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDiscard;
    private javax.swing.JButton jButtonGuildSearch;
    private javax.swing.JButton jButtonMessage;
    private javax.swing.JButton jButtonPerm;
    private javax.swing.JButton jButtonUserSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea;
    private javax.swing.JTextField jTextFieldServer;
    private javax.swing.JTextField jTextFieldUser;
    // End of variables declaration//GEN-END:variables
}

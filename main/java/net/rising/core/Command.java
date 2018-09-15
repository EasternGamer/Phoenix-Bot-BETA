/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.core;

import net.rising.core.api.Database;
import net.rising.core.api.Utils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author crysi
 */
public interface Command {

    void run(MessageReceivedEvent event, Database database, Utils utils, boolean owner, boolean admin, boolean mod);
}

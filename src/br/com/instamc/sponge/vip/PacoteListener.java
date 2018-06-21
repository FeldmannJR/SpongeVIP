/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip;

import br.com.instamc.sponge.vip.data.PacoteDB;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 *
 * @author Carlos
 */
public class PacoteListener {

    @Listener
    public void join(ClientConnectionEvent.Join join) {
        Player p = join.getTargetEntity();
        PacoteDB.checkVencimentosOnLogin(p);

    }

  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.pacotes;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.Player;

/**
 *
 * @author Carlos
 */
public class PacotePermission extends Pacote {

    List<String> permission = new ArrayList<String>();
    public static final DataQuery PERMISSIONS = DataQuery.of("permissions");

    public List<String> getPermissions() {
        return permission;
    }

    @Override
    public boolean loadData(DataView data) {
        if (!super.loadData(data)) {
            return false;
        }
        if (data.contains(PERMISSIONS)) {
            for (String str : data.getStringList(PERMISSIONS).get()) {
                permission.add(str);
            }
            return true;
        }
        return false;
    }

    @Override
    public DataContainer setData(DataContainer c) {
        c.set(PERMISSIONS, permission);
        return c;
    }

    @Override
    public boolean give(Player p) {

        for (String perm : permission) {
            if (p.hasPermission(perm)) {
                return false;
            }
        }
        for (String perm : permission) {

            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "pex user " + p.getUniqueId() + " permission " + perm + " 1");

        }
        return true;
    }

}

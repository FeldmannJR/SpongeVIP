/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;

/**
 *
 * @author Carlos
 */
public class PermissionsUtils {

    public static void setGroup(Player p, String grupo) {
        Sponge.getGame().getCommandManager().process(Sponge.getServer().getConsole(), "pex user " + p.getUniqueId().toString() + " parent set group " + grupo);
      
    }

    public static String getGroup(Player player) {
        for (Subject sub : player.getParents()) {
            if (sub.getContainingCollection().equals(getGroups()) && (sub.getIdentifier() != null)) {
           
                return sub.getIdentifier();
            }
        }
        return null;
    }

    public static SubjectCollection getGroups() {
        return SpongeVIP.getPermissions().getGroupSubjects();
    }
}

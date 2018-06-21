/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.cmds;

import br.com.instamc.sponge.vip.menus.PlayersMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 *
 * @author Carlos
 */
public class CmdShop implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            PlayersMenu.openPlayer((Player)src,null);
            return CommandResult.success();
        }
        return CommandResult.empty();
    }

}

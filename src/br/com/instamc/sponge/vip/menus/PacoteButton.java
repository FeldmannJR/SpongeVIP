/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.menus;

import br.com.instamc.sponge.library.menu.ClickType;
import br.com.instamc.sponge.library.menu.Menu;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.menu.menus.venda.VendaMenu;
import br.com.instamc.sponge.library.menu.menus.venda.moedas.MoedaType;
import br.com.instamc.sponge.vip.SpongeVIP;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

/**
 *
 * @author Carlos
 */
public class PacoteButton extends MenuButton {

    final Pacote pac;
   

    public PacoteButton(SlotPos pos, Pacote p, Player pl) {
        super(pos, p.buildItem(pl));
        this.pac = p;
    }

    @Override
    public void click(Player p, Menu m, ClickType click) {

        new ViewPacoteMenu(pac, p).open(p);
    }

}

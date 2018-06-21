/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.menus;

import br.com.instamc.sponge.library.menu.ClickType;
import br.com.instamc.sponge.library.menu.Menu;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.utils.ItemStackBuilder;
import br.com.instamc.sponge.library.utils.ItemUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.SpongeVIP;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteType;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

/**
 *
 * @author Carlos
 */
public class PlayersMenu {

    public static void openPlayer(Player p, Pagina pag) {

        Menu m = new Menu(pag == null ? "Pacotes" : "" + pag.nome, pag != null ? pag.linhas : 6);
        List<SlotPos> npode = new ArrayList();
        if (pag == null) {
            for (MenuButton mb : SpongeVIP.getButtons()) {
                m.addButton(mb);
                npode.add(mb.getSlot());
            }
        }

        //PACOTES
        for (Pacote pac : PacoteManager.getPacotes()) {
            if (pac.getSlot() != null && pac.getPagina() == pag) {

                m.addButton(new PacoteButton(pac.getSlot(), pac, p));
            }
        }
        //PAGINAS
        if (pag == null) {
            for (Pagina pg : PacoteManager.paginas) {
                if (pg.slot == null) {
                    continue;
                }
                ItemStack icone = pg.icone.copy();
                ItemUtils.setItemName(icone, Txt.f("§e§l" + pg.nome));
                ItemUtils.addLore(icone, Txt.f("§a=> §fClique aqui para abrir a pagina!"));
                m.addButton(new MenuButton(pg.slot, icone) {

                    @Override
                    public void click(Player p, Menu m, ClickType click) {
                        openPlayer(p, pg);
                    }
                });
            }
        } else {
            m.addButton(new MenuButton(new SlotPos(0, pag.linhas - 1), ItemStackBuilder.of(ItemTypes.REPEATER).withName("§e§lVoltar").build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    PlayersMenu.openPlayer(p, null);
                }
            });
        }

        m.open(p);

    }

    public void reloadButtons() {

    }
}

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
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import com.flowpowered.math.vector.Vector2i;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class StaffMenu extends Menu {

    public StaffMenu(int page) {
        super("Pacotes " + (page + 1), 6);
        for (Pacote pac : getPage(page)) {
            addButtonNextSlot(new MenuButton(new SlotPos(0, 0), pac.buildItem(true, pac.getPreco())) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    new EditPacoteMenu(pac).open(p);

                }
            });
        }
        addButton(new MenuButton(new SlotPos(3, 5), ItemStackBuilder.of(ItemTypes.BONE).withName("§b§lEditar Slots").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
                new EditSlotsMenu(null).open(p);
            }
        });
        addButton(new MenuButton(new SlotPos(5, 5), ItemStackBuilder.of(ItemTypes.PAPER).withName("§b§lEditar Paginas").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
            new EditPaginaListMenu().open(p);
            }
        });

        if (page != 0) {
            addButton(new MenuButton(new SlotPos(0, 5), ItemStackBuilder.of(ItemTypes.REPEATER).withName("§b§lVoltar").build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    new StaffMenu(page - 1).open(p);
                }

            });
        }

        if (page < getPages()) {
            addButton(new MenuButton(new SlotPos(8, 5), ItemStackBuilder.of(ItemTypes.COMPARATOR).withName("§b§lAvancar").build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    new StaffMenu(page + 1).open(p);
                }

            });
        }

    }

    public static int getPages() {
        int size = PacoteManager.getPacotes().size();

        return ((size - 1) / 45);
    }

    public static List<Pacote> getPage(int page) {
        ArrayList<Pacote> infos = new ArrayList();
        int foi = 0;
        int start = page * 45;
        for (Pacote info : PacoteManager.getPacotes()) {
            if (foi >= (start + 45)) {
                break;
            }
            if (foi >= start) {
                infos.add(info);
            }
            foi++;
        }
        return infos;

    }

}

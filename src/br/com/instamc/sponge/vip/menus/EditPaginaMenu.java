/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.menus;

import br.com.instamc.sponge.library.menu.ClickType;
import br.com.instamc.sponge.library.menu.Menu;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.menu.MenuCloseAction;
import br.com.instamc.sponge.library.menu.buttons.NothingButton;
import br.com.instamc.sponge.library.utils.ItemStackBuilder;
import br.com.instamc.sponge.library.utils.SchedulerUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class EditPaginaMenu extends Menu {

    public EditPaginaMenu(Pagina pag) {
        super("Pagina " + pag.nome, 1);

        addButton(new MenuButton(new SlotPos(3, 0), ItemStackBuilder.of(ItemTypes.BARRIER).withName("§c§lDeletar").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
                PacoteManager.delete(pag);
                openEditPaginas(p);
                p.sendMessage(Txt.f("§cDeletado!"));
            }
        });
        addButton(new MenuButton(new SlotPos(4, 0), ItemStackBuilder.of(ItemTypes.STAINED_GLASS_PANE).withName("§c§lEditar Itens").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
              new EditSlotsMenu(pag).open(p);
            }
        });

        addButton(new MenuButton(new SlotPos(5, 0), ItemStackBuilder.of(ItemTypes.DIAMOND).withName("§c§lSetar Icone").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
                Menu set = new Menu("Setar Icone", 1);
                set.setMoveItens(true);

                for (int x : new int[]{0, 1, 2, 3, 5, 6, 7, 8}) {
                    set.addButton(new NothingButton(new SlotPos(x, 0), ItemStack.of(ItemTypes.BARRIER, 1)));

                }
                if (pag.icone != null) {
                    set.addNonButton(pag.icone.copy());
                }
                set.addClose(new MenuCloseAction() {

                    @Override
                    public void closeMenu(Player p, Menu m) {
                        if (!m.getNonButtons().isEmpty()) {
                            ItemStack i = m.getNonButtons().get(0);
                            pag.icone = i.copy();
                            p.sendMessage(Txt.f("§aIcone setado!"));
                        } else {

                            pag.icone = null;

                            p.sendMessage(Txt.f("§aIcone Removido!"));
                        }
                        PacoteDB.savePagina(pag);
                        SchedulerUtils.runSync(new Runnable() {

                            @Override
                            public void run() {
                                new EditPaginaMenu(pag).open(p);
                            }
                        }, 5);

                    }
                });
                set.open(p);
            }
        });

    }

    public static void openEditPaginas(Player p) {
        new EditPaginaListMenu().open(p);

    }

}

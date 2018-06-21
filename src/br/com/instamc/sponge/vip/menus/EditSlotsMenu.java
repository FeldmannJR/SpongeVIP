/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.menus;

import br.com.instamc.sponge.library.menu.ClickType;
import br.com.instamc.sponge.library.menu.Menu;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.menu.buttons.NothingButton;
import br.com.instamc.sponge.library.utils.ItemStackBuilder;
import br.com.instamc.sponge.library.utils.ItemUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.SpongeVIP;
import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class EditSlotsMenu extends Menu {

    public EditSlotsMenu(Pagina pag) {
        super("Slots", pag != null ? pag.linhas : 6);
        List<SlotPos> pos = new ArrayList();
        for (int x = 0; x <= 8; x++) {
            for (int y = 0; y <= 5; y++) {
                pos.add(new SlotPos(x, y));
            }
        }

        if (pag == null) {
            for (MenuButton mb : SpongeVIP.getButtons()) {
                ItemStack i = mb.item.copy();
                addButton(new NothingButton(mb.getSlot(), i));
                pos.remove(mb.getSlot());
            }
        } else {
            addButton(new MenuButton(new SlotPos(0, pag.linhas - 1), ItemStackBuilder.of(ItemTypes.BARRIER).withName("§c§lBotão Voltar").build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                }
            });
            pos.remove(new SlotPos(0, pag.linhas - 1));

        }
        PAC:
        for (Pacote pac : PacoteManager.getPacotes()) {
            if (pac.getSlot() != null) {

                for (SlotPos pacpos : new ArrayList<SlotPos>(pos)) {
                    if (pacpos.getX() == pac.getSlot().getX() && pacpos.getY() == pac.getSlot().getY() && pac.getPagina() == pag) {
                        pos.remove(pacpos);
                    }
                }
                if (pac.getPagina() == pag) {
                    addButton(new MenuButton(pac.getSlot(), pac.buildItem(false, pac.getPreco())) {

                        @Override
                        public void click(Player p, Menu m, ClickType click) {
                            pac.setSlot(null);
                            pac.setPagina(null);
                            PacoteDB.savePacote(pac);
                            new EditSlotsMenu(pag).open(p);
                        }
                    });
                }
            }
        }
        if (pag == null) {
            PG:
            for (Pagina pg : PacoteManager.paginas) {
                if (pg.slot != null) {

                    ItemStack icone = pg.icone.copy();
                    ItemUtils.setItemName(icone, Txt.f("§fPagina: §e" + pg.nome));
                    for (SlotPos pacpos : new ArrayList<SlotPos>(pos)) {
                        if (pacpos.getX() == pg.slot.getX() && pacpos.getY() == pg.slot.getY()) {
                            pos.remove(pacpos);
                        }
                    }
                    addButton(new MenuButton(pg.slot, icone) {

                        @Override
                        public void click(Player p, Menu m, ClickType click) {
                            pg.slot = null;
                            PacoteDB.savePagina(pg);
                            new EditSlotsMenu(pag).open(p);
                        }
                    });
                }
            }
        }
        for (SlotPos po : pos) {
            addButton(new MenuButton(po, ItemStackBuilder.of(ItemTypes.GLASS_PANE).withName("§aAdicionar Pacote").build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    Menu me = new Menu("Selecionar", 6);
                    final SlotPos slot = getSlot();
                    boolean tem = false;
                    for (Pacote pac : PacoteManager.getPacotes()) {
                        if (pac.getSlot() == null) {
                            tem = true;
                        }
                    }
                    for (Pagina pag : PacoteManager.paginas) {
                        if (pag.slot == null && pag.icone != null) {
                            tem = true;
                        }
                    }
                    if (!tem) {
                        playSound(p, SoundTypes.BLOCK_FIRE_EXTINGUISH);
                        p.sendMessage(Txt.f("§c§lNão tem pacotes!"));
                        return;
                    }
                    for (Pacote pac : PacoteManager.getPacotes()) {
                        if (pac.getSlot() == null) {
                            me.addButtonNextSlot(new MenuButton(new SlotPos(0, 0), pac.buildItem(true, pac.getPreco())) {

                                @Override
                                public void click(Player p, Menu m, ClickType click) {
                                    pac.setSlot(slot);
                                    pac.setPagina(pag);
                                    PacoteDB.savePacote(pac);
                                    new EditSlotsMenu(pag).open(p);

                                }
                            });
                        }
                    }
                    if (pag == null) {
                        for (Pagina pg : PacoteManager.paginas) {

                            if (pg.slot == null) {
                                ItemStack icone = pg.icone.copy();
                                ItemUtils.setItemName(icone, Txt.f("§fPagina: §e" + pg.nome));

                                me.addButtonNextSlot(new MenuButton(new SlotPos(0, 0), icone) {

                                    @Override
                                    public void click(Player p, Menu m, ClickType click) {
                                        pg.slot = slot;
                                        PacoteDB.savePagina(pg);
                                        new EditSlotsMenu(pag).open(p);

                                    }
                                });
                            }
                        }

                    }
                    me.open(p);
                }
            });
        }

    }

}

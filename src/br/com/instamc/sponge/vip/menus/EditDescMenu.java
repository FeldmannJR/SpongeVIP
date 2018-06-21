/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.menus;

import br.com.instamc.sponge.library.apis.ChatInputAPI;
import br.com.instamc.sponge.library.chat.ChatAPI;
import br.com.instamc.sponge.library.menu.ClickType;
import br.com.instamc.sponge.library.menu.Menu;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.utils.ItemStackBuilder;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.pacotes.PacoteVIP;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class EditDescMenu extends Menu {

    PacoteVIP pacote;

    public EditDescMenu(PacoteVIP pacote) {

        super("Editar Desc", 4);
        this.pacote = pacote;
        for (final String s : pacote.getVantagens()) {
            addButtonToSquare(new SlotPos(0, 0), new SlotPos(8, 2), new MenuButton(new SlotPos(0, 0), ItemStackBuilder.of(ItemTypes.PAPER).withName("§a" + s).build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    pacote.getVantagens().remove(s);
                    new EditDescMenu(pacote).open(p);

                }
            });
        }
        addButton(new MenuButton(new SlotPos(0, 3), ItemStackBuilder.of(ItemTypes.REPEATER).withName("§e§lVoltar").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
                new EditPacoteMenu(pacote).open(p);
            }
        });
        if (this.pacote.getVantagens().size() < 27) {
            addButton(new MenuButton(new SlotPos(4, 3), ItemStackBuilder.of(ItemTypes.EMERALD).withName("§a§lAdd Desc").build()) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    if (pacote.getVantagens().size() < 27) {
                        close(p);
                        ChatInputAPI.inputPlayer(p, new ChatInputAPI.ChatAction(10) {

                            @Override
                            public void inputText(Player p, String t) {
                                if (pacote.getVantagens().size() < 27) {
                                    pacote.getVantagens().add(t);
                                    PacoteManager.addPacote(pacote);
                                    new EditDescMenu(pacote).open(p);
                                } else {
                                    p.sendMessage(Txt.f("§6§lJá tem 27 vantagens!"));
                                }

                            }
                        }, "Adicionar desc");
                    }
                }
            });
        }

    }

}

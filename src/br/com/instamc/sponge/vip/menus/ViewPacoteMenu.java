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
import br.com.instamc.sponge.library.menu.menus.venda.VendaMenu;
import br.com.instamc.sponge.library.menu.menus.venda.moedas.MoedaType;
import br.com.instamc.sponge.library.utils.ItemStackBuilder;
import br.com.instamc.sponge.library.utils.ItemUtils;
import br.com.instamc.sponge.library.utils.SchedulerUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.ComprouPacote;
import br.com.instamc.sponge.vip.SpongeVIP;
import static br.com.instamc.sponge.vip.menus.EditPacoteMenu.buildEdit;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteItens;
import br.com.instamc.sponge.vip.pacotes.PacoteType;
import br.com.instamc.sponge.vip.pacotes.PacoteVIP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

/**
 *
 * @author Carlos
 */
public class ViewPacoteMenu extends Menu {

    Pacote pacote;

    public ViewPacoteMenu(Pacote pac, Player p) {
        super("Vendo Pacote", 6);
        this.pacote = pac;
        final int preco = pacote.getPreco(p);
        addButton(new NothingButton(new SlotPos(4, 0), pac.buildItem(true, preco)));

        for (SlotPos s : Menu.buildSquare(new SlotPos(7, 0), new SlotPos(8, 1))) {
            addButton(new MenuButton(s, ItemUtils.createStack(ItemTypes.EMERALD_BLOCK, (byte) 0, 1, Txt.f("§a§lComprar"), Arrays.asList(Txt.f("§fClique aqui para comprar"), Txt.f("§fo pacote por"), Txt.f(("§a§l" + preco + " " + SpongeVIP.moedashop.getName(preco)))))) {

                @Override
                public void click(Player p, Menu m, ClickType click) {
                    new VendaMenu(SpongeVIP.moedashop, pacote.buildItem(true, preco), pacote.getNameWithoutColor(), preco, pacote.getNameWithoutColor()) {

                        @Override
                        public void cancela(Player p) {
                            PlayersMenu.openPlayer(p, pac.getPagina());
                        }

                        @Override
                        public boolean compra(Player pa) {
                            boolean b = pac.give(pa);
                            if (b) {
                                SpongeVIP.sendMessageComprou(pa, "Pacote " + pac.getNameWithoutColor(), preco);

                                SchedulerUtils.runSync(new Runnable() {

                                    @Override
                                    public void run() {
                                        for (ComprouPacote com : SpongeVIP.comprapacote) {
                                            com.comprou(pa, pac);
                                        }
                                    }
                                }, 1);
                            }

                            return b;
                        }
                    }.open(p);
                }
            });
        }
        addButton(new MenuButton(new SlotPos(0, 0), ItemStackBuilder.of(ItemTypes.REPEATER).withName("§e§lVoltar").build()) {

            @Override
            public void click(Player p, Menu m, ClickType click) {
                PlayersMenu.openPlayer(p, pac.getPagina());
            }
        });
        if (pacote instanceof PacoteVIP) {
            List<String> vantagens = new ArrayList<String>();
            List<DyeColor> cores = new ArrayList<DyeColor>();
            cores.addAll(Arrays.asList(DyeColors.BLACK, DyeColors.BLUE, DyeColors.BROWN, DyeColors.CYAN, DyeColors.GRAY, DyeColors.GREEN, DyeColors.LIGHT_BLUE, DyeColors.LIME, DyeColors.MAGENTA, DyeColors.ORANGE, DyeColors.PINK, DyeColors.PURPLE, DyeColors.RED, DyeColors.SILVER, DyeColors.WHITE, DyeColors.YELLOW));
            for (String s : ((PacoteVIP) pacote).getVantagens()) {
                vantagens.add(s);
                if (vantagens.size() >= 3) {
                    ItemStack i = ItemStack.of(ItemTypes.STAINED_HARDENED_CLAY, 1);
                    DyeColor cor = cores.get(0);
                    i.offer(Keys.DYE_COLOR, cor);
                    cores.remove(cor);
                    ItemUtils.setItemName(i, Txt.f(" "));
                    for (String v : vantagens) {
                        ItemUtils.addLore(i, Txt.f("§b" + v.replace("&", "§")));
                    }

                    addButtonToSquare(new SlotPos(0, 1), new SlotPos(8, 1), new NothingButton(new SlotPos(0, 0), i));
                    vantagens.clear();
                }
            }
            if (!vantagens.isEmpty()) {
                ItemStack i = ItemStack.of(ItemTypes.STAINED_HARDENED_CLAY, 1);
                DyeColor cor = cores.get(new Random().nextInt(cores.size()));
                i.offer(Keys.DYE_COLOR, cor);
                cores.remove(cor);
                ItemUtils.setItemName(i, Txt.f(" "));
                for (String v : vantagens) {
                    ItemUtils.addLore(i, Txt.f("§b" + v));
                }

                addButtonToSquare(new SlotPos(0, 1), new SlotPos(8, 1), new NothingButton(new SlotPos(0, 0), i));
            }
            int dias = ((PacoteVIP) pacote).getDias();
            addButton(new NothingButton(new SlotPos(5, 0), ItemStackBuilder.of(ItemTypes.DIAMOND).withName("§e§lPacote VIP §f§l" + ((PacoteVIP) pacote).getGrupo()).build()));
            String tempo = dias + " dias";
            if (dias == PacoteVIP.PERMANENTE) {
                tempo = "Permanente";
            }
            addButton(new NothingButton(new SlotPos(3, 0), ItemStackBuilder.of(ItemTypes.CLOCK).withName("§b§l" + tempo).build()));
            int coins = ((PacoteVIP) pacote).getCoins();
            if (coins > 0) {
                addButton(new NothingButton(new SlotPos(2, 0), ItemStackBuilder.of(ItemTypes.DOUBLE_PLANT).withName("§6§l" + coins + " coins").build()));
            }
        }
        if (pacote instanceof PacoteItens) {
            addButton(new NothingButton(new SlotPos(4, 2), ItemUtils.createStack(ItemTypes.CHEST, (byte) 0, 1, Txt.f("§e§lItens"), Arrays.asList(Txt.f("§fItens que irá receber"), Txt.f("§fao obter o pacote!")))));
            for (ItemStack i : ((PacoteItens) pacote).getItens()) {
                addButtonToSquare(new SlotPos(0, 3), new SlotPos(8, 5), new NothingButton(new SlotPos(0, 0), i.copy()));
            }

        }
    }

}

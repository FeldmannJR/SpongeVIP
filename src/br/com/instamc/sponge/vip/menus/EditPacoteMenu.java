/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.menus;

import br.com.instamc.sponge.library.apis.ChatInputAPI;
import br.com.instamc.sponge.library.menu.ClickType;
import br.com.instamc.sponge.library.menu.Menu;
import br.com.instamc.sponge.library.menu.MenuButton;
import br.com.instamc.sponge.library.menu.MenuCloseAction;
import br.com.instamc.sponge.library.menu.buttons.NothingButton;
import br.com.instamc.sponge.library.menu.menus.ConfirmarMenu;
import br.com.instamc.sponge.library.utils.ItemUtils;
import br.com.instamc.sponge.library.utils.SchedulerUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteItens;
import br.com.instamc.sponge.vip.pacotes.PacoteVIP;
import java.util.Arrays;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class EditPacoteMenu extends Menu {
    
    final Pacote pacote;
    
    public EditPacoteMenu(Pacote p) {
        super("Editando", 6);
        this.pacote = p;
        addButton(new NothingButton(new SlotPos(4, 0), p.buildItem(true)));
        if (p instanceof PacoteItens) {
            
            addButton(buildEdit(pacote));
            for (ItemStack i : ((PacoteItens) pacote).getItens()) {
                addButtonToSquare(new SlotPos(0, 3), new SlotPos(8, 5), new NothingButton(new SlotPos(0, 0), i.copy()));
            }
            
        }
        if (p instanceof PacoteVIP) {
            addButton(buildGrupo(pacote));
            addButton(buildDias(pacote));
            addButton(buildCoins((PacoteVIP) p));
            addButton(buildDesc((PacoteVIP) p));
        }
        addButton(buildNome(pacote));
        addButton(buildPreco(pacote));
        addButton(buildIcone(pacote));
        addButton(buildDelete(pacote));
        addButton(resetSlot(pacote));
    }
    
    public static MenuButton buildDesc(PacoteVIP pacote) {
        return new MenuButton(new SlotPos(6, 2), ItemUtils.createStack(ItemTypes.PAPER, (byte) 1, 1, Txt.f("§c§lEdit Descs"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§feditar descs!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                new EditDescMenu(pacote).open(p);
                
            }
        };
    }

    public static MenuButton resetSlot(Pacote pacote) {
        return new MenuButton(new SlotPos(8, 1), ItemUtils.createStack(ItemTypes.GLASS_PANE, (byte) 1, 1, Txt.f("§c§lResetar Slot"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fresetar slot!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                pacote.setSlot(null);
                pacote.setPagina(null);
                PacoteManager.addPacote(pacote);
                p.sendMessage(Txt.f("§cResetado!"));
            }
        };
    }
    
    public static MenuButton buildCoins(PacoteVIP pacote) {
        return new MenuButton(new SlotPos(5, 2), ItemUtils.createStack(ItemTypes.DOUBLE_PLANT, (byte) 1, 1, Txt.f("§c§lSetar Coins"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar os coins do pacote!"), Txt.f("§6Atual: §c" + pacote.getCoins())))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                close(p);
                ChatInputAPI.inputPlayer(p, new ChatInputAPI.ChatAction(10) {
                    
                    @Override
                    public void inputText(Player p, String t) {
                        try {
                            int pre = Integer.valueOf(t);
                            if (pre <= 0) {
                                p.sendMessage(Txt.f("§cValor Inválido!"));
                                return;
                            }
                            pacote.setCoins(pre);
                            
                            PacoteManager.addPacote(pacote);
                            new EditPacoteMenu(pacote).open(p);
                            p.sendMessage(Txt.f("§aCoins setados para " + pre + " !"));
                            new EditPacoteMenu(pacote).open(p);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(Txt.f("§cValor Inválido!"));
                        }
                    }
                }, "Coins VIP Pacote " + pacote.getNome() + "");
                
            }
        };
        
    }
    
    public static MenuButton buildEdit(Pacote pacote) {
        return new MenuButton(new SlotPos(4, 2), ItemUtils.createStack(ItemTypes.CHEST_MINECART, (byte) 1, 1, Txt.f("§b§lSetar Itens"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar os itens do pacote!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                Menu set = new Menu("Setar Itens", 3);
                set.setMoveItens(true);
                for (ItemStack i : ((PacoteItens) pacote).getItens()) {
                    set.addNonButton(i.copy());
                }
                set.addClose(new MenuCloseAction() {
                    
                    @Override
                    public void closeMenu(Player p, Menu m) {
                        ((PacoteItens) pacote).setItens(m.getNonButtons());
                        PacoteManager.addPacote(pacote);
                        SchedulerUtils.runSync(new Runnable() {
                            
                            @Override
                            public void run() {
                                new EditPacoteMenu(pacote).open(p);
                            }
                        }, 5);
                        
                    }
                });
                set.open(p);
            }
        };
        
    }
    
    public static MenuButton buildPreco(Pacote pacote) {
        return new MenuButton(new SlotPos(5, 1), ItemUtils.createStack(ItemTypes.EMERALD, (byte) 1, 1, Txt.f("§b§lSetar Preço"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar o preço do pacote!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                close(p);
                ChatInputAPI.inputPlayer(p, new ChatInputAPI.ChatAction(10) {
                    
                    @Override
                    public void inputText(Player p, String t) {
                        try {
                            int pre = Integer.valueOf(t);
                            if (pre <= 0) {
                                p.sendMessage(Txt.f("§cValor Inválido!"));
                                return;
                            }
                            pacote.setPreco(pre);
                            PacoteManager.addPacote(pacote);
                            p.sendMessage(Txt.f("§aValor setado para " + pre + " !"));
                            new EditPacoteMenu(pacote).open(p);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(Txt.f("§cValor Inválido!"));
                        }
                    }
                }, "Valor Pacote " + pacote.getNome());
                
            }
        };
        
    }
    
    public static MenuButton buildDias(Pacote pacote) {
        return new MenuButton(new SlotPos(3, 2), ItemUtils.createStack(ItemTypes.CLOCK, (byte) 1, 1, Txt.f("§c§lSetar Dias"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar o tempo do pacote!"), Txt.f("§6Atual: §c" + ((PacoteVIP) pacote).getDias())))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                close(p);
                ChatInputAPI.inputPlayer(p, new ChatInputAPI.ChatAction(10) {
                    
                    @Override
                    public void inputText(Player p, String t) {
                        try {
                            int pre = Integer.valueOf(t);
                            if (pre < 0) {
                                p.sendMessage(Txt.f("§cValor Inválido!"));
                                return;
                            }
                            ((PacoteVIP) pacote).setDias(pre);
                            
                            PacoteManager.addPacote(pacote);
                            new EditPacoteMenu(pacote).open(p);
                            p.sendMessage(Txt.f("§aDias setados para " + pre + " !"));
                            new EditPacoteMenu(pacote).open(p);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(Txt.f("§cValor Inválido!"));
                        }
                    }
                }, "Dias VIP Pacote " + pacote.getNome() + ", 0 = PERMANENTE");
                
            }
        };
        
    }
    
    public static MenuButton buildNome(Pacote pacote) {
        return new MenuButton(new SlotPos(4, 1), ItemUtils.createStack(ItemTypes.NAME_TAG, (byte) 1, 1, Txt.f("§b§lSetar Nome"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar o nome do pacote!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                close(p);
                ChatInputAPI.inputPlayer(p, new ChatInputAPI.ChatAction(10) {
                    
                    @Override
                    public void inputText(Player p, String t) {
                        if (t.isEmpty() || t.length() > 30) {
                            p.sendMessage(Txt.f("§cNome muito grande!"));
                            return;
                        }
                        pacote.setNome(t);
                        PacoteManager.addPacote(pacote);
                        new EditPacoteMenu(pacote).open(p);
                        
                    }
                }, "Novo nome para o pacote " + pacote.getNome());
                
            }
        };
    }
    
    public static MenuButton buildDelete(Pacote pacote) {
        return new MenuButton(new SlotPos(8, 0), ItemUtils.createStack(ItemTypes.BARRIER, (byte) 0, 1, Txt.f("§c§lREMOVER PACOTE"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fREMOVER O PACOTE!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                new ConfirmarMenu(ItemStack.of(ItemTypes.BARRIER, 1), "Deletar Pacote " + pacote.getNome(), "DELETAR") {
                    
                    @Override
                    public void confirma(Player p) {
                        PacoteManager.delete(pacote);
                        close(p);
                        p.sendMessage(Txt.f("§cDeletado!"));
                    }
                    
                    @Override
                    public void recusa(Player p) {
                        close(p);
                    }
                }.open(p);
            }
        };
    }
    
    public static MenuButton buildGrupo(Pacote pacote) {
        return new MenuButton(new SlotPos(2, 1), ItemUtils.createStack(ItemTypes.DIAMOND, (byte) 1, 1, Txt.f("§6§lSetar Grupo VIP"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar o nome do grupo do PEX!"), Txt.f("§bAtual: §6" + ((PacoteVIP) pacote).getGrupo())))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                close(p);
                ChatInputAPI.inputPlayer(p, new ChatInputAPI.ChatAction(10) {
                    
                    @Override
                    public void inputText(Player p, String t) {
                        if (t.isEmpty() || t.length() > 30) {
                            p.sendMessage(Txt.f("§cNome muito grande!"));
                            return;
                        }
                        ((PacoteVIP) pacote).setGrupo(t);
                        PacoteManager.addPacote(pacote);
                        new EditPacoteMenu(pacote).open(p);
                        
                    }
                }, "Novo nome para o pacote " + pacote.getNome());
                
            }
        };
    }
    
    public static MenuButton buildIcone(Pacote pacote) {
        return new MenuButton(new SlotPos(3, 1), ItemUtils.createStack(ItemTypes.YELLOW_FLOWER, (byte) 1, 1, Txt.f("§b§lSetar Icone"), Arrays.asList(Txt.f("§fClique aqui para"), Txt.f("§fsetar o icone do pacote!")))) {
            
            @Override
            public void click(Player p, Menu m, ClickType click) {
                Menu set = new Menu("Setar Icone", 1);
                set.setMoveItens(true);
                
                for (int x : new int[]{0, 1, 2, 3, 5, 6, 7, 8}) {
                    set.addButton(new NothingButton(new SlotPos(x, 0), ItemStack.of(ItemTypes.BARRIER, 1)));
                    
                }
                if (pacote.getIcone() != null) {
                    set.addNonButton(pacote.getIcone().copy());
                }
                set.addClose(new MenuCloseAction() {
                    
                    @Override
                    public void closeMenu(Player p, Menu m) {
                        if (!m.getNonButtons().isEmpty()) {
                            ItemStack i = m.getNonButtons().get(0);
                            pacote.setIcone(i);
                            p.sendMessage(Txt.f("§aIcone setado!"));
                        } else {
                            
                            pacote.setIcone(null);
                            
                            p.sendMessage(Txt.f("§aIcone Removido!"));
                        }
                        PacoteDB.savePacote(pacote);
                        SchedulerUtils.runSync(new Runnable() {
                            
                            @Override
                            public void run() {
                                new EditPacoteMenu(pacote).open(p);
                            }
                        }, 5);
                        
                    }
                });
                set.open(p);
                
            }
        };
        
    }
    
}

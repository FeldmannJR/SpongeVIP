/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.pacotes;

import br.com.instamc.sponge.library.SpongeLib;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.library.utils.data.ItemStackSerializer;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.SpongeVIP;
import br.com.instamc.sponge.vip.data.DataSerializer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.item.inventory.ItemStack;
import static org.spongepowered.api.data.DataQuery.of;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public abstract class Pacote implements DataSerializable {

    String nome;
    int preco;
    ItemStack icone = null;
    int id = -1;
    Pagina pagina;

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }

    public void setIcone(ItemStack icone) {
        this.icone = icone;
    }

    public Pagina getPagina() {
        return pagina;
    }
    
    
    public static final DataQuery NOME = DataQuery.of("nome");
    public static final DataQuery ICONE = DataQuery.of("icone");
    public static final DataQuery PRECO = DataQuery.of("preco");
    public static final DataQuery CLASSE = DataQuery.of("classe");
    public static final DataQuery SLOT = DataQuery.of("slot");
    public static final DataQuery PAGINA = DataQuery.of("pagina");

    public int getPreco(Player p) {
        return preco;
    }
    SlotPos slot = null;

    public String getNome() {
        return nome;
    }

    public SlotPos getSlot() {
        return slot;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStack buildItem(boolean desc) {
        return buildItem(true, preco);
    }

    public ItemStack buildItem(Player pl) {
        return buildItem(true, getPreco(pl));
    }
    public String getNameWithoutColor(){
        return Txt.f(nome.replace("&", "§")).toPlain();
    }

    public ItemStack buildItem(boolean desc, int precoc) {
        ItemStack i = null;
        if (icone == null) {
            i = ItemStack.builder().itemType(ItemTypes.APPLE).build();
        } else {
            i = icone.copy();
        }
        i.offer(Keys.DISPLAY_NAME, Txt.f("§e§l" + nome.replace("&", "§")));

        if (desc) {

            i.offer(Keys.ITEM_LORE, Arrays.asList(Txt.f("§f§lPreço: §a§l" + precoc+" "+SpongeVIP.moedashop.getName(precoc))));

        }

        return i;
    }

    public ItemStack getIcone() {
        return icone;
    }

    public abstract DataContainer setData(DataContainer c);

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = new MemoryDataContainer();
        container.set(NOME, nome);
        if (icone != null) {
            container.set(ICONE, ItemStackSerializer.serializeItemStack(icone));
        }
        container.set(PRECO, preco);
        container.set(CLASSE, getClass().getSimpleName());
        if (slot != null) {
            container.set(SLOT, slot.getX() + ":" + slot.getY());
        } else {

        }
        if (pagina != null) {
            container.set(PAGINA, pagina.nome);
        }
        container = setData(container);
        return container;
    }

    public void setSlot(SlotPos slot) {
        this.slot = slot;
    }

    public boolean loadData(DataView data) {
        if (data.contains(ICONE)) {

            icone = ItemStackSerializer.deserializeItemStack(data.getString(ICONE).get());
        }

        if (data.contains(SLOT)) {
            Optional<String> str = data.getString(SLOT);
            if (str.isPresent()) {
                String[] split = str.get().split(":");
                if (split.length == 2) {
                    try {
                        int x = Integer.valueOf(split[0]);
                        int y = Integer.valueOf(split[1]);
                        this.slot = new SlotPos(x, y);
                    } catch (NumberFormatException ex) {

                    }
                }
            }

        }
        if (data.contains(PAGINA)) {
            for (Pagina p : PacoteManager.paginas) {
                if (p.nome.equals(data.getString(PAGINA).get())) {
                    this.pagina = p;
                }
            }
        }
        if (data.contains(NOME, PRECO)) {

            this.nome = data.getString(NOME).get();
            this.preco = data.getInt(PRECO).get();

            return true;
        }

        return false;
    }

    public abstract boolean give(Player p);

    public int getPreco() {
        return preco;
    }

    public static class Builder extends AbstractDataBuilder<Pacote> {

        public Builder() {
            super(Pacote.class, 1);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Optional<Pacote> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(CLASSE)) {
                String name = container.getString(CLASSE).get();
                for (PacoteType pacotes : PacoteType.values()) {

                    if (pacotes.getClasse().getSimpleName().equals(name)) {
                        Pacote pac = null;
                        try {
                            pac = pacotes.classe.newInstance();
                        } catch (InstantiationException ex) {
                            Logger.getLogger(Pacote.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(Pacote.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (pac != null) {
                            if (!pac.loadData(container)) {

                                return Optional.empty();
                            }
                            return Optional.of(pac);
                        }

                    }
                }

            }
            return Optional.empty();
        }
    }
}

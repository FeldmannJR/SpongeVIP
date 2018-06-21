/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.pacotes;

import br.com.instamc.sponge.library.SpongeLib;
import br.com.instamc.sponge.library.utils.InventoryUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.library.utils.data.ItemStackSerializer;
import br.com.instamc.sponge.library.utils.encode.Base64;
import br.com.instamc.sponge.vip.SpongeVIP;
import br.com.instamc.sponge.vip.data.DataSerializer;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.ArmorSlotType;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.slot.EquipmentSlot;

/**
 *
 * @author Carlos
 */
public class PacoteItens extends Pacote {

    List<ItemStack> itens = new ArrayList<ItemStack>();
    public static final DataQuery ITENS = DataQuery.of("itens");

    public List<ItemStack> getItens() {
        return itens;
    }

    public void setItens(List<ItemStack> itens) {
        this.itens = itens;
    }

    @Override
    public DataContainer setData(DataContainer c) {

        List<String> strl = new ArrayList();
        for (ItemStack i : itens) {
            strl.add(ItemStackSerializer.serializeItemStack(i));
        }

        c.set(ITENS, strl);
        return c;
    }

    @Override
    public ItemStack buildItem(boolean desc, int preco) {
        ItemStack i = super.buildItem(desc, preco); //To change body of generated methods, choose Tools | Templates.
        if (getIcone() == null) {
            if (!itens.isEmpty()) {
                ItemStack it = itens.get(0).copy();
                if (i.get(Keys.DISPLAY_NAME).isPresent()) {
                    it.offer(Keys.DISPLAY_NAME, i.getOrNull(Keys.DISPLAY_NAME));
                }
                if (i.get(Keys.ITEM_LORE).isPresent()) {
                    it.offer(Keys.ITEM_LORE, i.getOrNull(Keys.ITEM_LORE));
                }
                it.setQuantity(1);
                return it;
            }
        }
        return i;
    }

    @Override
    public boolean loadData(DataView data) {
        if (!super.loadData(data)) {
            return false;
        }
        if (data.contains(ITENS)) {
            for (String str : data.getStringList(ITENS).get()) {
                itens.add(ItemStackSerializer.deserializeItemStack(str));
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean give(Player p) {

        int empty = InventoryUtils.getEmpty(p);

        if (getItens().size() > empty) {
            SpongeVIP.sendMessage(p, "§c§lSeu inventário está lotado!, Esvazie!");
            return false;
        }
        for (ItemStack i : getItens()) {
            p.getInventory().offer(i.copy());
        }
        return true;
    }

}

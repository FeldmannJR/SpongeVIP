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
import br.com.instamc.sponge.library.utils.ItemUtils;
import br.com.instamc.sponge.library.utils.SchedulerUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

/**
 *
 * @author Carlos
 */
public class EditPaginaListMenu extends Menu {
    
    public EditPaginaListMenu() {
        super("Paginas", 6);
        for (Pagina pag : PacoteManager.paginas) {
            ItemStack it = pag.icone.copy();
            ItemUtils.setItemName(it, Txt.f("§e" + pag.nome));
            addButtonNextSlot(new MenuButton(new SlotPos(0, 0), it) {
                
                @Override
                public void click(Player p, Menu m, ClickType click) {
                    new EditPaginaMenu(pag).open(p);
                }
            });
        }
        
    }
    
}

/*
 *  _   __   _   _____   _____       ___       ___  ___   _____  
 * | | |  \ | | /  ___/ |_   _|     /   |     /   |/   | /  ___| 
 * | | |   \| | | |___    | |      / /| |    / /|   /| | | |     
 * | | | |\   | \___  \   | |     / / | |   / / |__/ | | | |     
 * | | | | \  |  ___| |   | |    / /  | |  / /       | | | |___  
 * |_| |_|  \_| /_____/   |_|   /_/   |_| /_/        |_| \_____| 
 * 
 * Classe criada por Carlos Andre Feldmann Junior
 * Apoio: Isaias Finger, Gabriel Slomka, Gabriel Augusto Souza
 * Skype: junior.feldmann
 * GitHub: https://github.com/feldmannjr
 * Facebook: https://www.facebook.com/carlosandre.feldmannjunior
 */
package br.com.instamc.sponge.vip.provisorio;

import br.com.instamc.sponge.library.menu.menus.venda.moedas.Moeda;
import br.com.instamc.sponge.vip.SpongeVIP;
import java.util.UUID;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

/**
 * @author Feldmann
 */
public class MCustomCoins extends Moeda {

    public MCustomCoins() {
        super("Online Coin", ItemStack.of(ItemTypes.CLOCK, 1), TextColors.AQUA);
    }

    @Override
    public boolean possui(UUID uuid, int i) {
    //    return SpongeVIP.coins.getCoins(uuid) >= i;
        return false;
    }

    @Override
    public void remove(UUID uuid, int i) {
    //    SpongeVIP.coins.removeCoins(uuid, i);
    }

    @Override
    public int get(UUID uuid) {
      //  return SpongeVIP.coins.getCoins(uuid);
    return 0;
    }

}

/*
 *  _   __   _   _____   _____       ___       ___  ___   _____  
 * | | |  \ | | /  ___/ |_   _|     /   |     /   |/   | /  ___| 
 * | | |   \| | | |___    | |      / /| |    / /|   /| | | |     
 * | | | |\   | \___  \   | |     / / | |   / / |__/ | | | |     
 * | | | | \  |  ___| |   | |    / /  | |  / /       | | | |___  
 * |_| |_|  \_| /_____/   |_|   /_/   |_| /_/        |_| \_____| 
 * 
 * Projeto feito por Carlos Andre Feldmann Junior, Isaias Finger e Gabriel Augusto Souza.
 */
package br.com.instamc.sponge.vip.pacotes;

import br.com.instamc.sponge.library.apis.EconAPI;
import br.com.instamc.sponge.library.utils.ItemUtils;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PermissionsUtils;
import br.com.instamc.sponge.vip.data.PacoteDB;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 *
 * @author Carlos
 */
public class PacoteVIP extends PacoteItens {

    String grupo = "";
    public static int PERMANENTE = 0;
    int dias = 0;
    int coins = 0;
    public static final DataQuery GRUPO = DataQuery.of("grupo");
    public static final DataQuery DIAS = DataQuery.of("dias");
    public static final DataQuery VANTAGENS = DataQuery.of("vantagens");
    public static final DataQuery COINS = DataQuery.of("coins");
    List<String> vantagens = new ArrayList<>();

    public String getGrupo() {
        return grupo;
    }

    public int getDias() {
        return dias;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public List<String> getVantagens() {
        return vantagens;
    }

    @Override
    public DataContainer setData(DataContainer c) {
        DataContainer dc = super.setData(c);
        dc.set(GRUPO, grupo);
        dc.set(VANTAGENS, vantagens);
        dc.set(DIAS, dias);
        dc.set(COINS, coins);
        return dc;
    }

    @Override
    public boolean give(Player p) {
        if (getGrupo().equals("")) {
            return false;
        }
        boolean b = super.give(p); //To change body of generated methods, choose Tools | Templates.
        if (!b) {
            return false;
        }

        PacoteDB.addVencimento(this, p.getUniqueId());

        PermissionsUtils.setGroup(p, getGrupo());
        if (coins > 0) {
            EconAPI.addMoney(p.getUniqueId(), coins);
        }
        return true;
    }

    @Override
    public ItemStack buildItem(Player p) {
        ItemStack i = super.buildItem(p);
        String tempo = "§b§lPERMANENTE";
        if (!isLifetime()) {
            tempo = "§6§l" + getDias() + " dias";
        }
        ItemUtils.addLore(i, Txt.f("§f§lTempo: " + tempo));
        if (getDesconto(p) > 0) {
            int desconto = (int) (double) (getDesconto(p) * 100);
            ItemUtils.addLore(i, Txt.f("§d§lDesconto de " + desconto + "%"));
        }
        return i;
    }

    public int getCoins() {
        return coins;
    }

    @Override
    public boolean loadData(DataView data) {
        if (!super.loadData(data)) {
            return false;
        }
        if (data.contains(COINS)) {
            this.coins = data.getInt(COINS).get();
        }
        if (data.contains(VANTAGENS)) {
            this.vantagens = data.getStringList(VANTAGENS).get();
        } else {
            this.vantagens = new ArrayList<String>();
        }
        if (data.contains(GRUPO, DIAS)) {
            this.dias = data.getInt(DIAS).get();
            this.grupo = data.getString(GRUPO).get();
            return true;
        }
        System.out.println("NAO TEM GRUPO,DIAS,VANTAGENS");
        return false;
    }

    public double getDesconto(Player p) {
        if (dias == 0) {
            return 0;
        }
        double desconto = 0;
        List<PacoteDB.Vencimento> vents = PacoteDB.getVencimentos(p.getUniqueId());
        for (PacoteDB.Vencimento v : vents) {
            if (v.getPacote() == id) {
                if (v.getStatus() == 1) {
                    desconto = 0.2;
                    break;
                }
                if (v.getStatus() == -1) {
                    long venceu = v.getVenceu().getTime() + (1000 * 60 * 60 * 24 * 3);
                    if (venceu >= System.currentTimeMillis()) {
                        if (desconto <= 0.1) {
                            desconto = 0.1;
                        }
                    }
                }
            }
        }
        return desconto;
    }

    @Override
    public int getPreco(Player p) {
        if (dias == 0) {
            return preco;
        }
        double desconto = getDesconto(p);
        double precod = preco - ((double) preco * desconto);
        return (int) precod;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public boolean isLifetime() {
        return dias == PERMANENTE;
    }

    public void setCoins(int pre) {
        this.coins = pre;
    }
}

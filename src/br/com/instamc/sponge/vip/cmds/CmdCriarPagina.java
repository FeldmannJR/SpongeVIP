/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.cmds;

import br.com.instamc.sponge.library.apis.ComandoAPI;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 *
 * @author Carlos
 */
public class CmdCriarPagina extends ComandoAPI {

    public CmdCriarPagina() {
        super(CommandType.OP, "criarpagina");
    }

    @Override
    public void onCommand(CommandSource cs, String[] args) {
        if (args.length < 2) {
            cs.sendMessage(Txt.f("§cUse /criarpagina linhas NOME"));
            return;
        }
        String nome = "";
        for (int x = 1; x < args.length; x++) {
            nome += args[x] + " ";
        }
        nome = nome.trim();
        int linhas = 1;
        try {
            linhas = Integer.valueOf(args[0]);
        } catch (NumberFormatException ex) {
            cs.sendMessage(Txt.f("§cLinhas inválidas!"));

            return;
        }
        if (linhas < 1 && linhas > 6) {
            cs.sendMessage(Txt.f("§c 1 <= Linhas <= 6"));
            return;
        }
        Pagina p = new Pagina(ItemStack.of(ItemTypes.PAPER, 1), nome, null, linhas);
        PacoteManager.addPagina(p);
        cs.sendMessage(Txt.f("§aPagina criada, edite no /shopadm ."));

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.cmds;

import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteType;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 *
 * @author Carlos
 */
public class CmdCriarPacote implements CommandExecutor {
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        
        PacoteType tipo = (PacoteType) args.getOne("tipo").get();
        Integer preco = (Integer) args.getOne("preco").get();
        String nome = (String) args.getOne("nome").get();
        
        try {
            Pacote p = tipo.getClasse().newInstance();
            p.setNome(nome);
            p.setPreco(preco);
            PacoteManager.addPacote(p);
            src.sendMessage(Txt.f("§aPacote adicionado!"));
            return CommandResult.success();
        } catch (InstantiationException ex) {
            Logger.getLogger(CmdCriarPacote.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CmdCriarPacote.class.getName()).log(Level.SEVERE, null, ex);
        }
        return CommandResult.empty();
    }
    
}

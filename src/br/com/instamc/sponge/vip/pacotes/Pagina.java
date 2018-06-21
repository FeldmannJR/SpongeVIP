/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.pacotes;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class Pagina {

    public ItemStack icone;
    public String nome;
    public SlotPos slot;
    public int linhas = 2;

    public Pagina(ItemStack icone, String nome, SlotPos slot,int linhas) {
        this.icone = icone;
        this.nome = nome;
        this.linhas = linhas;
        this.slot = slot;
    }

}

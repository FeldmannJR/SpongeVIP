/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.pacotes;

/**
 *
 * @author Carlos
 */
public enum PacoteType {

    VIP(PacoteVIP.class),
    ITENS(PacoteItens.class),
    PERMISSION(PacotePermission.class);
    Class<? extends Pacote> classe;

    private PacoteType(Class<? extends Pacote> classe) {
        this.classe = classe;
    }

    public Class<? extends Pacote> getClasse() {
        return classe;
    }

}

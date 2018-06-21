/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip;

import br.com.instamc.sponge.vip.data.PacoteDB;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteType;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.item.inventory.property.SlotPos;

/**
 *
 * @author Carlos
 */
public class PacoteManager {

    private static List<Pacote> pacotes = new ArrayList();
    public static List<Pagina> paginas = new ArrayList();

    public static void init() {

        for (Pagina p : PacoteDB.loadPaginas()) {
            paginas.add(p);
        }
        for (Pacote p : PacoteDB.loadPacotes()) {
            pacotes.add(p);
        }
        for (Pacote p : new ArrayList<Pacote>(getPacotes())) {
            boolean tem = false;
            if (p.getPagina() == null) {
                continue;
            }
            for (Pagina pag : paginas) {
                if (pag.nome.equals(p.getPagina().nome)) {
                    tem = true;
                }
            }
            if (!tem) {
                p.setPagina(null);
                p.setSlot(null);
                addPacote(p);
            }
        }

    }

    public static Optional<Pacote> getPacoteById(int id) {
        for (Pacote p : pacotes) {
            if (p.getId() == id) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public static List<Pacote> getPacotesByType(PacoteType tipo) {
        ArrayList<Pacote> pac = new ArrayList();
        for (Pacote p : pacotes) {
            if (p.getClass().equals(tipo.getClasse())) {
                pac.add(p);
            }
        }
        return pac;
    }

    public static void addPagina(Pagina p) {
        PacoteDB.savePagina(p);
        for (Pagina r : new ArrayList<Pagina>(paginas)) {
            if (r.nome.equals(p.nome)) {
                paginas.remove(r);
            }
        }
        paginas.add(p);
    }

    public static void addPacote(Pacote p) {
        PacoteDB.savePacote(p);
        for (Pacote r : new ArrayList<Pacote>(pacotes)) {
            if (r.getId() == p.getId()) {
                pacotes.remove(r);
            }
        }
        pacotes.add(p);
    }

    public static List<Pacote> getPacotes() {
        return pacotes;
    }

    public static void delete(Pacote pacote) {
        for (Pacote r : new ArrayList<Pacote>(pacotes)) {
            if (r.getId() == pacote.getId()) {
                pacotes.remove(r);
            }
        }
        PacoteDB.removePacote(pacote);
    }

    public static void delete(Pagina p) {
        for (Pagina r : new ArrayList<Pagina>(paginas)) {
            if (r.nome.equals(p.nome)) {
                paginas.remove(r);
            }
        }
        for (Pacote pac : new ArrayList<Pacote>(getPacotes())) {
            if (pac.getPagina() != null) {
                if (pac.getPagina().nome.equalsIgnoreCase(p.nome)) {
                    pac.setPagina(null);
                    pac.setSlot(null);
                    addPacote(pac);
                }
            }
        }
        PacoteDB.removePagina(p);
    }

}

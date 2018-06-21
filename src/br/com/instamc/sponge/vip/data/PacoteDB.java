/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.instamc.sponge.vip.data;

import br.com.instamc.sponge.library.DB;
import br.com.instamc.sponge.library.SpongeLib;
import br.com.instamc.sponge.library.utils.Txt;
import br.com.instamc.sponge.library.utils.data.ItemStackSerializer;
import br.com.instamc.sponge.vip.PacoteManager;
import br.com.instamc.sponge.vip.PermissionsUtils;
import br.com.instamc.sponge.vip.SpongeVIP;
import br.com.instamc.sponge.vip.pacotes.Pacote;
import br.com.instamc.sponge.vip.pacotes.PacoteVIP;
import br.com.instamc.sponge.vip.pacotes.Pagina;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.leaping.permissionsex.PermissionsEx;
import ninja.leaping.permissionsex.sponge.PermissionsExPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.service.permission.PermissionService;

/**
 *
 * @author Carlos
 */
public class PacoteDB {

    public static Connection getConnection() {
        return DB.getConnection(SpongeVIP.banco);
    }
    static int VENCEU = -1;
    static int INATIVO = 0;
    static int ATIVO = 1;

    public static void init() {
        try {
            getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `pacotes` ( `id` INT NOT NULL AUTO_INCREMENT , `data` BLOB NOT NULL , PRIMARY KEY (`id`) ) ENGINE = InnoDB;");
            /*
             STATUS
             -1 VENCEU
             0 INATIVO
             1 ATIVO            
             */

            getConnection().createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `vencimentos` "
                    + "( `id` INT NOT NULL AUTO_INCREMENT ,"
                    + " `uuid` varchar(60),"
                    + " `pacoteid` integer,"
                    + " `grupo` varchar(50), "
                    + " `comeca` TIMESTAMP, "
                    + " `dias` INTEGER ,"
                    + " `status` INTEGER DEFAULT 0,"
                    + " `venceu` TIMESTAMP,"
                    + "PRIMARY KEY (`id`) ) ENGINE = InnoDB;");

            getConnection().createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `paginas` "
                    + "( `nome` VARCHAR(200) ,"
                    + " `slot` VARCHAR(10),"
                    + " `icone` TEXT,"
                    + "`linhas` INT DEFAULT 2,"
                    + "PRIMARY KEY (`nome`) ) ENGINE = InnoDB;");

        } catch (SQLException ex) {
            erroMySql(ex);
        }
    }

    public static void savePagina(Pagina p) {
        try {

            PreparedStatement st = getConnection().prepareStatement("INSERT INTO paginas (`nome`,`slot`,`icone`,`linhas`) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE slot = ?, icone = ?, linhas = ?");
            st.setString(1, p.nome);
            if (p.icone != null) {
                st.setString(3, ItemStackSerializer.serializeItemStack(p.icone));
                st.setString(6, ItemStackSerializer.serializeItemStack(p.icone));
            } else {
                st.setString(3, null);
                st.setString(5, null);

            }
            st.setInt(4, p.linhas);
            st.setInt(7, p.linhas);
            if (p.slot != null) {
                st.setString(2, p.slot.getX() + ";" + p.slot.getY());
                st.setString(5, p.slot.getX() + ";" + p.slot.getY());
            } else {
                st.setString(2, null);
                st.setString(5, null);
            }

            st.execute();

        } catch (SQLException ex) {
            erroMySql(ex);
        }

    }

    public static void savePacote(Pacote p) {
        try {
            if (p.getId() != -1) {

                PreparedStatement st = getConnection().prepareStatement("UPDATE `pacotes` set `data` = ? WHERE ID = " + p.getId());
                st.setString(1, DataSerializer.serializePlayerData(p));
                st.execute();

            } else {
                PreparedStatement st = getConnection().prepareStatement("INSERT INTO `pacotes` (`data`) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
                st.setString(1, DataSerializer.serializePlayerData(p));
                st.execute();
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            erroMySql(ex);
        }

    }

    public static List<Pagina> loadPaginas() {
        List<Pagina> pacotes = new ArrayList();
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM `paginas`");
            while (rs.next()) {

                String nome = rs.getString("nome");
                String slot = rs.getString("slot");
                ItemStack item = null;
                if (rs.getString("icone") != null) {
                    item = ItemStackSerializer.deserializeItemStack(rs.getString("icone"));
                }
                SlotPos pos = null;
                int linhas = rs.getInt("linhas");
                if (slot != null) {
                    if (slot.contains(";")) {
                        String[] split = slot.split(";");
                        if (split.length == 2) {

                            try {

                                int x = Integer.valueOf(split[0]);
                                int z = Integer.valueOf(split[1]);
                                pos = new SlotPos(x, z);
                            } catch (NumberFormatException ex) {

                            }

                        }
                    }

                }
                pacotes.add(new Pagina(item, nome, pos, linhas));

            }
        } catch (SQLException ex) {
            erroMySql(ex);
        }
        return pacotes;
    }

    public static List<Pacote> loadPacotes() {
        List<Pacote> pacotes = new ArrayList();
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM `pacotes`");
            while (rs.next()) {

                Optional<Pacote> pac = DataSerializer.deserializePlayerData(rs.getString("data"));
                if (pac.isPresent()) {
                    Pacote p = pac.get();
                    p.setId(rs.getInt("id"));
                    pacotes.add(p);

                }

            }
        } catch (SQLException ex) {
            erroMySql(ex);
        }
        return pacotes;
    }

    public static Vencimento getAtivo(List<Vencimento> vencimentos) {
        for (Vencimento v : vencimentos) {
            if (v.status == ATIVO) {
                return v;
            }
        }
        return null;
    }

    public static boolean checkVenceu(Vencimento ativo) {
        if (ativo.dias == PacoteVIP.PERMANENTE) {
            return false;
        }
        Timestamp termina = new Timestamp(ativo.comeca.getTime() + (ativo.dias * 24L * 60L * 60L * 1000L));
        Timestamp agora = new Timestamp(System.currentTimeMillis());
        if (termina.before(agora)) {
            try {
                getConnection().createStatement().executeUpdate("UPDATE vencimentos set `status`=" + VENCEU + ", venceu = NOW() where id = " + ativo.id);
            } catch (SQLException ex) {
                erroMySql(ex);
            }
            ativo.status = VENCEU;
            ativo.venceu = new Timestamp(System.currentTimeMillis());
            return true;

        }
        return false;
    }

    public static boolean alocaOutroVip(Player p, List<Vencimento> vencimentos) {
        for (Vencimento v : vencimentos) {
            if (v.status == INATIVO) {
                v.status = ATIVO;
                v.comeca = new Timestamp(System.currentTimeMillis());
                PermissionsUtils.setGroup(p, v.grupo);
                try {
                    getConnection().createStatement().executeUpdate("UPDATE vencimentos set `status`=" + ATIVO + ", comeca = NOW() where id = " + v.id);

                } catch (SQLException ex) {
                    erroMySql(ex);
                }

                return true;

            }

        }
        return false;
    }

    public static boolean hasAnyVip(List<Vencimento> vencimentos) {
        for (Vencimento v : vencimentos) {
            if (v.status == INATIVO) {
                return true;
            }
        }
        return false;
    }

    public static void mandaRenovar(Player p, List<Vencimento> v) {
        Vencimento ativo = getAtivo(v);
        if (ativo != null) {
            if (ativo.getDias() != PacoteVIP.PERMANENTE) {
                Optional<Pacote> pac = PacoteManager.getPacoteById(ativo.getPacote());
                if (pac.isPresent()) {
                    SpongeVIP.sendMessage(p, "§dVocê pode renovar seu §f" + pac.get().getNameWithoutColor() + "§d com §6§l20% de desconto§d até expirar! §c§lAPROVEITE!");
                    return;
                }

            }

        } else {
            for (Vencimento ven : v) {
                if (ven.status == VENCEU) {
                    if (ven.getDias() != PacoteVIP.PERMANENTE) {
                        Optional<Pacote> pac = PacoteManager.getPacoteById(ven.getId());
                        if (pac.isPresent()) {

                            long venceu = ven.getVenceu().getTime() + (1000 * 60 * 60 * 24 * 3);
                            if (venceu >= System.currentTimeMillis()) {
                                long dias = (venceu - System.currentTimeMillis()) / 1000 / 60 / 60 / 24;
                                String tem = "";
                                if (dias == 1) {
                                    tem = "1 dia";
                                } else if (dias > 0) {
                                    tem = dias + " dias";
                                } else {
                                    tem = "menos de 1 dia";
                                }
                                SpongeVIP.sendMessage(p, "§bVocê tem " + tem + " para renovar seu §f" + pac.get().getNameWithoutColor() + "§b com §6§l10% de desconto§b! §c§lAPROVEITE!");

                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void checkVencimentosOnLogin(Player p) {
        List<Vencimento> v = getVencimentos(p.getUniqueId());
        Vencimento ativo = getAtivo(v);

        if (ativo != null) {
            if (checkVenceu(ativo)) {
                SpongeVIP.sendMessage(p, "");
                SpongeVIP.sendMessage(p, "§cSeu VIP Expirou! :(");
                SpongeVIP.sendMessage(p, "");

                if (hasAnyVip(v)) {
                    alocaOutroVip(p, v);
                } else {
                    PermissionsUtils.setGroup(p, "default");
                }
            } else {
                if (ativo.dias != PacoteVIP.PERMANENTE) {
                    long add = 24 * 60 * 60 * 1000;
                    add *= ativo.dias;
                    long dif = (ativo.comeca.getTime() + add) - System.currentTimeMillis();
                    long dias = dif / 1000L / 60L / 60L / 24L;
                    Optional<Pacote> pac = PacoteManager.getPacoteById(ativo.getPacote());
                    String possui = "menos de um dia";
                    if (dias == 1) {
                        possui = "1 dia";

                    } else if (dias > 1) {
                        possui = dias + " dias";
                    }
                    if (pac.isPresent()) {

                        SpongeVIP.sendMessage(p, "§fVocê possuí §6" + possui + " §fde §6" + pac.get().getNome().replace("&", "§") + "§f!");
                    } else {
                        SpongeVIP.sendMessage(p, "§fVocê possuí §c" + possui + " §fde §6VIP§f!");

                    }
                }
            }
        } else {

            if (hasAnyVip(v)) {
                alocaOutroVip(p, v);
            }
        }
        mandaRenovar(p, v);
    }

    public static void removePacote(Pacote pacote) {
        try {
            getConnection().createStatement().execute("DELETE FROM pacotes WHERE id = " + pacote.getId());
        } catch (SQLException ex) {
            Logger.getLogger(PacoteDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void removePagina(Pagina p) {
        try {
            getConnection().createStatement().execute("DELETE FROM paginas WHERE nome = '" + p.nome + "'");
        } catch (SQLException ex) {
            Logger.getLogger(PacoteDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(Player p, String msg) {
        p.sendMessage(Txt.f(msg));
    }

    public static boolean addVencimento(PacoteVIP pacote, UUID uid) {
        List<Vencimento> v = getVencimentos(uid);
        Vencimento ativo = getAtivo(v);

        if (ativo != null) {

            if (!checkVenceu(ativo)) {
                int sobrou;
                if (ativo.dias != PacoteVIP.PERMANENTE) {
                    long add = 1000 * 60 * 60 * 24;
                    add *= ativo.dias;
                    Timestamp termina = new Timestamp(ativo.comeca.getTime() + add);
                    Timestamp agora = new Timestamp(System.currentTimeMillis());
                    long dif = termina.getTime() - agora.getTime();
                    long horas = dif / 1000 / 60 / 60;
                    long dias = horas / 24;
                    horas = horas % 24;
                    sobrou = (int) dias;
                    if (horas > 12) {
                        sobrou++;
                    }

                    if (sobrou <= 0) {
                        sobrou = 1;
                    }

                } else {
                    sobrou = 0;
                }
                ativo.status = INATIVO;
                ativo.dias = sobrou;
                if (pacote.getGrupo().equals(ativo.grupo)) {
                    ativo.status = ATIVO;
                    ativo.dias += pacote.getDias();
                } else if (ativo.dias != 0) {
                    ativo.status = VENCEU;
                    ativo.venceu = new Timestamp(System.currentTimeMillis());
                    try {
                        getConnection().createStatement().executeUpdate("UPDATE vencimentos set `status` = " + ativo.status + ",`venceu` = NOW() where id = " + ativo.id);
                    } catch (SQLException ex) {
                        erroMySql(ex);
                    }
                }
                if (ativo.status != VENCEU) {
                    try {
                        getConnection().createStatement().executeUpdate("UPDATE vencimentos set `status` = " + ativo.status + ",`dias` = " + ativo.dias + " where id = " + ativo.id);
                    } catch (SQLException ex) {
                        erroMySql(ex);
                    }
                }

                if (ativo.status == ATIVO) {
                    return true;
                }

            }
        }

        try {
            getConnection().createStatement().executeUpdate(
                    "INSERT INTO vencimentos "
                    + "("
                    + "`uuid`,"
                    + "`pacoteid`,"
                    + "`grupo`,"
                    + "`comeca`,"
                    + "`dias`,"
                    + "`status`)"
                    //
                    + " VALUES("
                    + "'" + uid.toString() + "',"
                    + pacote.getId() + ","
                    + "'" + pacote.getGrupo() + "',"
                    + "NOW(),"
                    + pacote.getDias() + ","
                    + ATIVO + ""
                    + ")");
        } catch (SQLException ex) {
            erroMySql(ex);
        }

        return true;
    }

    public static List<Vencimento> getVencimentos(UUID uid) {
        List<Vencimento> vencis = new ArrayList<>();
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM vencimentos WHERE uuid = '" + uid.toString() + "'");
            while (rs.next()) {

                vencis.add(new Vencimento(rs.getInt("id"), rs.getInt("dias"), rs.getInt("pacoteid"), rs.getTimestamp("comeca"), rs.getTimestamp("venceu"), rs.getInt("status"), uid, rs.getString("grupo")));

            }
        } catch (SQLException ex) {
            erroMySql(ex);
        }

        return vencis;
    }

    public static void erroMySql(Exception ex) {
        ex.printStackTrace();
        Sponge.getServer().shutdown(Txt.f("§cOcorreu um erro, entre em contato com a staff!"));

    }

    public static class Vencimento {

        int id;
        int dias;
        int pacote;
        Timestamp comeca;
        Timestamp venceu;
        int status;
        UUID uid;
        String grupo;

        public int getPacote() {
            return pacote;
        }

        public int getDias() {
            return dias;
        }

        public Vencimento(int id, int dias, int pacote, Timestamp comeca, Timestamp venceu, int status, UUID uid, String grupo) {
            this.id = id;
            this.dias = dias;
            this.pacote = pacote;
            this.comeca = comeca;
            this.status = status;
            this.venceu = venceu;
            this.uid = uid;
            this.grupo = grupo;
        }

        public Timestamp getVenceu() {
            return venceu;
        }

        public int getStatus() {
            return status;
        }

        public int getId() {
            return id;
        }

        public String getDiasRestantes() {
            if (dias == PacoteVIP.PERMANENTE) {
                return "Vitálicio";
            }
            long add = 24 * 60 * 60 * 1000;
            add *= dias;
            long dif = (comeca.getTime() + add) - System.currentTimeMillis();
            long dias = dif / 1000 / 60 / 60 / 24;
            String possui = "menos de um dia";
            if (dias == 1) {
                possui = "1 dia";

            } else if (dias > 1) {
                possui = dias + " dias";
            }
            return possui;
        }

    }
}

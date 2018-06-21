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

import br.com.instamc.sponge.vip.data.PacoteDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongepowered.api.entity.living.player.Player;

/**
 * @author Feldmann
 */
public class CustomCoins {

    public CustomCoins() {
        try {
            PacoteDB.getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS customcoins (uuid VARCHAR(200) PRIMARY KEY,coins INTEGER UNSIGNED)");
        } catch (SQLException ex) {
            Logger.getLogger(CustomCoins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getCoins(UUID uid) {
        try {
            ResultSet rs1 = PacoteDB.getConnection().createStatement().executeQuery("SELECT * FROM customcoins WHERE `uuid` ='" + uid.toString() + "'");
            if (rs1.next()) {
                return rs1.getInt("coins");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CustomCoins.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;

    }

    public void addCoins(UUID p, int coins) {
        try {
            PacoteDB.getConnection().createStatement().execute("INSERT INTO customcoins (`uuid`,`coins`) VALUES('" + p.toString() + "'," + coins + ") ON DUPLICATE KEY UPDATE `coins` = `coins` + " + coins);
        } catch (SQLException ex) {
            Logger.getLogger(CustomCoins.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void removeCoins(UUID id, int money) {
        try {
            PacoteDB.getConnection().createStatement().execute("INSERT INTO customcoins (`uuid`,`coins`) VALUES('" + id.toString() + "'," + money + ") ON DUPLICATE KEY UPDATE `coins` = `coins` - " + money);

        } catch (SQLException ex) {
            Logger.getLogger(CustomCoins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

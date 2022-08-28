package me.botsko.prism.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.botsko.prism.Prism;

public class Settings {

    /**
     * 
     * @param player
     * @param key
     * @return
     */
    public static String getPlayerKey(Player player, String key) {
        return player.getName() + "." + key;
    }

    /**
     * 
     * @param key
     */
    public static void deleteSetting(String key) {
        deleteSetting( key, null );
    }

    /**
     * 
     * @param key
     */
    public static void deleteSetting(String key, Player player) {
        String prefix = Prism.config.getString("prism.mysql.prefix");
        try (
            Connection conn = Prism.dbc();
            PreparedStatement s = conn.prepareStatement("DELETE FROM " + prefix + "meta WHERE k = ?")
        ) {
            String finalKey = key;
            if( player != null ) {
                finalKey = getPlayerKey( player, key );
            }
            s.setString( 1, finalKey );
            s.executeUpdate();
        } catch ( final SQLException e ) {
            // plugin.logDbError( e );
        }
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public static void saveSetting(String key, String value) {
        saveSetting( key, value, null );
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public static void saveSetting(String key, String value, Player player) {
        String prefix = Prism.config.getString("prism.mysql.prefix");
        try (
            Connection conn = Prism.dbc();
            PreparedStatement s = conn.prepareStatement("DELETE FROM " + prefix + "meta WHERE k = ?");
            PreparedStatement s2 = conn.prepareStatement("INSERT INTO " + prefix + "meta (k,v) VALUES (?,?)")
        ) {

            String finalKey = key;
            if( player != null ) {
                finalKey = getPlayerKey( player, key );
            }

            s.setString( 1, finalKey );
            s.executeUpdate();

            s2.setString( 1, finalKey );
            s2.setString( 2, value );
            s2.executeUpdate();

        } catch ( final SQLException e ) {
            // plugin.logDbError( e );
        }
    }

    /**
     * 
     * @param key
     * @return
     */
    public static String getSetting(String key) {
        return getSetting( key, null );
    }

    /**
     * 
     * @param key
     * @return
     */
    public static String getSetting(String key, Player player) {
        String prefix = Prism.config.getString("prism.mysql.prefix");
        String value = null;
        try (
            Connection conn = Prism.dbc();
            PreparedStatement s = conn.prepareStatement("SELECT v FROM " + prefix + "meta WHERE k = ? LIMIT 0,1")
        ) {

            String finalKey = key;
            if( player != null ) {
                finalKey = getPlayerKey( player, key );
            }

            s.setString( 1, finalKey );
            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    value = rs.getString("v");
                }
            }

        } catch ( final SQLException e ) {
            // plugin.logDbError( e );
        }
        return value;
    }
}

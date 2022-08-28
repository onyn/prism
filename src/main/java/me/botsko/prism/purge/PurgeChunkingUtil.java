package me.botsko.prism.purge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.botsko.prism.Prism;

public class PurgeChunkingUtil {

    /**
     * 
     * @param playername
     */
    public static int getMinimumPrimaryKey() {
        String prefix = Prism.config.getString("prism.mysql.prefix");
        int id = 0;
        try (
            Connection conn = Prism.dbc();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT MIN(id) FROM " + prefix + "data")
        ) {
            if( rs.first() ) {
                id = rs.getInt( 1 );
            }

        } catch ( final SQLException ignored ) {

        }
        return id;
    }

    /**
     * 
     * @param playername
     */
    public static int getMaximumPrimaryKey() {
        String prefix = Prism.config.getString("prism.mysql.prefix");
        int id = 0;
        try (
            Connection conn = Prism.dbc();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT id FROM " + prefix + "data ORDER BY id DESC LIMIT 1;")
        ) {
            if( rs.first() ) {
                id = rs.getInt( 1 );
            }

        } catch ( final SQLException ignored ) {

        }
        return id;
    }
}

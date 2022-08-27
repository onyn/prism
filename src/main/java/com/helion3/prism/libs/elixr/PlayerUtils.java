package com.helion3.prism.libs.elixr;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerUtils {

    public static void resetPlayer(Player p) {
        p.closeInventory();
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        InventoryUtils.updateInventory(p);
        if (!p.isDead()) {
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);

            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }

            p.setFireTicks(0);
        }

        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        p.setAllowFlight(false);
        p.setFlying(false);
    }

    /**
     * http://forums.bukkit.org/threads/directions.91550/#post-1265631
     */
    public static BlockFace getPlayerFacing(Player player) {
        float y = player.getLocation().getYaw();
        if (y < 0.0F) {
            y += 360.0F;
        }

        y %= 360.0F;
        int i = (int) ((double) (y + 8.0F) / 22.5);
        if (i == 0) {
            return BlockFace.WEST;
        } else if (i == 1) {
            return BlockFace.NORTH_WEST;
        } else if (i == 2) {
            return BlockFace.NORTH_WEST;
        } else if (i == 3) {
            return BlockFace.NORTH_WEST;
        } else if (i == 4) {
            return BlockFace.NORTH;
        } else if (i == 5) {
            return BlockFace.NORTH_EAST;
        } else if (i == 6) {
            return BlockFace.NORTH_EAST;
        } else if (i == 7) {
            return BlockFace.NORTH_EAST;
        } else if (i == 8) {
            return BlockFace.EAST;
        } else if (i == 9) {
            return BlockFace.SOUTH_EAST;
        } else if (i == 10) {
            return BlockFace.SOUTH_EAST;
        } else if (i == 11) {
            return BlockFace.SOUTH_EAST;
        } else if (i == 12) {
            return BlockFace.SOUTH;
        } else if (i == 13) {
            return BlockFace.SOUTH_WEST;
        } else if (i == 14) {
            return BlockFace.SOUTH_WEST;
        } else if (i == 15) {
            return BlockFace.SOUTH_WEST;
        }
        return BlockFace.WEST;
    }
}

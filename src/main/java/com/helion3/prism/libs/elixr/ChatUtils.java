package com.helion3.prism.libs.elixr;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static void notifyNearby(Location loc, int radius, String msg) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (loc.getWorld().equals(p.getWorld()) && loc.distance(p.getLocation()) <= (double) radius) {
                p.sendMessage(msg);
            }
        }
    }
}

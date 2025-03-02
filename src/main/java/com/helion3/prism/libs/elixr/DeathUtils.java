package com.helion3.prism.libs.elixr;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class DeathUtils {

    /**
     * Returns the name of what caused an entity to die.
     */
    public static String getCauseNiceName(Entity entity) {
        EntityDamageEvent e = entity.getLastDamageCause();
        if (e == null) {
            return "unknown";
        }

        // Determine the root cause
        EntityDamageEvent.DamageCause damageCause = e.getCause();
        Entity killer = null;

        // If was damaged by an entity
        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entity.getLastDamageCause();
            // Arrow?
            if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();
                ProjectileSource source = arrow.getShooter();
                if (source instanceof Player) {
                    killer = (Player) source;
                }
            } else {
                killer = entityDamageByEntityEvent.getDamager();
            }
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Detect additional suicide. For example, when you potion
            // yourself with instant damage it doesn't show as suicide.
            if (killer instanceof Player) {
                // Themself
                if (killer.getName().equals(player.getName())) {
                    return "suicide";
                }
                // translate bukkit events to nicer names
                if (damageCause.equals(DamageCause.ENTITY_ATTACK) || damageCause.equals(DamageCause.PROJECTILE)) {
                    return "pvp";
                }
            }
        }

        // Causes of death for either entities or players
        if (damageCause.equals(DamageCause.ENTITY_ATTACK)) {
            return "mob";
        } else if (damageCause.equals(DamageCause.PROJECTILE)) {
            return "skeleton";
        } else if (damageCause.equals(DamageCause.ENTITY_EXPLOSION)) {
            return "creeper";
        } else if (damageCause.equals(DamageCause.CONTACT)) {
            return "cactus";
        } else if (damageCause.equals(DamageCause.BLOCK_EXPLOSION)) {
            return "tnt";
        } else if (damageCause.equals(DamageCause.FIRE) || damageCause.equals(DamageCause.FIRE_TICK)) {
            return "fire";
        } else if (damageCause.equals(DamageCause.MAGIC)) {
            return "potion";
        }
        return damageCause.name().toLowerCase();
    }

    /**
     * Returns the name of the attacker, whether mob or player.
     */
    public static String getAttackerName(Entity victim) {

        // Determine base cause
        String cause = getCauseNiceName(victim);

        if (victim instanceof Player) {
            Player killer = ((Player) victim).getKiller();
            if (killer != null) {
                return killer.getName();
            }
        }

        if (cause.equals("mob")) {

            Entity killer = ((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager();

            // Playa!
            if (killer instanceof Player) {
                return killer.getName();

            } else if (killer instanceof Skeleton) {
                // Which skeleton type?
                Skeleton skele = (Skeleton) killer;
                return skele.getSkeletonType() == SkeletonType.WITHER ? "witherskeleton" : "skeleton";

            } else if (killer instanceof Arrow) {
                // Shot!
                return "skeleton";

            } else if (killer instanceof Wolf) {
                // Aggressive wolves
                Wolf wolf = (Wolf) killer;
                if (wolf.isTamed()) {
                    if (wolf.getOwner() instanceof Player || wolf.getOwner() instanceof OfflinePlayer) {
                        return "pvpwolf";
                    } else {
                        return "wolf";
                    }
                } else {
                    return "wolf";
                }
            } else {
                return killer.getType().getName().toLowerCase();
            }
        }
        return cause;
    }

    /**
     * Returns the name of the attacker, whether mob or player.
     */
    public static String getVictimName(Entity victim) {

        if (victim instanceof Player) {
            return victim.getName();
        } else if (victim instanceof Skeleton) {
            // Which skeleton type?
            Skeleton skele = (Skeleton) victim;
            return skele.getSkeletonType() == SkeletonType.WITHER ? "witherskeleton" : "skeleton";
        } else if (victim instanceof Arrow) {
            // Shot!
            return "skeleton";
        } else if (victim instanceof Wolf) {
            // Aggressive wolves
            Wolf wolf = (Wolf) victim;
            if (wolf.isTamed()) {
                if (wolf.getOwner() instanceof Player || wolf.getOwner() instanceof OfflinePlayer) {
                    return "pvpwolf";
                } else {
                    return "wolf";
                }
            } else {
                return "wolf";
            }
        } else {
            return victim.getType().getName().toLowerCase();
        }
    }

    /**
     * Determines the owner of a tamed wolf.
     */
    public static String getTameWolfOwner(EntityDeathEvent event) {
        String owner = "";
        Entity killer = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
        if (killer instanceof Wolf) {
            Wolf wolf = (Wolf) killer;
            if (wolf.isTamed()) {
                if (wolf.getOwner() instanceof Player) {
                    owner = wolf.getOwner().getName();
                }

                if (wolf.getOwner() instanceof OfflinePlayer) {
                    owner = wolf.getOwner().getName();
                }
            }
        }

        return owner;
    }

    /**
     * Determines the weapon used to kill an entity.
     */
    public static String getWeapon(Player p) {
        String death_weapon = "";
        if (p.getKiller() instanceof Player) {
            ItemStack weapon = p.getKiller().getItemInHand();
            death_weapon = weapon.getType().toString().toLowerCase();
            death_weapon = death_weapon.replaceAll("_", " ");
            if (death_weapon.equalsIgnoreCase("air")) {
                death_weapon = " hands";
            }
        }
        return death_weapon;
    }
}

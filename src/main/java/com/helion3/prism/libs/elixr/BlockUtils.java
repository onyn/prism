package com.helion3.prism.libs.elixr;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Bed;

public class BlockUtils {

    /**
     * Determines if the existing block at a location is
     * something that's commonly acceptable to replace.
     */
    public static boolean isAcceptableForBlockPlace(Material m) {
        switch (m) {
            case AIR:
            case FIRE:
            case GRAVEL:
            case LAVA:
            case SAND:
            case STATIONARY_WATER:
            case STATIONARY_LAVA:
            case WATER:
            case SNOW:
            case SNOW_BLOCK:
            case LONG_GRASS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Recursively grabs a list of all blocks directly above Block
     * that are anticipated to fall.
     */
    public static ArrayList<Block> findFallingBlocksAboveBlock(Block block) {
        ArrayList<Block> falling_blocks = new ArrayList<>();
        Block above = block.getRelative(BlockFace.UP);
        if (isFallingBlock(above)) {
            falling_blocks.add(above);
            ArrayList<Block> fallingBlocksAbove = findFallingBlocksAboveBlock(above);
            if (fallingBlocksAbove.size() > 0) {
                falling_blocks.addAll(fallingBlocksAbove);
            }
        }
        return falling_blocks;
    }

    /**
     * Determine whether or not a block is capable of falling.
     * <p>
     * Seems like there's got to be another way to do this...
     */
    public static boolean isFallingBlock(Block block) {
        Material m = block.getType();
        return m.equals(Material.SAND) || m.equals(Material.GRAVEL) || m.equals(Material.ANVIL);
    }

    /**
     * Searches for detachable blocks on the four acceptable sides of a block.
     */
    public static ArrayList<Block> findSideFaceAttachedBlocks(Block block) {
        ArrayList<Block> detaching_blocks = new ArrayList<>();
        Block blockToCheck = block.getRelative(BlockFace.EAST);
        if (isSideFaceDetachableMaterial(blockToCheck.getType())) {
            detaching_blocks.add(blockToCheck);
        }

        blockToCheck = block.getRelative(BlockFace.WEST);
        if (isSideFaceDetachableMaterial(blockToCheck.getType())) {
            detaching_blocks.add(blockToCheck);
        }

        blockToCheck = block.getRelative(BlockFace.NORTH);
        if (isSideFaceDetachableMaterial(blockToCheck.getType())) {
            detaching_blocks.add(blockToCheck);
        }

        blockToCheck = block.getRelative(BlockFace.SOUTH);
        if (isSideFaceDetachableMaterial(blockToCheck.getType())) {
            detaching_blocks.add(blockToCheck);
        }

        return detaching_blocks;
    }

    public static Block findFirstSurroundingBlockOfType(Block source, Material surrounding) {
        Block blockToCheck = source.getRelative(BlockFace.EAST);
        if (blockToCheck.getType().equals(surrounding)) {
            return blockToCheck;
        }
        blockToCheck = source.getRelative(BlockFace.WEST);
        if (blockToCheck.getType().equals(surrounding)) {
            return blockToCheck;
        }
        blockToCheck = source.getRelative(BlockFace.NORTH);
        if (blockToCheck.getType().equals(surrounding)) {
            return blockToCheck;
        }
        blockToCheck = source.getRelative(BlockFace.SOUTH);
        if (blockToCheck.getType().equals(surrounding)) {
            return blockToCheck;
        }
        return null;
    }

    /**
     * Determine whether or not a block is going to detach
     * from the side of a block.
     * <p>
     * Seems like there's got to be another way to do this...
     */
    public static boolean isSideFaceDetachableMaterial(Material m) {
        if (m.equals(Material.WALL_SIGN)
            || m.equals(Material.TORCH)
            || m.equals(Material.LEVER)
            || m.equals(Material.WOOD_BUTTON)
            || m.equals(Material.STONE_BUTTON)
            || m.equals(Material.LADDER)
            || m.equals(Material.VINE)
            || m.equals(Material.COCOA)
            || m.equals(Material.PORTAL)
            || m.equals(Material.PISTON_EXTENSION)
            || m.equals(Material.PISTON_MOVING_PIECE)
            || m.equals(Material.PISTON_BASE) // Fake entry, the base always breaks if the extension is lost
            || m.equals(Material.PISTON_STICKY_BASE)
            || m.equals(Material.REDSTONE_TORCH_OFF)
            || m.equals(Material.REDSTONE_TORCH_ON)
            || m.equals(Material.TRAP_DOOR)) {
            return true;
        }
        return false;
    }

    /**
     * Searches for detachable blocks on the four acceptable sides of a block.
     */
    public static ArrayList<Block> findTopFaceAttachedBlocks(Block block) {
        ArrayList<Block> detaching_blocks = new ArrayList<>();
        Block blockToCheck = block.getRelative(BlockFace.UP);
        if (isTopFaceDetachableMaterial(blockToCheck.getType())) {
            detaching_blocks.add(blockToCheck);
            if (blockToCheck.getType().equals(Material.CACTUS) || blockToCheck.getType().equals(Material.SUGAR_CANE_BLOCK)) {
                ArrayList<Block> additionalBlocks = findTopFaceAttachedBlocks(blockToCheck);
                if (!additionalBlocks.isEmpty()) {
                    detaching_blocks.addAll(additionalBlocks);
                }
            }
        }
        return detaching_blocks;
    }

    /**
     * Determine whether or not a block is going to detach
     * from the side of a block.
     * <p>
     * Seems like there's got to be another way to do this...
     */
    public static boolean isTopFaceDetachableMaterial(Material m) {
        switch (m) {
            case SNOW:
            case LONG_GRASS:
            case ACTIVATOR_RAIL:
            case BROWN_MUSHROOM:
            case CACTUS:
            case CARROT:
            case DEAD_BUSH:
            case DETECTOR_RAIL:
            case DOUBLE_PLANT:
            case POTATO:
            case CROPS:
            case DIODE:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case FLOWER_POT:
            case GOLD_PLATE:
            case IRON_DOOR:
            case IRON_DOOR_BLOCK:
            case IRON_PLATE:
            case LEVER:
            case MELON_STEM:
            case NETHER_WARTS:
            case PORTAL:
            case POWERED_RAIL:
            case PUMPKIN_STEM:
            case RAILS:
            case RED_MUSHROOM:
            case RED_ROSE:
            case REDSTONE:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case REDSTONE_WIRE:
            case SAPLING:
            case SIGN:
            case SIGN_POST:
            case SKULL:
            case SUGAR_CANE_BLOCK:
            case STONE_PLATE:
            case TORCH:
            case TRIPWIRE:
            case WATER_LILY:
            case WHEAT:
            case WOOD_DOOR:
            case WOOD_PLATE:
            case WOODEN_DOOR:
            case YELLOW_FLOWER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether or not a block location is filled
     * by a material that means an attachable material
     * is now detached.
     */
    public static boolean materialMeansBlockDetachment(Material m) {
        switch (m) {
            case AIR:
            case FIRE:
            case LAVA:
            case STATIONARY_WATER:
            case STATIONARY_LAVA:
            case WATER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Searches for detachable entities in a
     */
    public static ArrayList<Entity> findHangingEntities(Block block) {
        ArrayList<Entity> entities = new ArrayList<>();
        Entity[] foundEntities = block.getChunk().getEntities();
        if (foundEntities.length > 0) {
            for (Entity e : foundEntities) {
                // Some modded servers seems to list entities in the chunk
                // that exists in other worlds. No idea why but we can at
                // least check for it.
                // https://snowy-evening.com/botsko/prism/318/
                if (!block.getWorld().equals(e.getWorld())) {
                    continue;
                }
                // Let's limit this to only entities within 1 block of the current.
                if (block.getLocation().distance(e.getLocation()) < 2 && isHangingEntity(e)) {
                    entities.add(e);
                }
            }
        }

        return entities;
    }

    /**
     * Is an entity a hanging type, attachable to a block.
     */
    public static boolean isHangingEntity(Entity entity) {
        EntityType e = entity.getType();
        return e.equals(EntityType.ITEM_FRAME) || e.equals(EntityType.PAINTING);
    }

    public static Block getSiblingForDoubleLengthBlock(Block block) {
        // Handle special double-length blocks
        if (block.getType().equals(Material.WOODEN_DOOR) || block.getType().equals(Material.IRON_DOOR_BLOCK)) {
            // If you've broken the top half of a door, we need to record the action for the bottom.
            // This is because a top half break doesn't record the orientation of the door while the bottom does,
            // and we have code in the rollback/restore to add the top half back in.
            if (block.getData() == 8 || block.getData() == 9) {
                return block.getRelative(BlockFace.DOWN);
            }
        }
        // If it's a bed, we always record the lower half and rely on appliers
        if (block.getType().equals(Material.BED_BLOCK)) {
            Bed b = (Bed) block.getState().getData();
            if (b.isHeadOfBed()) {
                return block.getRelative(b.getFacing().getOppositeFace());
            }
        }
        if (block.getType().equals(Material.CHEST)) {
            return findFirstSurroundingBlockOfType(block, Material.CHEST);
        }
        return null;
    }

    /**
     * Lower door halves get byte values based on which direction the front
     * of the door is facing.
     * <p>
     * 0 = West
     * 1 = North
     * 2 = East
     * 3 = South
     * <p>
     * The upper halves of both door types always have a value of 8.
     */
    public static void properlySetDoor(Block originalBlock, int typeid, byte subid) {
        // Wood door upper or iron door upper
        if (subid == 8 || subid == 9) { // 8 for single doors or left side of double, 9 for right side of double
            Block aboveOrBelow = originalBlock.getRelative(BlockFace.DOWN);
            aboveOrBelow.setTypeId(typeid);
            aboveOrBelow.setData((byte) 0); // we have no way to know which direction the lower half was facing

        } else { // Wood door lower or iron door lower
            Block aboveOrBelow = originalBlock.getRelative(BlockFace.UP);
            aboveOrBelow.setTypeId(typeid);
            // Determine the directing the bottom half is facing, then check
            // it's left side for an existing door, because the subid changes
            // if we're on the right.
            Block left = null;
            switch (subid) {
                case 0:
                    // Back faces east
                    left = originalBlock.getRelative(BlockFace.NORTH);
                    break;
                case 1:
                    // Back faces south
                    left = originalBlock.getRelative(BlockFace.EAST);
                    break;
                case 2:
                    // Back faces west
                    left = originalBlock.getRelative(BlockFace.SOUTH);
                    break;
                case 3:
                    // Back faces north
                    left = originalBlock.getRelative(BlockFace.WEST);
                    break;
            }
            if (aboveOrBelow != null) {
                if (left != null && isDoor(left.getType())) {
                    aboveOrBelow.setData((byte) 9);
                } else {
                    aboveOrBelow.setData((byte) 8);
                }
            }
        }
    }

    public static boolean isDoor(Material m) {
        switch (m) {
            case IRON_DOOR:
            case IRON_DOOR_BLOCK:
            case WOOD_DOOR:
            case WOODEN_DOOR:
                return true;
            default:
                return false;
        }
    }

    /**
     * Given the lower block of a bed, we translate that to the top
     * half, figuring out which direction and data value it gets.
     */
    public static void properlySetBed(Block originalBlock, int typeid, byte subid) {
        Block top = null;
        int new_subid = 0;
        switch (subid) {
            case 3:
                top = originalBlock.getRelative(BlockFace.EAST);
                new_subid = 11;
                break;
            case 2:
                top = originalBlock.getRelative(BlockFace.NORTH);
                new_subid = 10;
                break;
            case 1:
                top = originalBlock.getRelative(BlockFace.WEST);
                new_subid = 9;
                break;
            case 0:
                top = originalBlock.getRelative(BlockFace.SOUTH);
                new_subid = 8;
                break;
        }

        if (top != null) {
            top.setTypeId(typeid);
            top.setData((byte) new_subid);
        } else {
            System.out.println("Error setting bed: block top location was illegal. Data value: " + subid + " New data value: " + new_subid);
        }
    }

    public static void properlySetDoublePlant(Block originalBlock, int typeid, byte subid) {
        if (originalBlock.getType().equals(Material.DOUBLE_PLANT)) {
            Block above = originalBlock.getRelative(BlockFace.UP);
            if (isAcceptableForBlockPlace(above.getType())) {
                if (typeid == 175 && subid < 8) {
                    subid = 8;
                }

                above.setTypeId(typeid);
                above.setData(subid);
            }
        }
    }

    public static boolean canFlowBreakMaterial(Material m) {
        switch (m) {
            case LONG_GRASS:
            case ACTIVATOR_RAIL:
            case BROWN_MUSHROOM:
            case CACTUS:
            case CARROT:
            case DEAD_BUSH:
            case DETECTOR_RAIL:
            case DOUBLE_PLANT:
            case POTATO:
            case CROPS:
            case DIODE:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case FLOWER_POT:
            case IRON_DOOR:
            case IRON_DOOR_BLOCK:
            case LEVER:
            case MELON_STEM:
            case NETHER_WARTS:
            case POWERED_RAIL:
            case PUMPKIN_STEM:
            case RAILS:
            case RED_MUSHROOM:
            case RED_ROSE:
            case REDSTONE:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case REDSTONE_WIRE:
            case SAPLING:
            case SIGN:
            case SIGN_POST:
            case SKULL:
            case SUGAR_CANE_BLOCK:
            case STONE_PLATE:
            case TORCH:
            case TRIPWIRE:
            case WATER_LILY:
            case WHEAT:
            case WOOD_DOOR:
            case WOOD_PLATE:
            case WOODEN_DOOR:
            case YELLOW_FLOWER:
            case COCOA: // different from pop off list
            case LADDER: // different from pop off list
            case TRIPWIRE_HOOK: // different from pop off list
            case VINE: // different from pop off list
                return true;
            default:
                return false;
        }
    }

    public static boolean materialRequiresSoil(Material m) {
        switch (m) {
            case CARROT:
            case POTATO:
            case CROPS:
            case MELON_STEM:
            case PUMPKIN_STEM:
            case WHEAT:
                return true;
            default:
                return false;
        }
    }

    public static ArrayList<Block> findConnectedBlocksOfType(Material type, Block currBlock, ArrayList<Location> foundLocations) {
        ArrayList<Block> foundBlocks = new ArrayList<>();
        if (foundLocations == null) {
            foundLocations = new ArrayList<>();
        }

        foundLocations.add(currBlock.getLocation());

        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = -1; y <= 1; ++y) {
                    Block newblock = currBlock.getRelative(x, y, z);
                    if (newblock.getType() == type && !foundLocations.contains(newblock.getLocation())) {
                        foundBlocks.add(newblock);
                        ArrayList<Block> additionalBlocks = findConnectedBlocksOfType(type, newblock, foundLocations);
                        if (additionalBlocks.size() > 0) {
                            foundBlocks.addAll(additionalBlocks);
                        }
                    }
                }
            }
        }

        return foundBlocks;
    }

    public static Block getFirstBlockOfMaterialBelow(Material m, Location loc) {
        for (int y = (int) loc.getY(); y > 0; --y) {
            loc.setY(y);
            if (loc.getBlock().getType().equals(m)) {
                return loc.getBlock();
            }
        }

        return null;
    }

    public static boolean isGrowableStructure(Material m) {
        switch (m) {
            case LEAVES:
            case LOG:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                return true;
            default:
                return false;
        }
    }

    /**
     * There are several items that are officially different
     * ItemStacks, but for the purposes of what we're doing
     * are really considered one core item. This attempts
     * to be a little lenient on matching the ids.
     * <p>
     * Example: Redstone lamp (off) is 123, (on) is 124 but
     * either id means it's a redstone lamp.
     */
    public static boolean areBlockIdsSameCoreItem(int id1, int id2) {

        // Get the obvious one out of the way.
        if (id1 == id2) return true;

        // Grass/Dirt
        if ((id1 == 2 || id1 == 3) && (id2 == 2 || id2 == 3)) {
            return true;
        }

        // Mycel/Dirt
        if ((id1 == 110 || id1 == 3) && (id2 == 110 || id2 == 3)) {
            return true;
        }

        // Water
        if ((id1 == 8 || id1 == 9) && (id2 == 8 || id2 == 9)) {
            return true;
        }

        // Lava
        if ((id1 == 10 || id1 == 11) && (id2 == 10 || id2 == 11)) {
            return true;
        }

        // Redstone torch
        if ((id1 == 75 || id1 == 76) && (id2 == 75 || id2 == 76)) {
            return true;
        }

        // Repeater
        if ((id1 == 93 || id1 == 94) && (id2 == 93 || id2 == 94)) {
            return true;
        }

        // Redstone lamp
        if ((id1 == 123 || id1 == 124) && (id2 == 123 || id2 == 124)) {
            return true;
        }

        // Furnace
        if ((id1 == 61 || id1 == 62) && (id2 == 61 || id2 == 62)) {
            return true;
        }

        // Redstone comparator
        if ((id1 == 149 || id1 == 150) && (id2 == 149 || id2 == 150)) {
            return true;
        }

        return false;
    }
}

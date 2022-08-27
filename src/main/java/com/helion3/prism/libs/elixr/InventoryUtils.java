package com.helion3.prism.libs.elixr;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author botskonet
 */
public class InventoryUtils {

    public static void updateInventory(Player p) {
        p.updateInventory();
    }

    public static boolean playerInvIsEmpty(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean playerArmorIsEmpty(Player p) {
        for (ItemStack item : p.getInventory().getArmorContents()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the slot id of a specific item type, or -1 if none
     */
    public static int inventoryHasItem(Inventory inv, int item_id, int sub_id) {
        int currentSlot = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getTypeId() == item_id && item.getDurability() == sub_id) {
                return currentSlot;
            }
            ++currentSlot;
        }
        return -1;
    }

    /**
     * Moves a specific item to the player's hand, returns false if the item doesn't exist in the inventory
     */
    public static boolean moveItemToHand(PlayerInventory inv, int item_id, byte sub_id) {
        int slot = inventoryHasItem(inv, item_id, sub_id);
        if (slot > -1) {
            ItemStack item = inv.getItem(slot);
            inv.clear(slot);
            // If the player has an item in-hand, switch to a vacant spot
            if (!playerHasEmptyHand(inv)) {
                inv.setItem(slot, inv.getItemInHand());
            }

            inv.setItemInHand(item);
            return true;
        }
        return false;
    }

    /**
     * Whether or not the player has an empty hand
     */
    public static boolean playerHasEmptyHand(PlayerInventory inv) {
        return inv.getItemInHand().getTypeId() == 0;
    }

    /**
     * Adds an item to the inventory, returns a hashmap of leftovers
     */
    public static HashMap<Integer, ItemStack> addItemToInventory(Inventory inv, ItemStack item) {
        return inv.addItem(item);
    }

    public static boolean handItemToPlayer(PlayerInventory inv, ItemStack item) {
        // Ensure there's at least one empty inv spot
        if (inv.firstEmpty() != -1) {
            ItemStack originalItem = inv.getItemInHand().clone();
            // If the player has an item in-hand, switch to a vacant spot
            if (!playerHasEmptyHand(inv)) {
                // We need to manually add the item stack to a different
                // slot because by default, bukkit combines items with addItem
                // and that was causing items to be lost unless they were the max
                // stack size
                for (int i = 0; i <= inv.getSize(); ++i) {
                    if (i == inv.getHeldItemSlot()) {
                        continue;
                    }
                    ItemStack current = inv.getItem(i);
                    if (current == null) {
                        inv.setItem(i, originalItem);
                        break;
                    }
                }
            }
            inv.setItemInHand(item);
            return true;
        }
        return false;
    }

    /**
     * Subtract a specific quantity from an inventory slots item stack.
     */
    public static void subtractAmountFromPlayerInvSlot(PlayerInventory inv, int slot, int quant) {
        ItemStack itemAtSlot = inv.getItem(slot);
        if (itemAtSlot != null && quant <= 64) {
            itemAtSlot.setAmount(itemAtSlot.getAmount() - quant);
            if (itemAtSlot.getAmount() == 0) {
                inv.clear(slot);
            }
        }

    }

    /**
     * Drop items at player's location.
     */
    public static void dropItemsByPlayer(HashMap<Integer, ItemStack> leftovers, Player player) {
        if (!leftovers.isEmpty()) {
            for (Map.Entry<Integer, ItemStack> entry : leftovers.entrySet()) {
                player.getWorld().dropItemNaturally(player.getLocation(), entry.getValue());
            }
        }
    }

    /**
     * Is an inventory fully empty
     */
    public static boolean isEmpty(Inventory in) {
        boolean ret = false;
        if (in == null) {
            return true;
        } else {
            for (ItemStack item : in.getContents()) {
                ret |= (item != null);
            }
            return !ret;
        }
    }

    public static void movePlayerInventoryToContainer(PlayerInventory inv, Block target, HashMap<Integer, Short> filters) throws Exception {
        InventoryHolder container = (InventoryHolder) target.getState();
        if (!moveInventoryToInventory(inv, container.getInventory(), false, filters)) {
            throw new Exception("Target container is full.");
        }
    }

    public static void moveContainerInventoryToPlayer(PlayerInventory inv, Block target, HashMap<Integer, Short> filters) throws Exception {
        InventoryHolder container = (InventoryHolder) target.getState();
        moveInventoryToInventory(container.getInventory(), inv, false, filters);
    }

    public static boolean moveInventoryToInventory(Inventory from, Inventory to, boolean fullFlag, HashMap<Integer, Short> filters) {
        if (to.firstEmpty() != -1 && !fullFlag) {
            for (ItemStack item : from.getContents()) {
                if (to.firstEmpty() == -1) {
                    return false;
                }

                if (item != null && to.firstEmpty() != -1) {
                    boolean shouldTransfer = false;
                    if (filters.size() > 0) {
                        for (Map.Entry<Integer, Short> entry : filters.entrySet()) {
                            if (entry.getKey() == item.getTypeId() && entry.getValue() == item.getDurability()) {
                                shouldTransfer = true;
                            }
                        }
                    } else {
                        shouldTransfer = true;
                    }

                    if (shouldTransfer) {
                        HashMap<Integer, ItemStack> leftovers = to.addItem(item);
                        if (leftovers.size() == 0) {
                            from.removeItem(item);
                        } else {
                            from.removeItem(item);
                            from.addItem(leftovers.get(0));
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static ItemStack[] sortItemStack(ItemStack[] stack, Player player) {
        return sortItemStack(stack, 0, stack.length, player);
    }

    public static ItemStack[] sortItemStack(ItemStack[] stack, int start, int end, Player player) {
        stack = stackItems(stack, start, end);
        recQuickSort(stack, start, end - 1);
        return stack;
    }

    private static ItemStack[] stackItems(ItemStack[] items, int start, int end) {
        for (int i = start; i < end; ++i) {
            ItemStack item = items[i];

            // Avoid infinite stacks and stacks with durability
            if (item == null || item.getAmount() <= 0 || !com.helion3.prism.libs.elixr.ItemUtils.canSafelyStack(item)) {
                continue;
            }

            int max_stack = item.getMaxStackSize();
            if (item.getAmount() < max_stack) {
                int needed = max_stack - item.getAmount(); // Number of needed items until max_stack

                // Find another stack of the same type
                for (int j = i + 1; j < end; ++j) {
                    ItemStack item2 = items[j];

                    // Avoid infinite stacks and stacks with durability
                    if (item2 == null || item2.getAmount() <= 0 || !ItemUtils.canSafelyStack(item)) {
                        continue;
                    }

                    // Same type?
                    // Blocks store their color in the damage value
                    if (item2.getTypeId() == item.getTypeId() && (!ItemUtils.dataValueUsedForSubitems(item.getTypeId()) || item.getDurability() == item2.getDurability())) {
                        // This stack won't fit in the parent stack
                        if (item2.getAmount() > needed) {
                            item.setAmount(max_stack);
                            item2.setAmount(item2.getAmount() - needed);
                            break;
                        } else {
                            item.setAmount(item.getAmount() + item2.getAmount());
                            needed = max_stack - item.getAmount();
                            items[j].setTypeId(0);
                        }
                    }
                }
            }
        }
        return items;
    }

    private static void swap(ItemStack[] list, int first, int second) {
        ItemStack temp = list[first];
        list[first] = list[second];
        list[second] = temp;
    }

    private static int partition(ItemStack[] list, int first, int last) {
        swap(list, first, (first + last) / 2);
        ItemStack pivot = list[first];
        int smallIndex = first;

        for (int index = first + 1; index <= last; ++index) {
            ComparableIS compElem = new ComparableIS(list[index]);
            if (compElem.compareTo(pivot) < 0) {
                ++smallIndex;
                swap(list, smallIndex, index);
            }
        }

        swap(list, first, smallIndex);
        return smallIndex;
    }

    private static void recQuickSort(ItemStack[] list, int first, int last) {
        if (first < last) {
            int pivotLocation = partition(list, first, last);
            recQuickSort(list, first, pivotLocation - 1);
            recQuickSort(list, pivotLocation + 1, last);
        }
    }

    /**
     * @author botskonet
     */
    private static class ComparableIS {
        private ItemStack item;

        public ComparableIS(ItemStack item) {
            this.item = item;
        }

        public int compareTo(ItemStack check) {
            // Type ID first
            if (item == null && check != null) {
                return -1;
            } else if (item != null && check == null) {
                return 1;
            } else if (item == null && check == null) {
                return 0;
            } else if (item.getTypeId() > check.getTypeId()) {
                return 1;
            } else if (item.getTypeId() < check.getTypeId()) {
                return -1;
            } else {
                if (item.getTypeId() == check.getTypeId()) {
                    if (ItemUtils.dataValueUsedForSubitems(item.getTypeId())) {
                        if (item.getDurability() < check.getDurability()) {
                            return 1;
                        } else if (item.getDurability() > check.getDurability()) {
                            return -1;
                        }
                    }

                    // Stack size
                    if (item.getAmount() < check.getAmount()) {
                        return -1;
                    } else if (item.getAmount() > check.getAmount()) {
                        return 1;
                    }
                }

                return 0;
            }
        }
    }
}

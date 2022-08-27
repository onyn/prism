package com.helion3.prism.libs.elixr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MaterialAliases {

    /**
     * Contains loaded item ids => aliases
     */
    protected HashMap<String, String> itemAliases = new HashMap<>();

    /**
     * Load the yml file and save config to hashmap
     */
    public MaterialAliases() {
        FileConfiguration items = null;
        InputStream defConfigStream = this.getClass().getResourceAsStream("/items.yml");
        if (defConfigStream != null) {
            System.out.println("Elixr: Loaded items directory");
            items = YamlConfiguration.loadConfiguration(defConfigStream);
        }

        if (items != null) {
            // Load all item ids/aliases
            Map<String, Object> itemaliases = items.getConfigurationSection("items").getValues(false);

            // Cache the values for easier lookup
            if (itemaliases != null) {
                for (String key : itemaliases.keySet()) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> aliases = (ArrayList<String>) itemaliases.get(key);
                    if (aliases.size() > 0) {
                        for (String alias : aliases) {
                            itemAliases.put(key, alias);
                        }
                    }
                }
            }
        } else {
            System.out.println("ERROR: The Elixr library was unable to load an internal item alias list.");
        }
    }

    /**
     * Returns the loaded list of item aliases/ids;
     */
    public HashMap<String, String> getItemAliases() {
        return this.itemAliases;
    }

    /**
     * Returns the proper name given an item type id, data/durability
     */
    public String getAlias(int typeid, int subid) {
        String item_name = null;
        if (!this.itemAliases.isEmpty()) {
            String key = typeid + ":" + subid;
            item_name = this.itemAliases.get(key);
        }

        if (item_name == null) {
            ItemStack i = new ItemStack(typeid, subid);
            item_name = i.getType().name().toLowerCase().replace("_", " ");
        }

        return item_name;
    }

    /**
     * Returns the proper name given an item stack
     */
    public String getAlias(ItemStack i) {
        return this.getAlias(i.getTypeId(), (byte) i.getDurability());
    }

    public ArrayList<int[]> getIdsByAlias(String alias) {
        ArrayList<int[]> itemIds = new ArrayList();
        if (!this.itemAliases.isEmpty()) {
            for (Map.Entry<String, String> entry : itemAliases.entrySet()) {
                int[] ids = new int[2];
                if (entry.getValue().equals(alias)) {
                    String[] _tmp = entry.getKey().split(":");
                    ids[0] = Integer.parseInt(_tmp[0]);
                    ids[1] = Integer.parseInt(_tmp[1]);
                    itemIds.add(ids);
                }
            }
        }
        return itemIds;
    }
}

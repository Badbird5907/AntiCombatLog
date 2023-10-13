package net.badbird5907.anticombatlog.hooks.impl;

import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.util.EnchantUtils;

import java.util.Map;

public class ExcellentEnchantUtil {
    public static boolean shouldKeep(ItemStack item) {
        Map<ExcellentEnchant, Integer> excellents = EnchantUtils.getExcellents(item);
        if (excellents.isEmpty()) return false;
        return excellents.keySet().stream().anyMatch(enchant -> {
            // return true to add to list
            if (enchant.getId().equalsIgnoreCase("soulbound")) {
                if (EnchantUtils.isOutOfCharges(item, enchant)) {
                    return false;
                }
                return EnchantUtils.getLevel(item, enchant) >= 0;
                // Logger.debug("  - Consuming charges...");
                // EnchantUtils.consumeCharges(item, enchant, EnchantUtils.getLevel(item, enchant)); // disabled because excellent enchants does this for us when the player is kiled
            }
            return false;
        });
    }
}

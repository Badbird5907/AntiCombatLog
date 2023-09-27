package net.badbird5907.anticombatlog.hooks.impl;

import net.badbird5907.blib.util.Logger;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.util.EnchantUtils;

import java.util.Map;

public class ExcellentEnchantUtil {
    public static boolean shouldKeep(ItemStack item) {
        Map<ExcellentEnchant, Integer> excellents = EnchantUtils.getExcellents(item);
        if (excellents.isEmpty()) return false;
        Logger.debug("Found " + excellents.size() + " excellent enchants on " + item.getType().name() + "!");
        return excellents.keySet().stream().anyMatch(enchant -> {
            Logger.debug(" - Checking if " + enchant.getId() + " is a soulbound enchant...");
            // return true to add to list
            if (enchant.getId().equalsIgnoreCase("soulbound")) {
                Logger.debug("  - Found soulbound enchant!");
                if (EnchantUtils.isOutOfCharges(item, enchant)) {
                    Logger.debug("  - Out of charges!");
                    return false;
                }
                if (EnchantUtils.getLevel(item, enchant) < 0) {
                    Logger.debug("  - Level is less than 0!");
                    return false;
                }
                // Logger.debug("  - Consuming charges...");
                // EnchantUtils.consumeCharges(item, enchant, EnchantUtils.getLevel(item, enchant)); // disabled because excellent enchants does this for us when the player is kiled
                return true;
            }
            return false;
        });
    }
}

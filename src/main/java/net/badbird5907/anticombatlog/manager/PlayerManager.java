package net.badbird5907.anticombatlog.manager;

import net.badbird5907.anticombatlog.AntiCombatLog;
import org.bukkit.entity.Player;

public class PlayerManager {
    public static void join(Player player) {
        if (CombatManager.getInstance().getToKillOnLogin().containsKey(player.getUniqueId())) {
            CombatManager.getInstance().getToKillOnLogin().remove(player.getUniqueId());
            AntiCombatLog.getKilled().add(player.getUniqueId());
            /*
            List<ItemStack> toKeep = new ArrayList<>();
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR)
                    continue;
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                    if (AEAPI.hasWhitescroll(item)) {
                        toKeep.add(item);
                        AEAPI.removeWhitescroll(item);
                        continue;
                    }
                }
            }
            player.getInventory().clear();
             */
            player.setHealth(0.0d);
            AntiCombatLog.getInstance().clearCombatTag(player);
            AntiCombatLog.getInstance().getStorageProvider().saveToKillOnLogin(CombatManager.getInstance().getToKillOnLogin());
            return;
        }
        if (NPCManager.getInstance().isSpawned(player.getUniqueId())) {
            double health = NPCManager.getInstance().getHealth(player.getUniqueId());
            player.setHealth(health);
            NPCManager.getInstance().despawn(player.getUniqueId());
        }
        /*
        if (UUIDUtil.contains(CombatManager.freezeTimer, player.getUniqueId())) {
            UUIDUtil.remove(CombatManager.freezeTimer, player.getUniqueId());
            int a = CombatManager.getInstance().getInCombatTag().getOrDefault(player.getUniqueId(), 0);
            CombatManager.getInstance().getInCombatTag().put(player.getUniqueId(), a + AntiCombatLog.getInstance().getConfig().getInt("login-after-combat-log-add-timer-seconds", 5));
        }

        */
        if (CombatManager.getInstance().getFreezeTimer().remove(player.getUniqueId())) {
            int a = CombatManager.getInstance().getInCombatTag().getOrDefault(player.getUniqueId(), 0);
            CombatManager.getInstance().getInCombatTag().put(player.getUniqueId(), a + AntiCombatLog.getInstance().getConfig().getInt("login-after-combat-log-add-timer-seconds", 5));
        }
    }
}

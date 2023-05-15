package net.badbird5907.anticombatlog.manager;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarManager {
    public static void sendBar(Player player, int seconds) {
        String str = StringUtils.format(ConfigValues.getActionBarMessage(), seconds + "");
        if (str == null) return;
        player.sendActionBar(str);
    }

    public static void update() {
        AntiCombatLog.getInCombatTag().forEach((uuid, integer) -> {
            if (Bukkit.getPlayer(uuid) != null) {
                sendBar(Bukkit.getPlayer(uuid), integer);
            }
        });
    }
}

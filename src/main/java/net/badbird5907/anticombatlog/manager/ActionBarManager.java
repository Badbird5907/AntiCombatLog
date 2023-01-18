package net.badbird5907.anticombatlog.manager;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarManager {
    @Getter
    public static ActionBarManager instance = new ActionBarManager();

    private ActionBarManager() {
    }

    public void sendBar(Player player, int seconds) {
        player.sendActionBar(StringUtils.format(AntiCombatLog.getInstance()
                .getConfig().getString("messages.action-bar-message", ""), seconds + ""));
    }

    public void update() {
        CombatManager.getInstance().getInCombatTag().forEach((uuid, integer) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                sendBar(player, integer);
            }
        });
    }
}

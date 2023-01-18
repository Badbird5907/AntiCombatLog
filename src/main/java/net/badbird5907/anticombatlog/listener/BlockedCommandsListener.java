package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BlockedCommandsListener implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (AntiCombatLog.getInstance().getConfig().getBoolean("blocked-commands.enabled", true)) {
            String command = event.getMessage().split(" ")[0].replace("/", "");
            if (AntiCombatLog.getInstance().getConfig().getStringList("blocked-commands.blocked").contains(command.toLowerCase())
                    && AntiCombatLog.isCombatTagged(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(StringUtils.format(AntiCombatLog.getInstance().getConfig().getString("messages.blocked-command","")));
            }
        }
    }
}

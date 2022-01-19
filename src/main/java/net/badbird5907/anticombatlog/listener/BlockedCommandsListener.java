package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.utils.ConfigValues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BlockedCommandsListener implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (ConfigValues.isEnableBlockedCommands()) {
            String command = event.getMessage().split(" ")[0].replace("/", "");
            if (ConfigValues.getBlockedCommands().contains(command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ConfigValues.getBlockedCommandMessage());
            }
        }
    }
}

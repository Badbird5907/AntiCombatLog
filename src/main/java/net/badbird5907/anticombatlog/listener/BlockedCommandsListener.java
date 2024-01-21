package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BlockedCommandsListener implements Listener {
    private Map<String, Pattern> regexCache = new HashMap<>();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!AntiCombatLog.isCombatTagged(event.getPlayer())) return;
        if (ConfigValues.isEnableBlockedCommands()) {
            String command = event.getMessage().split(" ")[0].replace("/", "");
            if (ConfigValues.isBlockedCommandsRegex()) {
                for (String blockedCommand : ConfigValues.getBlockedCommands()) {
                    Pattern pattern = regexCache.getOrDefault(blockedCommand, Pattern.compile(blockedCommand));
                    if (pattern.matcher(command).matches()) {
                        event.setCancelled(true);
                        AntiCombatLog.getInstance().getLogger().info("Blocked command " + command + " from " + event.getPlayer().getName() + " (Matched regex: " + blockedCommand + ")");
                        if (ConfigValues.getBlockedCommandMessage() != null)
                            event.getPlayer().sendMessage(ConfigValues.getBlockedCommandMessage());
                        return;
                    }
                }
            }
            if (ConfigValues.getBlockedCommands().contains(command)) {
                event.setCancelled(true);
                AntiCombatLog.getInstance().getLogger().info("Blocked command " + command + " from " + event.getPlayer().getName() + " (Matched command: " + command + ")");
                if (ConfigValues.getBlockedCommandMessage() != null)
                    event.getPlayer().sendMessage(ConfigValues.getBlockedCommandMessage());
            }
        }
    }
}

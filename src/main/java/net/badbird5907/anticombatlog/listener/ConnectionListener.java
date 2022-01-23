package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.blib.util.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (AntiCombatLog.isCombatTagged(event.getPlayer())) {
            AntiCombatLog.disconnect(event.getPlayer());
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("anticombatlog.admin")){
            if (AntiCombatLog.updateAvailable){
                event.getPlayer().sendMessage(CC.GREEN + "[AntiCombatLog] There is a update available! Your version is: " + CC.B + AntiCombatLog.getInstance().getDescription().getVersion() + CC.R + CC.GREEN + " and the new version is: " + CC.B + AntiCombatLog.getNewVersion() + CC.R + CC.GREEN + ".\nDownload @ https://badbird5907.xyz/anticombatlog?ref=server");
            }
        }
        AntiCombatLog.join(event.getPlayer());
    }
}

package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event){
        if (AntiCombatLog.isCombatTagged(event.getPlayer())){
            AntiCombatLog.disconnect(event.getPlayer());
        }
    }
    @EventHandler
    public void onConnect(PlayerJoinEvent event){
        AntiCombatLog.join(event.getPlayer());
    }
}

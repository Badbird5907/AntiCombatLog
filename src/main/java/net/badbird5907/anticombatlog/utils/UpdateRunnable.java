package net.badbird5907.anticombatlog.utils;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.UnCombatTagEvent;
import net.badbird5907.anticombatlog.manager.ActionBarManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        if (NPCManager.isEnabled()) {
            NPCManager.update();
        }
        Map<UUID, Integer> stillInCombat = new ConcurrentHashMap<>();
        AntiCombatLog.getInCombatTag().forEach(((uuid, integer) -> {
            if (Bukkit.getPlayer(uuid) != null) {
                /*
                if (AntiCombatLog.getFreezeTimer().contains(uuid)) {
                    AntiCombatLog.getFreezeTimer().remove(uuid);
                    stillInCombat.put(uuid, integer);
                    return;
                }
                 */
                int a = integer - 1;
                if (a > 0)
                    stillInCombat.put(uuid, a);
                else {
                    UnCombatTagEvent event = new UnCombatTagEvent(Bukkit.getPlayer(uuid));
                    Bukkit.getPluginManager().callEvent(event);
                    if (ConfigValues.scoreboardEnabled()) {
                        ScoreboardManager.getScoreboards().remove(uuid);
                        Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                    if (ConfigValues.getCombatExpiredMessage() != null)
                        Bukkit.getPlayer(uuid).sendMessage(ConfigValues.getCombatExpiredMessage());
                }
            }
        }));
        AntiCombatLog.setInCombatTag(stillInCombat);
        if (ConfigValues.scoreboardEnabled())
            ScoreboardManager.update();
        if (ConfigValues.actionBarEnabled())
            ActionBarManager.update();
    }
}

package net.badbird5907.anticombatlog.runnbale;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.manager.ActionBarManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.manager.ScoreboardManager;
import net.badbird5907.anticombatlog.object.NotifyType;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        NPCManager.update();
        Map<UUID,Integer> stillInCombat = new ConcurrentHashMap<>();
        AntiCombatLog.getInCombatTag().forEach(((uuid, integer) -> {
            if (Bukkit.getPlayer(uuid) != null){
                int a = integer - 1;
                if (a > 0)
                    stillInCombat.put(uuid,a);
                else {
                    ScoreboardManager.getScoreboards().remove(uuid);
                    Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    Bukkit.getPlayer(uuid).sendMessage(ConfigValues.getCombatExpiredMessage());
                }
            }
        }));
        AntiCombatLog.setInCombatTag(stillInCombat);
        if (ConfigValues.getNotifyType() == NotifyType.BOARD || ConfigValues.getNotifyType() == NotifyType.BOTH)
            ScoreboardManager.update();
        if (ConfigValues.getNotifyType() == NotifyType.ACTIONBAR || ConfigValues.getNotifyType() == NotifyType.BOTH)
            ActionBarManager.update();
        ActionBarManager.update();
    }
}

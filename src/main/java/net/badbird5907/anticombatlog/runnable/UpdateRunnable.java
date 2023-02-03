package net.badbird5907.anticombatlog.runnable;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.UnCombatTagEvent;
import net.badbird5907.anticombatlog.manager.ActionBarManager;
import net.badbird5907.anticombatlog.manager.BossBarManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.manager.ScoreboardManager;
import net.badbird5907.anticombatlog.object.NotifyType;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        NPCManager.update();
        Map<UUID, Integer> stillInCombat = new ConcurrentHashMap<>();
        AntiCombatLog.getInCombatTag().forEach(((uuid, integer) -> {
            if (UUIDUtil.contains(AntiCombatLog.getFreezeTimer(), uuid)) {
                UUIDUtil.remove(AntiCombatLog.getFreezeTimer(), uuid);
                stillInCombat.put(uuid, integer);
                return;
            }
            if (Bukkit.getPlayer(uuid) != null) {
                int a = integer - 1;
                if (a > 0)
                    stillInCombat.put(uuid, a);
                else {
                    Player player = Bukkit.getPlayer(uuid);
                    UnCombatTagEvent event = new UnCombatTagEvent(player);
                    Bukkit.getPluginManager().callEvent(event);
                    ScoreboardManager.getScoreboards().remove(uuid);
                    BossBarManager.getInstance().removeBossBar(player);
                    Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    Bukkit.getPlayer(uuid).sendMessage(ConfigValues.getCombatExpiredMessage());
                }
            }
        }));
        AntiCombatLog.setInCombatTag(stillInCombat);
        if (ConfigValues.scoreboardEnabled())
            ScoreboardManager.update();
        if (ConfigValues.actionBarEnabled())
            ActionBarManager.update();
        if (ConfigValues.bossBarEnabled())
            BossBarManager.getInstance().update();
    }
}

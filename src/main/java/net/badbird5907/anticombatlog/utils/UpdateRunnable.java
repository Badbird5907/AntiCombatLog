package net.badbird5907.anticombatlog.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.UnCombatTagEvent;
import net.badbird5907.anticombatlog.manager.ActionBarManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.badbird5907.anticombatlog.AntiCombatLog.ALLOW_EXIT_IN_COMBAT;


public class UpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        NPCManager.update();
        Map<UUID, Integer> stillInCombat = new ConcurrentHashMap<>();
        AntiCombatLog.getInCombatTag().forEach(((uuid, integer) -> {
            if (AntiCombatLog.getFreezeTimer().contains(uuid)) {
                AntiCombatLog.getFreezeTimer().remove(uuid);
                stillInCombat.put(uuid, integer);
                return;
            }
            if (Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);
                int a = integer - 1;
                if (a > 0) {
                    stillInCombat.put(uuid, a);

                } else {
                    UnCombatTagEvent event = new UnCombatTagEvent(player);

                    Bukkit.getPluginManager().callEvent(event);

                    if (ConfigValues.scoreboardEnabled()) {
                        ScoreboardManager.getScoreboards().remove(uuid);
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
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

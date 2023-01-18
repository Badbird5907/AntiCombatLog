package net.badbird5907.anticombatlog.manager;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.CombatLogNPCSpawnEvent;
import net.badbird5907.anticombatlog.api.events.UnCombatTagEvent;
import net.badbird5907.anticombatlog.utils.ConfigUtils;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class CombatManager {
    @Getter
    private static final CombatManager instance = new CombatManager();

    private CombatManager() {
    }

    @Getter
    private final Map<UUID, Integer> inCombatTag = new HashMap<>(); //might do async idk
    @Getter
    private Map<UUID, String> toKillOnLogin;
    @Getter
    private final Set<UUID> freezeTimer = new HashSet<>();

    public void update() {
        inCombatTag.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            int time = entry.getValue();
            if (freezeTimer.contains(uuid)) {
                freezeTimer.remove(uuid);
                return false;
            }
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (time > 0) {
                    entry.setValue(time - 1);
                    return false;
                } else {
                    UnCombatTagEvent event = new UnCombatTagEvent(player);
                    Bukkit.getPluginManager().callEvent(event);
                    ScoreboardManager.getScoreboards().remove(uuid);
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    return true;
                }
            }
            return true;
        });
    }

    public void disconnect(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
            return;
        if (!AntiCombatLog.getInstance().getConfig().getBoolean("npc-combat-log")) {
            player.setHealth(0.0d);
            return;
        }
        if (ConfigUtils.getInstance().getExemptWorlds().contains(player.getWorld().getName()))
            return;
        CombatLogNPCSpawnEvent event = new CombatLogNPCSpawnEvent(player, AntiCombatLog.getInstance().getConfig().getInt("combat-log-seconds", 15), false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        int seconds = event.isIndefinite() ? -1 : event.getTime();
        NPCManager.getInstance().spawn(player, seconds);
        sendCombatLoggedMessage(player);
        CombatManager.getInstance().getFreezeTimer().add(player.getUniqueId());
    }

    public void init() {
        toKillOnLogin = AntiCombatLog.getInstance().getStorageProvider().getToKillOnLogin();
    }
    public void sendCombatLoggedMessage(Player player) {
        String message = StringUtils.format(AntiCombatLog.getInstance().getConfig().getString("messages.logged-out-combat",
                "&cYou have been combat tagged. If you log out right now, you will not be safe. Your combat tag expires in &e%1"),
                player.getName(), AntiCombatLog.getInstance().getConfig().getInt("combat-log-seconds", 15) + "");
        int rad = AntiCombatLog.getInstance().getConfig().getInt("combat-logged-message-radius", -1);
        if (rad == -1) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(message);
            }
        } else {
            /*
            List<Player> players = player.getWorld().getPlayers();
            for (Player value : players) {
                if (value.getLocation().distance(player.getLocation()) < rad) {
                    value.sendMessage(message);
                }
            }
             */
            int x = rad / 2;
            int y = rad / 2;
            int z = rad / 2;
            for (Entity nearbyEntity : player.getNearbyEntities(x, y, z)) {
                if (nearbyEntity instanceof Player) {
                    Player nearbyPlayer = (Player) nearbyEntity;
                    nearbyPlayer.sendMessage(message);
                }
            }
        }
    }
}

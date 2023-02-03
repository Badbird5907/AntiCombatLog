package net.badbird5907.anticombatlog.manager;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {
    @Getter
    private static final BossBarManager instance = new BossBarManager();
    private BossBarManager() {}

    private Map<UUID, BossBar> bossBars = new HashMap<>();

    public void sendBossBar(Player player, int seconds) {
        BossBar bossBar = bossBars.computeIfAbsent(player.getUniqueId(), uuid -> {
            BossBar bar = player.getServer().createBossBar(StringUtils.format(ConfigValues.getBossBar(), seconds + ""), BarColor.RED, BarStyle.SOLID);
            bar.addPlayer(player);
            return bar;
        });
        bossBar.setTitle(StringUtils.format(ConfigValues.getBossBar(), seconds + ""));
        bossBar.setProgress((double) seconds / ConfigValues.getCombatTagSeconds());
    }
    public void removeBossBar(Player player){
        if (player == null) return;
        if (bossBars.containsKey(player.getUniqueId())){
            bossBars.remove(player.getUniqueId()).removePlayer(player);
        }
    }

    public void update() {
        AntiCombatLog.getInCombatTag().forEach((uuid, integer) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                sendBossBar(player, integer);
            }
        });
    }
}

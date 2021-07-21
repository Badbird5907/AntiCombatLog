package net.badbird5907.anticombatlog.manager;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarManager {
    public static void sendBar(Player player,int seconds){
        player.sendActionBar(StringUtils.format(ConfigValues.getActionBarMessage(),seconds + ""));
    }
    public static void update(){
        AntiCombatLog.getInCombatTag().forEach((uuid, integer) -> {
            if (Bukkit.getPlayer(uuid) != null){
                sendBar(Bukkit.getPlayer(uuid),integer);
            }
        });
    }
}

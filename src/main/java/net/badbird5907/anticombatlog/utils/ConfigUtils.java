package net.badbird5907.anticombatlog.utils;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.NotifyType;

import java.util.List;

@Getter
public class ConfigUtils {
    @Getter
    private static final ConfigUtils instance = new ConfigUtils();
    private NotifyType notifyType;
    private List<String> exemptWorlds;


    public void init() {
        reload();
    }

    public void reload() {
        notifyType = NotifyType.valueOf(AntiCombatLog.getInstance().getConfig().getString("notify-type", "BOTH").toUpperCase());
        exemptWorlds = AntiCombatLog.getInstance().getConfig().getStringList("exempt-worlds");
    }

    public boolean scoreboardEnabled() {
        return notifyType == NotifyType.BOTH || notifyType == NotifyType.BOARD;
    }

}

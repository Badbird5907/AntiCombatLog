package net.badbird5907.anticombatlog.utils;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.NotifyType;
import org.bukkit.configuration.file.FileConfiguration;

import static net.badbird5907.anticombatlog.utils.StringUtils.format;

@Getter
public class ConfigValues {
    @Getter private static int combatLogSeconds = getConfig().getInt("combat-log-seconds");
    @Getter private static int combatTagSeconds = getConfig().getInt("combat-tag-seconds");
    @Getter private static int combatLoggedMessageRadius = getConfig().getInt("combat-logged-message-radius");
    @Getter private static int npcHitResetSecond = getConfig().getInt("npc-hit-reset-seconds");
    @Getter private static boolean npcCombatLog = getConfig().getBoolean("npc-combat-log");
    @Getter private static String combatTaggedMessage = format(getConfig().getString("messages.combat-tagged"));
    @Getter private static String logInAfterKillMessage = format(getConfig().getString("messages.log-in-after-kill"));
    @Getter private static String unCombatTaggedMessage = format(getConfig().getString("messages.un-combat-tagged"));
    @Getter private static String combatLoggedMessage = format(getConfig().getString("messages.logged-out-combat"));
    @Getter private static String actionBarMessage = format(getConfig().getString("messages.action-bar-message"));
    @Getter private static String combatExpiredMessage = format(getConfig().getString("messages.combat-expired"));
    @Getter private static NotifyType notifyType = NotifyType.valueOf(getConfig().getString("notify-type"));
    private static FileConfiguration getConfig(){
        return AntiCombatLog.getInstance().getConfig();
    }
    //this might crash the server
    public static void setCombatLogSeconds(int combatLogSeconds) {
        getConfig().set("combat-log-seconds",combatLogSeconds);
        saveReload();
        ConfigValues.combatLogSeconds = combatLogSeconds;
    }
    public static void setCombatTagSeconds(int seconds) {
        getConfig().set("combat-tag-seconds",seconds);
        saveReload();
        ConfigValues.combatTagSeconds = seconds;
    }

    public static void setNpcCombatLog(boolean bool) {
        getConfig().set("npc-combat-log",bool);
        saveReload();
        ConfigValues.npcCombatLog = bool;
    }

    public static void setCombatTaggedMessage(String combatTaggedMessage) {
        getConfig().set("messages.combat-tagged",combatTaggedMessage);
        saveReload();
        ConfigValues.combatTaggedMessage = combatTaggedMessage;
    }

    public static void setUnCombatTaggedMessage(String unCombatTaggedMessage) {
        getConfig().set("messages.un-combat-tagged",unCombatTaggedMessage);
        saveReload();
        ConfigValues.unCombatTaggedMessage = unCombatTaggedMessage;
    }

    public static void setCombatLoggedMessage(String combatLoggedMessage) {
        getConfig().set("messages.un-combat-tagged",unCombatTaggedMessage);
        saveReload();
        ConfigValues.combatLoggedMessage = combatLoggedMessage;
    }
    public static void setCombatLoggedMessageRadius(int radius){
        getConfig().set("combat-logged-message-radius",radius);
        saveReload();
        combatLoggedMessageRadius = radius;
    }

    public static void setNotifyType(NotifyType notifyType) {
        getConfig().set("notify-type",notifyType);
        saveReload();
        ConfigValues.notifyType = notifyType;
    }

    public static void setActionBarMessage(String actionBarMessage) {
        getConfig().set("messages.action-bar-message",actionBarMessage);
        saveReload();
        ConfigValues.actionBarMessage = actionBarMessage;
    }

    public static void setNpcHitResetSecond(int npcHitResetSecond) {
        getConfig().set("npc-hit-reset-seconds",npcHitResetSecond);
        saveReload();
        ConfigValues.npcHitResetSecond = npcHitResetSecond;
    }

    public static void setLogInAfterKillMessage(String logInAfterKillMessage) {
        getConfig().set("messages.log-in-after-kill",logInAfterKillMessage);
        saveReload();
        ConfigValues.logInAfterKillMessage = logInAfterKillMessage;
    }

    public static void setCombatExpiredMessage(String combatExpiredMessage) {
        getConfig().set("messages.combat-expired",combatExpiredMessage);
        saveReload();
        ConfigValues.combatExpiredMessage = combatExpiredMessage;
    }

    private static void saveReload(){
        AntiCombatLog.getInstance().saveConfig();
        AntiCombatLog.getInstance().reloadConfig();
    }

    public static void load(){
        combatLogSeconds = getConfig().getInt("combat-log-seconds");
        npcCombatLog = getConfig().getBoolean("npc-combat-log");
        combatTaggedMessage = format(getConfig().getString("messages.combat-tagged"));
        unCombatTaggedMessage = format(getConfig().getString("messages.un-combat-tagged"));
        combatLoggedMessageRadius = getConfig().getInt("combat-logged-message-radius");
        combatLoggedMessage = format(getConfig().getString("messages.logged-out-combat"));
        combatTagSeconds = getConfig().getInt("combat-tag-seconds");
        notifyType = NotifyType.valueOf(getConfig().getString("notify-type"));
        actionBarMessage = format(getConfig().getString("messages.action-bar-message"));
        npcHitResetSecond = getConfig().getInt("npc-hit-reset-seconds");
    }
    public static void reload(){
        load();
    }
}

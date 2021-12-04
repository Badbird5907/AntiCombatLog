package net.badbird5907.anticombatlog.utils;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.NotifyType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static net.badbird5907.anticombatlog.utils.StringUtils.format;

@Getter
public class ConfigValues {
    @Getter
    private static int combatLogSeconds = 15;
    @Getter
    private static int combatTagSeconds = 15;
    @Getter
    private static int combatLoggedMessageRadius = -1;
    @Getter
    private static int npcHitResetSecond = 35;
    @Getter
    private static boolean npcCombatLog = true;
    @Getter
    private static String combatTaggedMessage = null;
    @Getter
    private static String logInAfterKillMessage = null;
    @Getter
    private static String unCombatTaggedMessage = null;
    @Getter
    private static String combatLoggedMessage = null;
    @Getter
    private static String actionBarMessage = null;
    @Getter
    private static String combatExpiredMessage = null;
    @Getter
    private static String killMessage = null;
    @Getter
    private static NotifyType notifyType = NotifyType.BOTH;

    private static FileConfiguration getConfig() {
        return AntiCombatLog.getInstance().getConfig();
    }

    //this might crash the server
    public static void setCombatLogSeconds(int combatLogSeconds) {
        getConfig().set("combat-log-seconds", combatLogSeconds);
        saveReload();
        ConfigValues.combatLogSeconds = combatLogSeconds;
    }

    public static void setCombatTagSeconds(int seconds) {
        getConfig().set("combat-tag-seconds", seconds);
        saveReload();
        ConfigValues.combatTagSeconds = seconds;
    }

    public static void setNpcCombatLog(boolean bool) {
        getConfig().set("npc-combat-log", bool);
        saveReload();
        ConfigValues.npcCombatLog = bool;
    }

    public static void setCombatTaggedMessage(String combatTaggedMessage) {
        getConfig().set("messages.combat-tagged", combatTaggedMessage);
        saveReload();
        ConfigValues.combatTaggedMessage = combatTaggedMessage;
    }

    public static void setUnCombatTaggedMessage(String unCombatTaggedMessage) {
        getConfig().set("messages.un-combat-tagged", unCombatTaggedMessage);
        saveReload();
        ConfigValues.unCombatTaggedMessage = unCombatTaggedMessage;
    }

    public static void setCombatLoggedMessage(String combatLoggedMessage) {
        getConfig().set("messages.un-combat-tagged", unCombatTaggedMessage);
        saveReload();
        ConfigValues.combatLoggedMessage = combatLoggedMessage;
    }

    public static void setCombatLoggedMessageRadius(int radius) {
        getConfig().set("combat-logged-message-radius", radius);
        saveReload();
        combatLoggedMessageRadius = radius;
    }

    public static void setNotifyType(NotifyType notifyType) {
        getConfig().set("notify-type", notifyType);
        saveReload();
        ConfigValues.notifyType = notifyType;
    }

    public static void setActionBarMessage(String actionBarMessage) {
        getConfig().set("messages.action-bar-message", actionBarMessage);
        saveReload();
        ConfigValues.actionBarMessage = actionBarMessage;
    }

    public static void setNpcHitResetSecond(int npcHitResetSecond) {
        getConfig().set("npc-hit-reset-seconds", npcHitResetSecond);
        saveReload();
        ConfigValues.npcHitResetSecond = npcHitResetSecond;
    }

    public static void setLogInAfterKillMessage(String logInAfterKillMessage) {
        getConfig().set("messages.log-in-after-kill", logInAfterKillMessage);
        saveReload();
        ConfigValues.logInAfterKillMessage = logInAfterKillMessage;
    }

    public static void setCombatExpiredMessage(String combatExpiredMessage) {
        getConfig().set("messages.combat-expired", combatExpiredMessage);
        saveReload();
        ConfigValues.combatExpiredMessage = combatExpiredMessage;
    }

    public static void setKillMessage(String killMessage) {
        getConfig().set("messages.kill-messages", killMessage);
        saveReload();
        ConfigValues.killMessage = killMessage;
    }

    private static void saveReload() {
        AntiCombatLog.getInstance().saveConfig();
        AntiCombatLog.getInstance().reloadConfig();
    }

    public static void load() {
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
        killMessage = format(getConfig().getString("messages.kill-message"));
        logInAfterKillMessage = format(getConfig().getString("messages.log-in-after-kill"));
        combatExpiredMessage = format(getConfig().getString("messages.combat-expired"));
    }

    public static void reload() {
        load();
    }

    public static boolean scoreboardEnabled() {
        return notifyType == NotifyType.BOTH || notifyType == NotifyType.BOARD;
    }

    public static void enable(JavaPlugin plugin) {
        if (!new File(plugin.getDataFolder() + "/config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        load();
    }
}

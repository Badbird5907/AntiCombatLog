package net.badbird5907.anticombatlog.utils;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.NotifyType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    @Getter
    private static List<String> blockedCommands = null;
    @Getter
    private static boolean enableBlockedCommands = true;
    @Getter
    private static String blockedCommandMessage = null;

    @Getter
    private static boolean showPlayerNameOnly = false,
    setDeathMessage = true;

    @Getter
    private static boolean enableHologram = true;

    @Getter
    private static List<String> exemptWorlds;

    private static FileConfiguration getConfig() {
        return AntiCombatLog.getInstance().getConfig();
    }

    private static void saveReload() {
        AntiCombatLog.getInstance().saveConfig();
        AntiCombatLog.getInstance().reloadConfig();
    }

    public static void load() {
        combatLogSeconds = getConfig().getInt("combat-log-seconds");
        npcCombatLog = getConfig().getBoolean("npc-combat-log");
        combatTaggedMessage = format(getConfig().getString("messages.combat-tagged","&cYou have been combat tagged. If you log out right now, you will not be safe. Your combat tag expires in &e%1"));
        unCombatTaggedMessage = format(getConfig().getString("messages.un-combat-tagged"));
        combatLoggedMessageRadius = getConfig().getInt("combat-logged-message-radius");
        combatLoggedMessage = format(getConfig().getString("messages.logged-out-combat"));
        combatTagSeconds = getConfig().getInt("combat-tag-seconds");
        notifyType = NotifyType.valueOf(getConfig().getString("notify-type").toUpperCase());
        actionBarMessage = format(getConfig().getString("messages.action-bar-message"));
        npcHitResetSecond = getConfig().getInt("npc-hit-reset-seconds");
        killMessage = format(getConfig().getString("messages.kill-message"));
        logInAfterKillMessage = format(getConfig().getString("messages.log-in-after-kill","&cYou logged out while in combat and was killed by &e%1"));
        combatExpiredMessage = format(getConfig().getString("messages.combat-expired"));
        blockedCommands = getConfig().getStringList("blocked-commands.blocked");
        enableBlockedCommands = getConfig().getBoolean("blocked-commands.enabled");
        blockedCommandMessage = format(getConfig().getString("messages.blocked-command", "&cYou cannot use this command while in combat."));
        showPlayerNameOnly = getConfig().getBoolean("only-show-player-name",false);
        enableHologram = getConfig().getBoolean("enable-hologram",true);
        setDeathMessage = getConfig().getBoolean("set-death-message",true);
        exemptWorlds = getConfig().getStringList("exempt-worlds");
    }

    public static void reload() {
        AntiCombatLog.getInstance().setConfig(null);
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

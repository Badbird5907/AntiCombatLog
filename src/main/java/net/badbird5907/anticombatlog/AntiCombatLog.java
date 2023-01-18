package net.badbird5907.anticombatlog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.anticombatlog.api.events.CombatTagEvent;
import net.badbird5907.anticombatlog.commands.AntiCombatLogCommand;
import net.badbird5907.anticombatlog.commands.ResetTagCommand;
import net.badbird5907.anticombatlog.hooks.HookManager;
import net.badbird5907.anticombatlog.listener.BlockedCommandsListener;
import net.badbird5907.anticombatlog.listener.CombatListener;
import net.badbird5907.anticombatlog.listener.ConnectionListener;
import net.badbird5907.anticombatlog.listener.NPCListener;
import net.badbird5907.anticombatlog.manager.CombatManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.object.UpdateRunnable;
import net.badbird5907.anticombatlog.storage.StorageProvider;
import net.badbird5907.anticombatlog.storage.impl.FileStorageProvider;
import net.badbird5907.anticombatlog.utils.ConfigUtils;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.bstats.Metrics;
import net.badbird5907.blib.spigotmc.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public final class AntiCombatLog extends JavaPlugin { //TODO config editor in game
    @Getter
    private static AntiCombatLog instance;
    private static UpdateRunnable updateRunnable;
    @Getter
    private static final List<UUID> killed = new ArrayList<>();
    @Getter
    private static String newVersion = "";
    private StorageProvider storageProvider = new FileStorageProvider(); // maybe implement DBs? Kinda useless for this plugin though

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void tag(Player victim, Player attacker) {
        if (victim.getGameMode() == GameMode.CREATIVE || attacker.getGameMode() == GameMode.CREATIVE)
            return;
        CombatTagEvent event = new CombatTagEvent(victim, attacker);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        boolean sendMessageVictim = true;
        boolean sendMessageAttacker = true;
        if (isCombatTagged(victim)) {
            CombatManager.getInstance().getInCombatTag().remove(victim.getUniqueId());
            sendMessageVictim = false;
        }
        if (CombatManager.getInstance().getInCombatTag().containsKey(attacker.getUniqueId())) {
            CombatManager.getInstance().getInCombatTag().remove(attacker.getUniqueId());
            sendMessageAttacker = false;
        }
        int tagSeconds = AntiCombatLog.getInstance().getConfig().getInt("combat-tag-seconds", 15);
        CombatManager.getInstance().getInCombatTag().put(victim.getUniqueId(), tagSeconds);
        CombatManager.getInstance().getInCombatTag().put(attacker.getUniqueId(), tagSeconds);
        String combatTagged = getInstance().getConfig().getString("messages.combat-tagged", ""); // TODO defaults
        if (sendMessageVictim)
            victim.sendMessage(StringUtils.format(combatTagged, tagSeconds + ""));
        if (sendMessageAttacker)
            attacker.sendMessage(StringUtils.format(combatTagged, tagSeconds + ""));
        if (getInstance().getConfig().getBoolean("disable.fly", false)) {
            victim.setAllowFlight(false);
            victim.setFlying(false);
            attacker.setAllowFlight(false);
            attacker.setFlying(false);
        }
    }

    public static boolean isCombatTagged(Player player) {
        return CombatManager.getInstance().getInCombatTag().containsKey(player.getUniqueId());
    }

    public static boolean updateAvailable = false;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting AntiCombatLog V." + getDescription().getVersion() + " by Badbird5907");
        long start = System.currentTimeMillis();
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        saveDefaultConfig();
        ConfigUtils.getInstance().init();
        bLib.create(this);
        Metrics metrics = new Metrics(this, 12150);

        getCommand("anticombatlog").setExecutor(new AntiCombatLogCommand());
        getCommand("resettag").setExecutor(new ResetTagCommand());

        storageProvider.init();
        CombatManager.getInstance().init();

        Listener[] listeners = new Listener[]{new CombatListener(), new ConnectionListener(), new NPCListener(), new BlockedCommandsListener()};
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
        updateRunnable = new UpdateRunnable();
        updateRunnable.runTaskTimer(this, 40L, 20L);
        if (getConfig().getBoolean("update-check")) {
            new UpdateChecker(94540).getVersion(version -> {
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    updateAvailable = true;
                    newVersion = version;
                    getLogger().info("There a new update available! Download at https://badbird5907.xyz/anticombatlog-latest");
                }
                //just dont say anything
            });
        }
        getLogger().info("Enabling plugin hooks...");
        enableHooks();
        getLogger().info(net.badbird5907.blib.utils.StringUtils.replacePlaceholders("Finished initializing AntiCombatLog (took %1 ms.)", (System.currentTimeMillis() - start) + ""));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling AntiCombatLog!");
        getLogger().info("Disabling plugin hooks...");
        disableHooks();

        storageProvider.saveToKillOnLogin(CombatManager.getInstance().getToKillOnLogin());

        getLogger().info("Despawning current combat logged NPCs.");
        NPCManager.getInstance().getNpcs().forEach((uuid, integerNPCStringTriplet) -> {
            NPCManager.getInstance().despawn(uuid);
        });
        if (updateRunnable != null)
            updateRunnable.cancel();
    }

    public void clearCombatTag(Player player) {
        CombatManager.getInstance().getInCombatTag().remove(player.getUniqueId());
        if (ConfigUtils.getInstance().scoreboardEnabled())
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private void enableHooks() {
        HookManager.enableAllLoaded();
    }

    private void disableHooks() {
        HookManager.disableAll();
    }

    public void saveData() {
        storageProvider.saveToKillOnLogin(CombatManager.getInstance().getToKillOnLogin());
    }
}

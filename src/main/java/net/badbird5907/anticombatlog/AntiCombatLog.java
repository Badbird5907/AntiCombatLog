package net.badbird5907.anticombatlog;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.anticombatlog.api.events.CombatLogNPCSpawnEvent;
import net.badbird5907.anticombatlog.api.events.CombatTagEvent;
import net.badbird5907.anticombatlog.commands.AntiCombatLogCommand;
import net.badbird5907.anticombatlog.commands.ResetTagCommand;
import net.badbird5907.anticombatlog.hooks.HookManager;
import net.badbird5907.anticombatlog.listener.BlockedCommandsListener;
import net.badbird5907.anticombatlog.listener.CombatListener;
import net.badbird5907.anticombatlog.listener.ConnectionListener;
import net.badbird5907.anticombatlog.listener.NPCListener;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.badbird5907.anticombatlog.utils.UpdateRunnable;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.bstats.Metrics;
import net.badbird5907.blib.spigotmc.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public final class AntiCombatLog extends JavaPlugin { //TODO config editor in game
    @Getter
    @Setter
    private static Map<UUID, Integer> inCombatTag = new HashMap<>(); //might do async idk
    @Getter
    @Setter
    private static Map<UUID, String> toKillOnLogin = new HashMap<>();
    @Getter
    @Setter
    private static Set<UUID> freezeTimer = new HashSet<>();
    @Getter
    private static AntiCombatLog instance;
    private static UpdateRunnable updateRunnable;
    private static File file = null;
    @Getter
    private static final List<UUID> killed = new ArrayList<>();
    @Getter
    private static String newVersion = "";

    public static void loadData() {
        String json = StringUtils.readFile(file);
        toKillOnLogin = new HashMap<>();
        Map<String, String> a = new Gson().fromJson(json, HashMap.class); //should fix random classcast exception
        a.forEach((a1, b) -> toKillOnLogin.put(UUID.fromString(a1), b)); //FIXME
    }

    public static void saveData() {
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), () -> {
            String json = new Gson().toJson(toKillOnLogin);
            try {
                PrintStream ps = new PrintStream(file);
                ps.print(json);
                ps.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendCombatLoggedMessage(Player player) {
        String msg = ConfigValues.getCombatLoggedMessage();
        if (msg == null) return;
        String message = StringUtils.format(msg, player.getName(), ConfigValues.getCombatLogSeconds() + "");
        if (ConfigValues.getCombatLoggedMessageRadius() == -1) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(message);
            }
        } else {
            List<Player> players = player.getWorld().getPlayers();
            for (Player value : players) {
                if (value.getLocation().distance(player.getLocation()) < ConfigValues.getCombatLoggedMessageRadius()) {
                    value.sendMessage(message);
                }
            }
        }
    }

    public static void tag(Player victim, Player attacker) {
        if (victim.getGameMode() == GameMode.CREATIVE || attacker.getGameMode() == GameMode.CREATIVE)
            return;
        CombatTagEvent event = new CombatTagEvent(victim, attacker);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        boolean sendMessageVictim = true;
        boolean sendMessageAttacker = true;
        if (inCombatTag.containsKey(victim.getUniqueId())) {
            inCombatTag.remove(victim.getUniqueId());
            sendMessageVictim = false;
        }
        if (inCombatTag.containsKey(attacker.getUniqueId())) {
            inCombatTag.remove(attacker.getUniqueId());
            sendMessageAttacker = false;
        }
        inCombatTag.put(victim.getUniqueId(), ConfigValues.getCombatTagSeconds());
        inCombatTag.put(attacker.getUniqueId(), ConfigValues.getCombatTagSeconds());
        if (sendMessageVictim)
            victim.sendMessage(StringUtils.format(ConfigValues.getCombatTaggedMessage(), ConfigValues.getCombatTagSeconds() + ""));
        if (sendMessageAttacker)
            attacker.sendMessage(StringUtils.format(ConfigValues.getCombatTaggedMessage(), ConfigValues.getCombatTagSeconds() + ""));
        if (ConfigValues.isDisableFly()) {
            victim.setAllowFlight(false);
            victim.setFlying(false);
            attacker.setAllowFlight(false);
            attacker.setFlying(false);
        }
    }

    public static void tag(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        CombatTagEvent event = new CombatTagEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        boolean sendMessage = true;
        if (inCombatTag.containsKey(player.getUniqueId())) {
            inCombatTag.remove(player.getUniqueId());
            sendMessage = false;
        }
        inCombatTag.put(player.getUniqueId(), ConfigValues.getCombatTagSeconds());
        if (sendMessage)
            player.sendMessage(StringUtils.format(ConfigValues.getCombatTaggedMessage(), ConfigValues.getCombatTagSeconds() + ""));
        if (ConfigValues.isDisableFly()) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    public static void disconnect(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
            return;
        if (!ConfigValues.isNpcCombatLog()) {
            player.setHealth(0.0d);
            return;
        }
        if (ConfigValues.getExemptWorlds().contains(player.getWorld().getName()))
            return;
        CombatLogNPCSpawnEvent event = new CombatLogNPCSpawnEvent(player, ConfigValues.getCombatLogSeconds(), false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        int seconds = event.isIndefinite() ? -1 : event.getTime();
        NPCManager.spawn(player, seconds);
        sendCombatLoggedMessage(player);
        freezeTimer.add(player.getUniqueId());
    }

    public static void join(Player player) {
        if (toKillOnLogin.containsKey(player.getUniqueId())) {
            toKillOnLogin.remove(player.getUniqueId());
            killed.add(player.getUniqueId());
            /*
            List<ItemStack> toKeep = new ArrayList<>();
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR)
                    continue;
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                    if (AEAPI.hasWhitescroll(item)) {
                        toKeep.add(item);
                        AEAPI.removeWhitescroll(item);
                        continue;
                    }
                }
            }
            player.getInventory().clear();
             */
            player.setHealth(0.0d);
            getInstance().clearCombatTag(player);
            saveData();
            return;
        }
        if (NPCManager.isSpawned(player.getUniqueId())) {
            double health = NPCManager.getHealth(player.getUniqueId());
            player.setHealth(health);
            NPCManager.despawn(player.getUniqueId());
        }
        if (freezeTimer.contains(player.getUniqueId())) {
            freezeTimer.remove(player.getUniqueId());
            int a = getInCombatTag().getOrDefault(player.getUniqueId(), 0);
            getInCombatTag().put(player.getUniqueId(), a + getInstance().getConfig().getInt("login-after-combat-log-add-timer-seconds", 5));
        }
    }

    public static boolean isCombatTagged(Player player) {
        return getInCombatTag().containsKey(player.getUniqueId());
    }

    public static boolean updateAvailable = false;

    @Override
    public void onLoad() {
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting AntiCombatLog V." + getDescription().getVersion() + " by Badbird5907");
        long start = System.currentTimeMillis();
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        bLib.create(this);
        Metrics metrics = new Metrics(this, 12150);

        ConfigValues.enable(this);
        Objects.requireNonNull(getCommand("anticombatlog")).setExecutor(new AntiCombatLogCommand());
        Objects.requireNonNull(getCommand("resettag")).setExecutor(new ResetTagCommand());
        file = new File(getInstance().getDataFolder() + "/data.json");
        if (!file.exists()) {
            file.createNewFile();
            PrintStream ps = new PrintStream(file);
            ps.print("{}");
            ps.close();
        }
        loadData();
        Listener[] listeners = new Listener[]{new CombatListener(), new ConnectionListener(), new NPCListener(), new BlockedCommandsListener()};
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
        HookManager.init();
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
        String json = new Gson().toJson(toKillOnLogin);
        try {
            PrintStream ps = new PrintStream(file);
            ps.print(json);
            ps.close();
        } catch (FileNotFoundException e) {
            System.err.println("Failed to save data.json!");
            e.printStackTrace();
        }
        getLogger().info("Despawning current combat logged NPCs.");
        NPCManager.getNpcs().forEach((uuid, integerNPCStringTriplet) -> {
            NPCManager.despawn(uuid);
        });
        if (updateRunnable != null)
            updateRunnable.cancel();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigValues.reload();
    }

    private FileConfiguration config = null;

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            config = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
        }
        return config;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    public void clearCombatTag(Player player) {
        AntiCombatLog.getInCombatTag().remove(player.getUniqueId());
        if (ConfigValues.scoreboardEnabled())
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private void enableHooks() {
        HookManager.enableAllLoaded();
    }

    private void disableHooks() {
        HookManager.disableAll();
    }
}

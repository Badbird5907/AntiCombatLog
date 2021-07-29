package net.badbird5907.anticombatlog;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.anticombatlog.api.events.CombatTagEvent;
import net.badbird5907.anticombatlog.commands.AntiCombatLogCommand;
import net.badbird5907.anticombatlog.commands.ResetTagCommand;
import net.badbird5907.anticombatlog.listener.CombatListener;
import net.badbird5907.anticombatlog.listener.ConnectionListener;
import net.badbird5907.anticombatlog.listener.NPCListener;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.runnable.UpdateRunnable;
import net.badbird5907.anticombatlog.spigot.Metrics;
import net.badbird5907.anticombatlog.spigot.UpdateChecker;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class AntiCombatLog extends JavaPlugin { //TODO config editor in game
    @Getter
    @Setter
    private static Map<UUID,Integer> inCombatTag = new HashMap<>(); //might do async idk
    @Getter
    @Setter
    private static Map<UUID,String> toKillOnLogin = new HashMap<>();
    @Getter
    private static AntiCombatLog instance;
    private static UpdateRunnable updateRunnable;
    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting AntiCombatLog V." + getDescription().getVersion() + " by Badbird5907");
        long start = System.currentTimeMillis();
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        Metrics metrics = new Metrics(this,12150);
        ConfigValues.enable(this);
        getCommand("anticombatlog").setExecutor(new AntiCombatLogCommand());
        getCommand("resettag").setExecutor(new ResetTagCommand());
        file = new File(getInstance().getDataFolder().getAbsolutePath() + "/data.json");
        if (!file.exists()){
            file.createNewFile();
            PrintStream ps = new PrintStream(file);
            ps.print("{}");
        }
        loadData();
        Listener[] listeners = new Listener[]{new CombatListener(),new ConnectionListener(),new NPCListener()};
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener,this);
        }
        updateRunnable = new UpdateRunnable();
        updateRunnable.runTaskTimer(this, 40L, 20L);
        if (getConfig().getBoolean("update-check")){
            new UpdateChecker(this,94540).getVersion(version ->{
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)){
                    getLogger().info("There a new update available! Download at https://badbird5907.xyz/anticombatlog");
                }else{
                    //just dont say anything
                }
            });
        }
        getLogger().info(StringUtils.replacePlaceholders("Done initializing AntiCombatLog (took %1 ms.)",(System.currentTimeMillis() - start) +""));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling AntiCombatLog! Thanks for choosing this!");
        String json = new Gson().toJson(toKillOnLogin);
        try {
            PrintStream ps = new PrintStream(file);
            ps.print(json);
            ps.close();
        } catch (FileNotFoundException e) {
            System.err.println("Failed to save data.json!");
            e.printStackTrace();
        }
        if (updateRunnable != null)
            updateRunnable.cancel();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigValues.reload();
    }

    @Override
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
    }
    private static File file = null;
    @SneakyThrows
    public static void loadData(){
        String json = StringUtils.readFile(file);
        toKillOnLogin = new HashMap<>();
        Map<String,String> a = new Gson().fromJson(json,HashMap.class); //should fix random classcast exception
        a.forEach((a1,b)->toKillOnLogin.put(UUID.fromString(a1),b)); //FIXME
    }
    public static void saveData(){
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(),()->{
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
    public static void sendCombatLoggedMessage(Player player){
        if (ConfigValues.getCombatLoggedMessageRadius() == -1){
            Bukkit.broadcastMessage((StringUtils.format(ConfigValues.getCombatLoggedMessage(), player.getName(), ConfigValues.getCombatLogSeconds() + "")));
        }else{
            List<Player> players = player.getWorld().getPlayers();
            for (Player value : players) {
                if (value.getLocation().distance(player.getLocation()) < ConfigValues.getCombatLoggedMessageRadius()) {
                    value.sendMessage(StringUtils.format(ConfigValues.getCombatLoggedMessage(), player.getName(), ConfigValues.getCombatLogSeconds() + ""));
                }
            }
        }
    }
    public static void tag(Player victim,Player attacker){
        if ((victim.getGameMode() != GameMode.SURVIVAL && victim.getGameMode() != GameMode.ADVENTURE) && (attacker.getGameMode() != GameMode.SURVIVAL && victim.getGameMode() != GameMode.ADVENTURE))
            return;
        CombatTagEvent event = new CombatTagEvent(victim,attacker);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        boolean sendMessageVictim = true;
        boolean sendMessageAttacker = true;
        if (inCombatTag.containsKey(victim.getUniqueId())){
            inCombatTag.remove(victim.getUniqueId());
            sendMessageVictim = false;
        }
        if (inCombatTag.containsKey(attacker.getUniqueId())){
            inCombatTag.remove(attacker.getUniqueId());
            sendMessageAttacker = false;
        }
        inCombatTag.put(victim.getUniqueId(),ConfigValues.getCombatTagSeconds());
        if (sendMessageVictim)
            victim.sendMessage(StringUtils.format(ConfigValues.getCombatTaggedMessage(),ConfigValues.getCombatTagSeconds() + ""));
        if (sendMessageAttacker)
            victim.sendMessage(StringUtils.format(ConfigValues.getCombatTaggedMessage(),ConfigValues.getCombatTagSeconds() + ""));
    }
    public static void disconnect(Player player){
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
            return;
        if (!ConfigValues.isNpcCombatLog()){
            player.setHealth(0.0d);
            return;
        }
        NPCManager.spawn(player,ConfigValues.getCombatLogSeconds());
        sendCombatLoggedMessage(player);
        getInCombatTag().remove(player.getUniqueId());
    }
    @Getter
    private static List<UUID> killed = new ArrayList<>();
    public static void join(Player player){
        if (toKillOnLogin.containsKey(player.getUniqueId())){
            toKillOnLogin.remove(player.getUniqueId());
            killed.add(player.getUniqueId());
            player.getInventory().clear();
            player.setHealth(0.0d);
            saveData();
            return;
        }
        if (NPCManager.isSpawned(player.getUniqueId())){
            double health = NPCManager.getHealth(player.getUniqueId());
            player.setHealth(health);
            NPCManager.despawn(player.getUniqueId());
        }
    }
    public static boolean isCombatTagged(Player player){
        return getInCombatTag().containsKey(player.getUniqueId());
    }
}

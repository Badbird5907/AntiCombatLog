package net.badbird5907.anticombatlog;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.anticombatlog.listener.CombatListener;
import net.badbird5907.anticombatlog.listener.ConnectionListener;
import net.badbird5907.anticombatlog.listener.NPCListener;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.runnbale.UpdateRunnable;
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
import java.util.concurrent.ConcurrentHashMap;

public final class AntiCombatLog extends JavaPlugin {
    @Getter
    @Setter
    private static Map<UUID,Integer> inCombatTag = new HashMap<>(); //might do async idk
    @Getter
    @Setter
    private static Map<UUID,String> toKillOnLogin = new ConcurrentHashMap<>();
    @Getter
    private static AntiCombatLog instance;
    private static UpdateRunnable updateRunnable;
    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        file = new File(getInstance().getDataFolder().getAbsolutePath() + "/data.json");
        if (!file.exists()){
            file.createNewFile();
            PrintStream ps = new PrintStream(file);
            ps.print(new Gson().toJson(toKillOnLogin));
        }
        loadData();
        updateRunnable = new UpdateRunnable();
        updateRunnable.runTaskTimer(this,40l,20l);
        Listener[] listeners = new Listener[]{new CombatListener(),new ConnectionListener(),new NPCListener()};
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener,this);
        }
    }

    @Override
    public void onDisable() {
        String json = new Gson().toJson(toKillOnLogin);
        try {
            PrintStream ps = new PrintStream(file);
            ps.print(json);
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }        updateRunnable.cancel();
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
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(),()->{
            toKillOnLogin = new Gson().fromJson(StringUtils.readFile(file), ConcurrentHashMap.class);
        });
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
    public static void tag(Player player){
        if (inCombatTag == null){
            System.err.print("null ?!");
            inCombatTag = new HashMap<>();
        }
        boolean sendMessage = true;
        if (inCombatTag.containsKey(player.getUniqueId())){
            inCombatTag.remove(player.getUniqueId());
            sendMessage = false;
        }
        inCombatTag.put(player.getUniqueId(),ConfigValues.getCombatTagSeconds());
        if (sendMessage)
            player.sendMessage(StringUtils.format(ConfigValues.getCombatTaggedMessage(),ConfigValues.getCombatTagSeconds() + ""));
    }
    public static void disconnect(Player player){
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
            return;
        NPCManager.spawn(player,ConfigValues.getCombatLogSeconds());
        sendCombatLoggedMessage(player);
        getInCombatTag().remove(player.getUniqueId());
    }
    @Getter
    private static List<UUID> killed = new ArrayList<>();
    public static void join(Player player){
        if (toKillOnLogin.containsKey(player.getUniqueId())){
            killed.add(player.getUniqueId());
            player.setHealth(0.0d);
            toKillOnLogin.remove(player.getUniqueId());
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

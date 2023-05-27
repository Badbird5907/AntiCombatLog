package net.badbird5907.anticombatlog.hooks.impl;

import me.realized.duels.api.Duels;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.CombatTagEvent;
import net.badbird5907.anticombatlog.hooks.IHook;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DuelsHook implements IHook, Listener {
    @Override
    public void enable(AntiCombatLog plugin) {

    }

    @Override
    public void disable(AntiCombatLog plugin) {

    }

    @Override
    public String getName() {
        return "Duels Hook";
    }

    @Override
    public String[] getDependencies() {
        return new String[]{"Duels"};
    }

    @Override
    public String getConfigKey() {
        return "duels";
    }

    @EventHandler
    public void onCombatLog(CombatTagEvent event) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Duels");
        if (plugin == null) {
            return;
        }
        Duels duelsApi = (Duels) plugin;
        if (duelsApi.getArenaManager().isInMatch(event.getVictim()) && AntiCombatLog.getInstance().getConfig().getBoolean("hooks.duels.dont-combat-tag", true)) {
            event.setCancelled(true);
            return;
        }
    }
}

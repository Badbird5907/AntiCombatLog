package net.badbird5907.anticombatlog.listener;

import net.advancedplugins.ae.api.AEAPI;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.citizensnpcs.api.event.NPCDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

public class NPCListener implements Listener {
    @EventHandler
    public void onNpcDeath(NPCDeathEvent event) {
        event.setDroppedExp((int) event.getNPC().getTrait(CombatNPCTrait.class).getXp());
        if (!event.getEvent().getEntity().getWorld().isGameRule("keepInventory")) {
            if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                event.getDrops().addAll(event.getNPC().getTrait(CombatNPCTrait.class).getItems().stream()
                        .filter(item -> !AEAPI.hasWhitescroll(item)).collect(Collectors.toList()));
            } else {
                event.getDrops().addAll(event.getNPC().getTrait(CombatNPCTrait.class).getItems());
            }
        }
        //event.getDrops().addAll(Arrays.stream(event.getNPC().getTrait(Equipment.class).getEquipment()).toList());
        String killer;
        if (event.getEvent().getEntity().getKiller() == null)
            killer = "null"; //https://discord.com/channels/315163488085475337/315625512753954816/916351371970740226 on citizens support discord
        else killer = event.getEvent().getEntity().getKiller().getName();
        AntiCombatLog.getToKillOnLogin().put(event.getNPC().getTrait(CombatNPCTrait.class).getUuid(), killer);
        AntiCombatLog.saveData();
    }
}

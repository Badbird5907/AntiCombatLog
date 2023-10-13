package net.badbird5907.anticombatlog.listener;

import net.advancedplugins.ae.api.AEAPI;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.hooks.impl.ExcellentEnchantUtil;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.badbird5907.blib.util.Logger;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class NPCListener implements Listener {
    @EventHandler
    public void onNpcDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        if (npc != null) { // idk why this is here but i'm keeping it just in case idk
            CombatNPCTrait trait = npc.getTraitNullable(CombatNPCTrait.class);
            if (trait != null) {
                event.setDroppedExp((int) trait.getXp());
                boolean value = Boolean.TRUE.equals(event.getEvent().getEntity().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY));
                if (!value) {
                    event.getDrops().addAll(trait.getItems().stream().filter(item -> {
                        if (item == null) return false;
                        return !shouldKeep(item);
                    }).collect(Collectors.toList()));
                }
                //event.getDrops().addAll(Arrays.stream(npc.getTrait(Equipment.class).getEquipment()).toList());
                String killer;
                if (event.getEvent().getEntity().getKiller() == null)
                    killer = "null"; //https://discord.com/channels/315163488085475337/315625512753954816/916351371970740226 on citizens support discord
                else killer = event.getEvent().getEntity().getKiller().getName();
                AntiCombatLog.getToKillOnLogin().put(trait.getUuid(), killer);
                AntiCombatLog.saveData();
            }
        } else Bukkit.getLogger().warning("NPC was null in NPCDeathEvent!");
    }

    private boolean shouldKeep(ItemStack item) {
        if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
            return AEAPI.hasWhitescroll(item);
        } else if (Bukkit.getPluginManager().isPluginEnabled("ExcellentEnchants")) {
            return ExcellentEnchantUtil.shouldKeep(item);
        } else {
            return false;
        }
    }
}

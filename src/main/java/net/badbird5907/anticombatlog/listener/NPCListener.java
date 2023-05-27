package net.badbird5907.anticombatlog.listener;

import net.advancedplugins.ae.api.AEAPI;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

public class NPCListener implements Listener {
    @EventHandler
    public void onNpcDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        if (npc != null) { // idk why this is here but i'm keeping it just in case idk
            CombatNPCTrait trait = npc.getTraitNullable(CombatNPCTrait.class);
            if (trait != null) {
                event.setDroppedExp((int) trait.getXp());
                Boolean value = event.getEvent().getEntity().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);
                if (Boolean.FALSE.equals(value)) {
                    if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                        event.getDrops().addAll(trait.getItems().stream()
                                .filter(item -> !AEAPI.hasWhitescroll(item)).collect(Collectors.toList()));
                    } else {
                        event.getDrops().addAll(trait.getItems());
                    }
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
}

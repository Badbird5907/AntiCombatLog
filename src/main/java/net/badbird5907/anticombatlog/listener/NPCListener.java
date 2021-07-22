package net.badbird5907.anticombatlog.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.NPCTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class NPCListener implements Listener {
    @EventHandler
    public void onNpcDeath(NPCDeathEvent event){
        event.setDroppedExp((int) event.getNPC().getTrait(NPCTrait.class).getXp());
        event.getDrops().addAll(event.getNPC().getTrait(NPCTrait.class).getItems());
        //event.getDrops().addAll(Arrays.stream(event.getNPC().getTrait(Equipment.class).getEquipment()).toList());
        String killer;
        if (event.getEvent().getEntity().getKiller() == null)
            killer = "null";
        else killer = event.getEvent().getEntity().getKiller().getName();
        AntiCombatLog.getToKillOnLogin().put(event.getNPC().getTrait(NPCTrait.class).getUuid(),killer);
        AntiCombatLog.saveData();
    }
    @EventHandler
    public void onMove(EntityMoveEvent event){
        if (event.getEntity().hasMetadata("NPC"))
            if (CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).getTrait(NPCTrait.class) != null)
                event.setCancelled(true);
    }
}

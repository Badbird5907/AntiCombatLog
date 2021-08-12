package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.NPCTrait;
import net.citizensnpcs.api.event.NPCDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
}

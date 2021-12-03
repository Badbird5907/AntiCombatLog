package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.citizensnpcs.api.event.NPCDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {
    @EventHandler
    public void onNpcDeath(NPCDeathEvent event){
        event.setDroppedExp((int) event.getNPC().getTrait(CombatNPCTrait.class).getXp());
        event.getDrops().addAll(event.getNPC().getTrait(CombatNPCTrait.class).getItems());
        //event.getDrops().addAll(Arrays.stream(event.getNPC().getTrait(Equipment.class).getEquipment()).toList());
        String killer;
        if (event.getEvent().getEntity().getKiller() == null)
            killer = "null"; //https://discord.com/channels/315163488085475337/315625512753954816/916351371970740226 on citizens support discord
        else killer = event.getEvent().getEntity().getKiller().getName();
        AntiCombatLog.getToKillOnLogin().put(event.getNPC().getTrait(CombatNPCTrait.class).getUuid(),killer);
        AntiCombatLog.saveData();
    }
}

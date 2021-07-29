package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.CombatLogKillEvent;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.object.NPCTrait;
import net.badbird5907.anticombatlog.utils.CC;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class CombatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event){
        if (event.isCancelled())
            return;
        if (event.getEntity().hasMetadata("NPC")) { //is offline npc
            if (!(event.getEntity() instanceof Player))
                return;
            NPCManager.damaged(event.getEntity());
            event.setCancelled(true);
            double damage = event.getDamage();
            LivingEntity le = (LivingEntity) event.getEntity();
            le.damage(damage);
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Player player = (Player) event.getEntity(),damager = (Player) event.getDamager();
            AntiCombatLog.tag(player,damager);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event){
        if (event.getEntity().hasMetadata("NPC") && CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).hasTrait(NPCTrait.class)){
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
            CombatLogKillEvent event1 = new CombatLogKillEvent(CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).getTrait(NPCTrait.class).getUuid(),event);
            Bukkit.getPluginManager().callEvent(event1);
            if (event1.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            event.setDeathMessage(StringUtils.format(ConfigValues.getKillMessage(),npc.getName()));
        }
        if (AntiCombatLog.getKilled().contains(event.getEntity().getUniqueId())){
            AntiCombatLog.getKilled().remove(event.getEntity().getUniqueId());
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setDeathMessage(null);
            String killer = AntiCombatLog.getToKillOnLogin().get(event.getEntity().getUniqueId());
            if (killer == null)
                killer = "null";
            event.getEntity().sendMessage(StringUtils.format(ConfigValues.getLogInAfterKillMessage(),killer));
            AntiCombatLog.getToKillOnLogin().remove(event.getEntity().getUniqueId());
            return;
        }
        AntiCombatLog.getInCombatTag().remove(event.getEntity().getUniqueId());
    }
}

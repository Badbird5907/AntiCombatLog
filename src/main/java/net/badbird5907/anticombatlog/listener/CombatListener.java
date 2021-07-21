package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

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
            AntiCombatLog.tag(player);
            AntiCombatLog.tag(damager);
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (event.getEntity().hasMetadata("NPC")){
        }
        if (AntiCombatLog.getKilled().contains(event.getEntity().getUniqueId())){
            System.out.println("gwebhchiji");
            AntiCombatLog.getKilled().remove(event.getEntity().getUniqueId());
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setDeathMessage(null);
            event.getEntity().sendMessage(StringUtils.format(ConfigValues.getLogInAfterKillMessage(),AntiCombatLog.getToKillOnLogin().get(event.getEntity().getUniqueId())));
            AntiCombatLog.getToKillOnLogin().remove(event.getEntity().getUniqueId());
            return;
        }
        AntiCombatLog.getInCombatTag().remove(event.getEntity().getUniqueId());
    }
}

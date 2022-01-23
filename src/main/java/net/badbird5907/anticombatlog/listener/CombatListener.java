package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.CombatLogKillEvent;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getEntity().hasMetadata("NPC") && NPCManager.getNPCRegistry().getNPC(event.getEntity()).hasTrait(CombatNPCTrait.class)) { //is offline npc
            if (!(event.getEntity() instanceof Player))
                return;
            NPCManager.damaged(event.getEntity());
            event.setCancelled(true);
            double damage = event.getDamage();
            LivingEntity le = (LivingEntity) event.getEntity();
            le.damage(damage);
            return;
        }
        if (event.getEntity() == event.getDamager())
            return;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity(), damager = (Player) event.getDamager();

            AntiCombatLog.tag(player, damager);
            return;
        }
        if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
            try {
                Projectile proj = (Projectile) event.getDamager();
                if (proj.getShooter() instanceof Player) {
                    AntiCombatLog.tag((Player) event.getEntity(), ((Player) proj.getShooter()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Could not combat tag using projectile!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().hasMetadata("NPC") && CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).hasTrait(CombatNPCTrait.class)) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
            CombatLogKillEvent event1 = new CombatLogKillEvent(CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).getTrait(CombatNPCTrait.class).getUuid(), event);
            Bukkit.getPluginManager().callEvent(event1);
            if (event1.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            CombatNPCTrait trait = npc.getTraitNullable(CombatNPCTrait.class);
            String name = trait == null ? "UNKNOWN" : trait.getRawName();
            event.setDeathMessage(StringUtils.format(ConfigValues.getKillMessage(), name));
        }
        if (AntiCombatLog.getKilled().contains(event.getEntity().getUniqueId())) {
            AntiCombatLog.getKilled().remove(event.getEntity().getUniqueId());
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setDeathMessage(null);
            String killer = AntiCombatLog.getToKillOnLogin().get(event.getEntity().getUniqueId());
            event.getEntity().sendMessage(StringUtils.format(ConfigValues.getLogInAfterKillMessage(), killer));
            AntiCombatLog.getToKillOnLogin().remove(event.getEntity().getUniqueId());
            return;
        }
        AntiCombatLog.getInstance().clearCombatTag(event.getEntity());
    }
}

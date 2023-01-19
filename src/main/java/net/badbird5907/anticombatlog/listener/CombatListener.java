package net.badbird5907.anticombatlog.listener;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.CombatLogKillEvent;
import net.badbird5907.anticombatlog.manager.CombatManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.badbird5907.anticombatlog.utils.ConfigUtils;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || event.getFinalDamage() <= 0)
            return;
        if (ConfigUtils.getInstance().getExemptWorlds().contains(event.getEntity().getWorld().getName()))
            return;
        if (event.getEntity().hasMetadata("NPC") && NPCManager.getInstance().getNPCRegistry().getNPC(event.getEntity()).hasTrait(CombatNPCTrait.class)) { //is offline npc
            if (!(event.getEntity() instanceof Player))
                return;
            NPCManager.getInstance().damaged(event.getEntity());
            /*
            event.setCancelled(true);
            double damage = event.getDamage();
            LivingEntity le = (LivingEntity) event.getEntity();
            le.damage(damage);
             */
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
                    if (proj.getShooter() == event.getEntity())
                        return; //player hit himself
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
            String message = StringUtils.format(AntiCombatLog.getInstance().getConfig().getString("messages.kill-message"), name);
            if (AntiCombatLog.getInstance().getConfig().getBoolean("set-death-message", true))
                event.deathMessage(Component.text(message));
            else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(message);
                }
            }
            /*
            String name1 = ( event.getEntity().getKiller() == null ? "null" :  event.getEntity().getKiller().getName());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage("Killer is: " + name1);
            }
             */

            if (event.getEntity().getKiller() != null) {
                AntiCombatLog.getInstance().clearCombatTag(event.getEntity().getKiller());
            }
        }
        if (AntiCombatLog.getKilled().remove(event.getEntity().getUniqueId())) {
            CombatManager.getInstance().getToKillOnLogin().remove(event.getEntity().getUniqueId());
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.deathMessage(null);
            String killer = CombatManager.getInstance().getToKillOnLogin().get(event.getEntity().getUniqueId());
            String s = AntiCombatLog.getInstance().getConfig().getString("messages.log-in-after-kill", "");
            if (s != null)
                event.getEntity().sendMessage(StringUtils.format(s, killer));
            return;
        }
        AntiCombatLog.getInstance().clearCombatTag(event.getEntity());
    }
}

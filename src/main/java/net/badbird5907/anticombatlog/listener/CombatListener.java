package net.badbird5907.anticombatlog.listener;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.api.events.CombatLogKillEvent;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CombatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || event.getFinalDamage() <= 0)
            return;
        if (ConfigValues.getExemptWorlds().contains(event.getEntity().getWorld().getName()))
            return;
        if (AntiCombatLog.isCitizensEnabled() && event.getEntity().hasMetadata("NPC") && NPCManager.getNPCRegistry().getNPC(event.getEntity()).hasTrait(CombatNPCTrait.class)) { //is offline npc
            if (!(event.getEntity() instanceof Player))
                return;
            NPCManager.damaged(event.getEntity());
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
        if (AntiCombatLog.isCitizensEnabled() && event.getEntity().hasMetadata("NPC") && CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).hasTrait(CombatNPCTrait.class)) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
            CombatLogKillEvent event1 = new CombatLogKillEvent(CitizensAPI.getNPCRegistry().getNPC(event.getEntity()).getTraitNullable(CombatNPCTrait.class).getUuid(), event);
            Bukkit.getPluginManager().callEvent(event1);
            if (event1.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            CombatNPCTrait trait = npc.getTraitNullable(CombatNPCTrait.class);
            String name = trait == null ? "UNKNOWN" : trait.getRawName();
            String message = StringUtils.format(ConfigValues.getKillMessage(), name);
            if (ConfigValues.isSetDeathMessage())
                event.setDeathMessage(message);
            else {
                if (message != null) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(message);
                    }
                }
            }
            if (event.getEntity().getKiller() != null) {
                AntiCombatLog.getInstance().clearCombatTag(event.getEntity().getKiller());
            }
        }
        if (AntiCombatLog.getKilled().remove(event.getEntity().getUniqueId())) {
            AntiCombatLog.getToKillOnLogin().remove(event.getEntity().getUniqueId());
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setDeathMessage(null);
            String killer = AntiCombatLog.getToKillOnLogin().get(event.getEntity().getUniqueId());
            String s = ConfigValues.getLogInAfterKillMessage();
            if (s != null)
                event.getEntity().sendMessage(StringUtils.format(s, killer));
            return;
        }
        AntiCombatLog.getInstance().clearCombatTag(event.getEntity());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (ConfigValues.isTagOnPearl()) {
                AntiCombatLog.tag(event.getPlayer(), event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onElytra(PlayerElytraBoostEvent event) {
        if (AntiCombatLog.isCombatTagged(event.getPlayer())) {
            if (AntiCombatLog.getInstance().getConfig().getBoolean("elytra-disable", false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.isGliding() && event.getEntity() instanceof Player && AntiCombatLog.isCombatTagged((Player) event.getEntity())) {
            if (AntiCombatLog.getInstance().getConfig().getBoolean("elytra-disable", false)) {
                event.setCancelled(true);
            }
        }
    }
}

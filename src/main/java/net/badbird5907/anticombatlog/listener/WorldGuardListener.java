package net.badbird5907.anticombatlog.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.raidstone.wgevents.events.RegionLeftEvent;
import net.raidstone.wgevents.events.RegionsLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Set;

import static net.badbird5907.anticombatlog.AntiCombatLog.ALLOW_EXIT_IN_COMBAT;
import static net.badbird5907.anticombatlog.AntiCombatLog.isCombatTagged;

public class WorldGuardListener implements Listener {
    @EventHandler
    public void onRegionsLeft(RegionLeftEvent event)
    {
        Player player = Bukkit.getPlayer(event.getUUID());
        if(player == null) return;


        ProtectedRegion region = event.getRegion();

            if (region.getFlag(ALLOW_EXIT_IN_COMBAT) != null) {
                StateFlag.State f = region.getFlag(ALLOW_EXIT_IN_COMBAT);
                if (f == StateFlag.State.ALLOW) {
                    //player può uscire dalla region
                }else{
                    if(isCombatTagged(player)){
                        //BlockVector3 minPoint = region.getMinimumPoint();
                        //BlockVector3 maxPoint = region.getMaximumPoint();

                        //double centerX = (maxPoint.getX() + minPoint.getX()) / 2.0;
                        //double centerY = (maxPoint.getY() + minPoint.getY()) / 2.0;
                        //double centerZ = (maxPoint.getZ() + minPoint.getZ()) / 2.0;


                        //Location targetLocation = new Location(player.getWorld(), centerX, player.getLocation().getY()+2, centerZ);
                        //Vector centerVector = targetLocation.toVector().subtract(player.getLocation().toVector()).normalize();
                        //Vector knockbackVelocity = centerVector.multiply(0.75);


                        //player.setVelocity(knockbackVelocity);
                        player.sendTitle(StringUtils.format(ConfigValues.getCannotLeaveTitleMessage()), StringUtils.format(ConfigValues.getCannotLeaveSubtitleMessage()), 1, 20, 1);
                        event.setCancelled(true);
                    }
                }
        }
    }
}

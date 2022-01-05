package net.badbird5907.anticombatlog.hooks.impl.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.hooks.IHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class WorldGuardHook implements IHook {
    private static StateFlag ALLOW_PVP_IN_COMBAT;

    @Override
    public void onLoad(AntiCombatLog plugin) {
        registerFlag("allow-pvp-in-combat", false);
    }

    public static StateFlag registerFlag(String name, boolean def) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag(name, def);
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("allow-pvp-in-combat");
            if (existing instanceof StateFlag) {
                return (StateFlag) existing;
            } else {
                Bukkit.getLogger().severe("Could not register WorldGuard flag 'allow-pvp-in-combat', some other plugin has already registered it!");
            }
        }
        return null;
    }

    @Override
    public void enable(AntiCombatLog plugin) {
        Bukkit.getPluginManager().registerEvents(new Listener() { //TODO make a red glass wall instead
            @EventHandler
            public void onPvP(DisallowedPVPEvent event) {
                boolean allowPvP = false;
                if (!AntiCombatLog.isCombatTagged(event.getAttacker())) { //attacker must be tagged
                    return;
                }
                if (!AntiCombatLog.isCombatTagged(event.getDefender())) { //defender must also be tagged
                    return;
                }
                RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(event.getAttacker().getWorld()));
                Location attacker = event.getAttacker().getLocation();
                Set<ProtectedRegion> regionsAttacker = manager.getApplicableRegions(BlockVector3.at(attacker.getX(), attacker.getY(), attacker.getZ())).getRegions();

                for (ProtectedRegion region : regionsAttacker) {
                    if (region.getFlag(ALLOW_PVP_IN_COMBAT) != null) {
                        StateFlag.State f = region.getFlag(ALLOW_PVP_IN_COMBAT);
                        if (f == StateFlag.State.ALLOW) {
                            allowPvP = true;
                        }
                    }
                }
                Location defender = event.getDefender().getLocation();
                Set<ProtectedRegion> regionsDefender = manager.getApplicableRegions(BlockVector3.at(defender.getX(), defender.getY(), defender.getZ())).getRegions();
                for (ProtectedRegion region : regionsDefender) {
                    if (region.getFlag(ALLOW_PVP_IN_COMBAT) != null) {
                        StateFlag.State f = region.getFlag(ALLOW_PVP_IN_COMBAT);
                        if (f == StateFlag.State.ALLOW) {
                            allowPvP = true;
                        }
                    }
                }
                if (!allowPvP) {
                    event.setCancelled(true);
                }
            }
        }, plugin);
    }

    @Override
    public void disable(AntiCombatLog plugin) {

    }

    @Override
    public String getName() {
        return "WorldGuard";
    }
}

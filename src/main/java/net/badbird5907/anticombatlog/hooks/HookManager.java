package net.badbird5907.anticombatlog.hooks;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.hooks.impl.DuelsHook;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class HookManager {
    private static IHook[] allHooks = new IHook[] {
            new DuelsHook()
    };
    private static Set<IHook> hooks = new HashSet<>();

    public static void init() {
        for (IHook hook : allHooks) {
            // check if hooks.name.enabled is true in config
            if (!AntiCombatLog.getInstance().getConfig().getBoolean("hooks." + hook.getConfigKey() + ".enabled")) {
                continue;
            }
            String[] deps = hook.getDependencies();
            boolean found = true;
            for (String dep : deps) {
                if (!Bukkit.getServer().getPluginManager().isPluginEnabled(dep)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                load(hook);
            }
        }
    }

    public static void load(IHook hook) {
        Logger.info(" - Loading hook: " + hook.getName());
        hook.enable(AntiCombatLog.getInstance());
        hooks.add(hook);
        if (hook instanceof Listener) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener) hook, AntiCombatLog.getInstance());
        }
    }

    public static void enable(Class<? extends IHook> hook) {
        for (IHook iHook : hooks) {
            if (iHook.getClass() == hook) {
                enable(iHook);
            }
        }
    }

    public static void enable(IHook hook) {
        Logger.info(" - Enabling hook: " + hook.getName());
        hook.enable(AntiCombatLog.getInstance());
        hooks.add(hook);
    }

    public static void disable(IHook hook) {
        hook.disable(AntiCombatLog.getInstance());
        hooks.add(hook);
    }
    public static void disable(Class<? extends IHook> clazz) {
        for (IHook hook : hooks) {
            if (hook.getClass() == clazz) {
                disable(hook);
            }
        }
    }
    public static void disableAll() {
        for (IHook hook : hooks) {
            disable(hook);
        }
    }
    public static IHook getHook(String name) {
        return hooks.stream().filter(hook -> hook.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public static void enableAllLoaded() {
        for (IHook hook : hooks) {
            enable(hook);
        }
    }
}

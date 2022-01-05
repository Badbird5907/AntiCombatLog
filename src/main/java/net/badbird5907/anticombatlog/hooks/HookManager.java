package net.badbird5907.anticombatlog.hooks;

import net.badbird5907.anticombatlog.AntiCombatLog;

import java.util.HashSet;
import java.util.Set;

public class HookManager {
    private static Set<IHook> hooks = new HashSet<>();
    public static void load(IHook hook) {
        hook.enable(AntiCombatLog.getInstance());
        hooks.add(hook);
    }

    public static void enable(Class<? extends IHook> hook) {
        for (IHook iHook : hooks) {
            if (iHook.getClass() == hook) {
                enable(iHook);
            }
        }
    }

    public static void enable(IHook hook) {
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

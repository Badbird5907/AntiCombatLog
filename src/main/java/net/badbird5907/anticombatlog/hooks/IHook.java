package net.badbird5907.anticombatlog.hooks;

import net.badbird5907.anticombatlog.AntiCombatLog;

public interface IHook {
    default void onLoad(AntiCombatLog plugin) {
    }
    void enable(AntiCombatLog plugin);
    void disable(AntiCombatLog plugin);
    String getName();
    String[] getDependencies();
    String getConfigKey();
}

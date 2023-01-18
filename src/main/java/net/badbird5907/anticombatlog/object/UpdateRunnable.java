package net.badbird5907.anticombatlog.object;

import net.badbird5907.anticombatlog.manager.ActionBarManager;
import net.badbird5907.anticombatlog.manager.CombatManager;
import net.badbird5907.anticombatlog.manager.NPCManager;
import net.badbird5907.anticombatlog.manager.ScoreboardManager;
import net.badbird5907.anticombatlog.utils.ConfigUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        NPCManager.getInstance().update();

        CombatManager.getInstance().update();

        if (ConfigUtils.getInstance().getNotifyType() == NotifyType.BOARD || ConfigUtils.getInstance().getNotifyType() == NotifyType.BOTH)
            ScoreboardManager.update();
        if (ConfigUtils.getInstance().getNotifyType() == NotifyType.ACTIONBAR || ConfigUtils.getInstance().getNotifyType() == NotifyType.BOTH)
            ActionBarManager.getInstance().update();
    }
}

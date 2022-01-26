package net.badbird5907.anticombatlog.commands;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.blib.util.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AntiCombatLogCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("anticombatlog.reload")) {
            long start = System.currentTimeMillis();
            sender.sendMessage(CC.GREEN + "Reloading Config...");
            AntiCombatLog.getInstance().reloadConfig();
            sender.sendMessage(CC.GREEN + "Reloaded Config in " + CC.GOLD + (System.currentTimeMillis() - start) + CC.GREEN + "ms");
            return true;
        }
        sender.sendMessage(CC.AQUA + "Anti-Combat-Log V." + AntiCombatLog.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.AQUA + "By Badbird5907");
        sender.sendMessage(CC.D_AQUA + "To reload config, do: /anticombatlog reload");
        return true;
    }
}

package net.badbird5907.anticombatlog.commands;

import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.blib.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetTagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(CC.RED + "Usage: /resettag <player>");
            return true;
        }
        String target = args[0];
        Player p = Bukkit.getPlayer(target);
        if (p == null) {
            sender.sendMessage(CC.RED + "Could not find that player!");
            return true;
        }
        AntiCombatLog.getInstance().clearCombatTag(p);
        sender.sendMessage(CC.GREEN + "Done! Tag of " + CC.GOLD + p.getName() + CC.GREEN + " has been reset!");
        return true;
    }
}

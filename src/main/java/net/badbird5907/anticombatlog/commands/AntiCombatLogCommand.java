package net.badbird5907.anticombatlog.commands;

import net.badbird5907.anticombatlog.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AntiCombatLogCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(CC.GOLD + "Anti-CombatLog by Badbird5907#5907");
        sender.sendMessage(CC.GREEN + "Command | Description");
        sender.sendMessage(CC.GOLD + "/resetcooldown <player> | reset a player's cooldown");
        return true;
    }
}

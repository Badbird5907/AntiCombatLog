package net.badbird5907.anticombatlog.manager;

import lombok.Getter;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardManager {
    @Getter
    private static List<UUID> scoreboards = new ArrayList<>();
    public static void setScoreBoard(Player player,int i){
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("combatlog","dummy",CC.RED + CC.B + "Combat Tag");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score timer = obj.getScore(CC.GRAY + "Combat Timer");
        timer.setScore(2);
        Team timerCounter = board.registerNewTeam("timerCounter");
        timerCounter.addEntry(ChatColor.GREEN.toString());
        timerCounter.setPrefix(CC.GRAY + CC.ARROW_RIGHT + " " + CC.RED + i + "s.");
        obj.getScore(ChatColor.GREEN.toString()).setScore(1);
        player.setScoreboard(board);
    }
    public static void update(){
        AntiCombatLog.getInCombatTag().forEach((uuid, integer) -> {
            if (Bukkit.getPlayer(uuid) != null){
                if (scoreboards.contains(uuid))
                    updateBoard(Bukkit.getPlayer(uuid),integer);
                else setScoreBoard(Bukkit.getPlayer(uuid),integer);
            }
        });
    }
    public static void updateBoard(Player player,int i){
        Scoreboard board = player.getScoreboard();
        if (board == null){
            setScoreBoard(player, i);
            return;
        }
        board.getTeam("timerCounter").setPrefix(CC.GRAY + CC.ARROW_RIGHT + " " + CC.RED + i + "s.");
    }
}

package net.badbird5907.anticombatlog.api.events;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.event.SimpleEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.UUID;

@Getter
@Setter
public class CombatLogNPCSpawnEvent extends SimpleEvent implements Cancellable {
    private Player player;
    private int time;
    private boolean indefinite,cancelled = false;

    public CombatLogNPCSpawnEvent(Player player, int time, boolean indefinite) {
        this.player = player;
        this.time = time;
        this.indefinite = indefinite;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}

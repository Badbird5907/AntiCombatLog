package net.badbird5907.anticombatlog.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.event.SimpleEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CombatTagEvent extends SimpleEvent implements Cancellable {
    private final Player victim;
    private final Player attacker;
    boolean cancelled;

    public CombatTagEvent(Player victim) {
        attacker = null;
        this.victim = victim;
    }
    public CombatTagEvent(Player victim, Player attacker) {
        this.victim = victim;
        this.attacker = attacker;
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

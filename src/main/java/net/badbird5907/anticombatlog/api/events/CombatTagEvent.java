package net.badbird5907.anticombatlog.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class CombatTagEvent extends Event implements Cancellable {
    boolean cancelled;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    private final Player victim;
    private final Player attacker;


    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}

package net.badbird5907.anticombatlog.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class CombatLogKillEvent extends Event implements Cancellable {
    private boolean cancelled = false;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    private final UUID player;
    private final PlayerDeathEvent actualDeathEvent;

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}

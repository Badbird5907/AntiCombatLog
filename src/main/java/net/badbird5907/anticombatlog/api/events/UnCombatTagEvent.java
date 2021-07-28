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
@Setter
public class UnCombatTagEvent extends Event {
    private final Player player;
    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}

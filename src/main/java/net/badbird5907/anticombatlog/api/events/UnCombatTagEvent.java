package net.badbird5907.anticombatlog.api.events;

import lombok.*;
import net.badbird5907.blib.event.SimpleEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnCombatTagEvent extends SimpleEvent {
    private final Player player; //TODO keep track of last combat tagged
}
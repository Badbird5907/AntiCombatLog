package net.badbird5907.anticombatlog.object;

import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class NPCTrait extends Trait {
    @Getter
    private final float xp;
    @Getter
    private final UUID uuid;
    @Getter
    private final List<ItemStack> items;
    public NPCTrait(String name, float xp, UUID uuid, List<ItemStack> items) {
        super(name);
        this.xp = xp;
        this.uuid = uuid;
        this.items = items;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        getNPC().setProtected(false);
        getNPC().getEntity().setInvulnerable(false);
    }

    @Override
    public void onDespawn() {
        super.onDespawn();
    }

}

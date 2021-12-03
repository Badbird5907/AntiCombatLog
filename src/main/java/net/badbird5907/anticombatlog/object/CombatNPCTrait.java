package net.badbird5907.anticombatlog.object;

import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class CombatNPCTrait extends Trait {
    @Getter
    private final float xp;
    @Getter
    private final UUID uuid;
    @Getter
    private final List<ItemStack> items;
    @Getter
    private final double health;
    public CombatNPCTrait(String name, float xp, UUID uuid, List<ItemStack> items, double health) {
        super(name);
        this.xp = xp;
        this.uuid = uuid;
        this.items = items;
        this.health = health;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        getNPC().setProtected(false);
        getNPC().getEntity().setInvulnerable(false);
        LivingEntity le = (LivingEntity) getNPC().getEntity();
        le.setHealth(health);
        le.setInvulnerable(false);
        le.setNoDamageTicks(0);
        le.setMaximumNoDamageTicks(0);
    }

    @Override
    public void onDespawn() {
        super.onDespawn();
    }

}

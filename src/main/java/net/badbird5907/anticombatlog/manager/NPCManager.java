package net.badbird5907.anticombatlog.manager;

import lombok.Getter;
import net.badbird5907.anticombatlog.object.CombatNPCTrait;
import net.badbird5907.anticombatlog.object.HoloTrait;
import net.badbird5907.anticombatlog.object.Triplet;
import net.badbird5907.anticombatlog.utils.ConfigValues;
import net.badbird5907.blib.util.CC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NPCManager {
    @Getter
    private static final Map<UUID, Triplet<Integer, NPC, String>> npcs = new ConcurrentHashMap<>();

    public static void update() {
        npcs.forEach((uuid, triplet) -> {
            triplet.setValue0(triplet.getValue0() - 1);
            if (!triplet.getValue1().isSpawned() || triplet.getValue0() <= 0) {
                if (triplet.getValue1().isSpawned())
                    triplet.getValue1().despawn();
                triplet.getValue1().destroy();
                npcs.remove(uuid);
                return;
            }
            HoloTrait holoTrait = triplet.getValue1().getTraitNullable(HoloTrait.class);
            if (holoTrait != null) {
                if (holoTrait.getLines().size() == 0)
                    holoTrait.addLine(CC.YELLOW + CC.B + triplet.getValue0() + " seconds left");
                else holoTrait.setLine(0, CC.YELLOW + CC.B + triplet.getValue0() + " seconds left");
            }
        });
    }

    public static void spawn(Player player, int i) {
        NPC npc;
        if (ConfigValues.isShowPlayerNameOnly()) {
            npc = getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
        }else {
            npc = getNPCRegistry().createNPC(EntityType.PLAYER, CC.RED + CC.B + "DISCONNECTED: " + CC.R + CC.RED + player.getName());
        }

        npc.addTrait(new CombatNPCTrait(player.getName(), player.getExp(), player.getUniqueId(), Arrays.asList(player.getInventory().getContents()), player.getHealth()));
        //npc.getTrait(HologramTrait.class).addLine(CC.YELLOW + CC.B + i + " seconds left");
        if (ConfigValues.isEnableHologram())
            npc.addTrait(new HoloTrait(player.getLocation()));
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, player.getInventory().getBoots());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, player.getInventory().getLeggings());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, player.getInventory().getChestplate());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, player.getInventory().getHelmet());
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, player.getInventory().getItemInMainHand()); //TODO multi version
        npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, player.getInventory().getItemInOffHand());
        npc.getTrait(Inventory.class).setContents(player.getInventory().getContents());
        if (Bukkit.getPluginManager().isPluginEnabled("Floodgate")) {
            if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) //to prevent problems with floodgate/geyser players
                npc.getOrAddTrait(SkinTrait.class).setSkinName(player.getName());
        } else {
            npc.getOrAddTrait(SkinTrait.class).setSkinName(player.getName());
        }
        npcs.put(player.getUniqueId(), new Triplet<>(i, npc, player.getName()));
        npc.spawn(player.getLocation());
    }

    public static boolean isSpawned(UUID player) {
        return npcs.containsKey(player);
    }

    public static void despawn(UUID player) {
        if (isSpawned(player)) {
            NPC npc = npcs.get(player).getValue1();
            if (npc.isSpawned()) {
                npc.despawn();
            }
            npc.destroy();
        }
    }

    public static double getHealth(UUID player) {
        if (isSpawned(player)) {
            NPC npc = npcs.get(player).getValue1();
            if (npc.isSpawned()) {
                LivingEntity e = (LivingEntity) npc.getEntity();
                return e.getHealth();
            }
        }
        return 20.0;
    }

    public static void damaged(Entity entity) {
        if (ConfigValues.getNpcHitResetSecond() == -1)
            return;
        npcs.values().forEach(triplet -> {
            if (entity.getUniqueId() == triplet.getValue1().getEntity().getUniqueId()) {
                triplet.setValue0(ConfigValues.getNpcHitResetSecond());
            }
        });
    }

    public static NPCRegistry getNPCRegistry() {
        if (CitizensAPI.getNamedNPCRegistry("AntiCombatLog") == null)
            CitizensAPI.createNamedNPCRegistry("AntiCombatLog", new MemoryNPCDataStore());
        return CitizensAPI.getNamedNPCRegistry("AntiCombatLog");
    }
}

package net.badbird5907.anticombatlog.object;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.citizensnpcs.Settings.Setting;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.Colorizer;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.api.util.Placeholders;
import net.citizensnpcs.trait.ArmorStandTrait;
import net.citizensnpcs.trait.ClickRedirectTrait;
import net.citizensnpcs.util.NMS;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@TraitName("hologramtrait")
public class HoloTrait extends Trait {
    private Location currentLoc;
    @Persist
    private net.citizensnpcs.trait.HologramTrait.HologramDirection direction;
    private final List<NPC> hologramNPCs;
    @Persist
    private double lineHeight;
    @Persist
    private final List<String> lines;
    private NPC nameNPC;
    private final NPCRegistry registry;

    public HoloTrait(Location loc) {
        super("hologramtrait");
        this.currentLoc = loc;
        this.direction = net.citizensnpcs.trait.HologramTrait.HologramDirection.BOTTOM_UP;
        this.hologramNPCs = Lists.newArrayList();
        this.lineHeight = -1.0D;
        this.lines = Lists.newArrayList();
        this.registry = CitizensAPI.createCitizensBackedNPCRegistry(new MemoryNPCDataStore());
    }

    public void addLine(String text) {
        this.lines.add(text);
        this.onDespawn();
        this.onSpawn();
    }

    public void clear() {
        this.onDespawn();
        this.lines.clear();
    }

    private NPC createHologram(String line, double heightOffset) {
        NPC hologramNPC = this.registry.createNPC(EntityType.ARMOR_STAND, line);
        hologramNPC.addTrait(new ClickRedirectTrait(this.npc));
        ArmorStandTrait trait = (ArmorStandTrait)hologramNPC.getOrAddTrait(ArmorStandTrait.class);
        trait.setVisible(false);
        trait.setSmall(true);
        trait.setMarker(true);
        trait.setGravity(false);
        trait.setHasArms(false);
        trait.setHasBaseplate(false);
        hologramNPC.spawn(this.currentLoc.clone().add(0.0D, this.getEntityHeight() + (this.direction == net.citizensnpcs.trait.HologramTrait.HologramDirection.BOTTOM_UP ? heightOffset : this.getMaxHeight() - heightOffset), 0.0D));
        return hologramNPC;
    }

    public net.citizensnpcs.trait.HologramTrait.HologramDirection getDirection() {
        return this.direction;
    }

    private double getEntityHeight() {
        return NMS.getHeight(this.npc.getEntity());
    }

    private double getHeight(int lineNumber) {
        return (this.lineHeight == -1.0D ? Setting.DEFAULT_NPC_HOLOGRAM_LINE_HEIGHT.asDouble() : this.lineHeight) * (double)(lineNumber + 1);
    }

    public Collection<ArmorStand> getHologramEntities() {
        return Collections2.transform(this.hologramNPCs, (n) -> {
            return (ArmorStand)n.getEntity();
        });
    }

    public double getLineHeight() {
        return this.lineHeight;
    }

    public List<String> getLines() {
        return this.lines;
    }

    private double getMaxHeight() {
        return (this.lineHeight == -1.0D ? Setting.DEFAULT_NPC_HOLOGRAM_LINE_HEIGHT.asDouble() : this.lineHeight) * (double)(this.lines.size() + (this.npc.requiresNameHologram() ? 0 : 1));
    }

    public ArmorStand getNameEntity() {
        return this.nameNPC != null && this.nameNPC.isSpawned() ? (ArmorStand)this.npc.getEntity() : null;
    }

    public void onDespawn() {
        if (this.nameNPC != null) {
            this.nameNPC.destroy();
            this.nameNPC = null;
        }

        Iterator var1 = this.hologramNPCs.iterator();

        while(var1.hasNext()) {
            NPC npc = (NPC)var1.next();
            npc.destroy();
        }

        this.hologramNPCs.clear();
    }

    public void onRemove() {
        this.onDespawn();
    }

    public void onSpawn() {
        //this.currentLoc = this.npc.getStoredLocation();
        if (this.npc.requiresNameHologram() && Boolean.parseBoolean(this.npc.data().get("nameplate-visible", true).toString())) {
            this.nameNPC = this.createHologram(this.npc.getFullName(), 0.0D);
        }

        for(int i = 0; i < this.lines.size(); ++i) {
            String line = (String)this.lines.get(i);
            this.hologramNPCs.add(this.createHologram(Placeholders.replace(line, (CommandSender)null, this.npc), this.getHeight(i)));
        }

    }

    public void removeLine(int idx) {
        this.lines.remove(idx);
        this.onDespawn();
        this.onSpawn();
    }

    public void run() {
        if (!this.npc.isSpawned()) {
            this.onDespawn();
        } else {
            boolean update;
            if (this.npc.requiresNameHologram()) {
                update = Boolean.parseBoolean(this.npc.data().get("nameplate-visible", true).toString());
                if (this.nameNPC != null && !update) {
                    this.nameNPC.destroy();
                    this.nameNPC = null;
                } else if (this.nameNPC == null && update) {
                    this.nameNPC = this.createHologram(this.npc.getFullName(), 0.0D);
                }
            }

            update = this.currentLoc.getWorld() != this.npc.getStoredLocation().getWorld() || this.currentLoc.distanceSquared(this.npc.getStoredLocation()) >= 0.01D;
            if (update) {
                this.currentLoc = this.npc.getStoredLocation();
            }

            if (this.nameNPC != null && this.nameNPC.isSpawned()) {
                if (update) {
                    this.nameNPC.teleport(this.currentLoc.clone().add(0.0D, this.getEntityHeight(), 0.0D), TeleportCause.PLUGIN);
                }

                this.nameNPC.setName(this.npc.getFullName());
            }

            for(int i = 0; i < this.hologramNPCs.size(); ++i) {
                NPC hologramNPC = (NPC)this.hologramNPCs.get(i);
                if (hologramNPC.isSpawned()) {
                    if (update) {
                        hologramNPC.teleport(this.currentLoc.clone().add(0.0D, this.getEntityHeight() + this.getHeight(i), 0.0D), TeleportCause.PLUGIN);
                    }

                    if (i >= this.lines.size()) {
                        Messaging.severe(new Object[]{"More hologram NPCs than lines for ID", this.npc.getId(), "lines", this.lines});
                        break;
                    }

                    String text = (String)this.lines.get(i);
                    if (text != null && !ChatColor.stripColor(Colorizer.parseColors(text)).isEmpty()) {
                        hologramNPC.setName(Placeholders.replace(text, (CommandSender)null, this.npc));
                        hologramNPC.data().set("nameplate-visible", true);
                    } else {
                        hologramNPC.setName("");
                        hologramNPC.data().set("nameplate-visible", false);
                    }
                }
            }

        }
    }

    public void setDirection(net.citizensnpcs.trait.HologramTrait.HologramDirection direction) {
        this.direction = direction;
        this.onDespawn();
        this.onSpawn();
    }

    public void setLine(int idx, String text) {
        if (idx == this.lines.size()) {
            this.lines.add(text);
        } else {
            this.lines.set(idx, text);
        }

        this.onDespawn();
        this.onSpawn();
    }

    public void setLineHeight(double height) {
        this.lineHeight = height;
        this.onDespawn();
        this.onSpawn();
    }

    public static enum HologramDirection {
        BOTTOM_UP,
        TOP_DOWN;

        private HologramDirection() {
        }
    }
}

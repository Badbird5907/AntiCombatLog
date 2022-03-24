package net.badbird5907.anticombatlog.utils;

import net.badbird5907.blib.objects.maps.pair.PairMap;

import java.util.*;

public class UUIDUtil {
    public static boolean contains(Collection<UUID> col, UUID uuid) {
        for (UUID u : col) {
            if (u.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(Set<? extends Map.Entry<UUID, ?>> entries, UUID uuid) {
        for (Map.Entry<UUID, ?> entry : entries) {
            if (entry.getKey().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(Map<UUID, ?> map, UUID uuid) {
        return contains(map.entrySet(), uuid);
    }

    public static boolean contains(PairMap<UUID, ?, ?> map, UUID uuid) {
        for (UUID uuid1 : map.keySet()) {
            if (uuid1.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(UUID uuid1, UUID uuid2) {
        return uuid1.equals(uuid2);
    }

    public static boolean remove(Collection<UUID> col, UUID uuid) {
        Iterator<UUID> it = col.iterator();
        while (it.hasNext()) {
            UUID u = it.next();
            if (u.equals(uuid)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static boolean remove(Map<UUID, ?> map, UUID uuid) {
        Iterator<? extends Map.Entry<UUID, ?>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, ?> entry = it.next();
            if (entry.getKey().equals(uuid)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static boolean remove(PairMap<UUID, ?, ?> map, UUID uuid) {
        Iterator<UUID> it = map.keySet().iterator();
        while (it.hasNext()) {
            UUID uuid1 = it.next();
            if (uuid1.equals(uuid)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}

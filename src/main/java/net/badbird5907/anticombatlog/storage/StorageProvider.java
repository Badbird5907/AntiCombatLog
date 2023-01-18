package net.badbird5907.anticombatlog.storage;

import java.util.Map;
import java.util.UUID;

public interface StorageProvider {
    void init();
    Map<UUID, String> getToKillOnLogin();

    void saveToKillOnLogin(Map<UUID, String> toKillOnLogin);

}

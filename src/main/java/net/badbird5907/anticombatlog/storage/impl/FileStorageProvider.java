package net.badbird5907.anticombatlog.storage.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import net.badbird5907.anticombatlog.AntiCombatLog;
import net.badbird5907.anticombatlog.storage.StorageProvider;
import net.badbird5907.anticombatlog.utils.StringUtils;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileStorageProvider implements StorageProvider {
    private File file;

    @SneakyThrows
    @Override
    public void init() {
        file = new File(AntiCombatLog.getInstance().getDataFolder() + "/data.json");
        if (!file.exists()) {
            file.createNewFile();
            PrintStream ps = new PrintStream(file);
            ps.print("{}");
            ps.close();
        }
    }

    private static final Type MAP_TYPE = new TypeToken<Map<UUID, String>>() {
    }.getType();

    @Override
    public Map<UUID, String> getToKillOnLogin() {
        /*
        String json = StringUtils.readFile(file);
        toKillOnLogin = new HashMap<>();
        Map<String, String> a = new Gson().fromJson(json, HashMap.class); //should fix random classcast exception
        a.forEach((a1, b) -> toKillOnLogin.put(UUID.fromString(a1), b));
         */
        String json = StringUtils.readFile(file);
        return AntiCombatLog.getInstance().getGson().fromJson(json, MAP_TYPE);
    }

    @Override
    public void saveToKillOnLogin(Map<UUID, String> toKillOnLogin) {
        Tasks.runAsync(()-> {
            String json = new Gson().toJson(toKillOnLogin);
            try {
                PrintStream ps = new PrintStream(file);
                ps.print(json);
                ps.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}

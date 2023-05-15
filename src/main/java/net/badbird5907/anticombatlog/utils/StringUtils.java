package net.badbird5907.anticombatlog.utils;

import lombok.experimental.UtilityClass;
import net.badbird5907.blib.util.CC;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

@UtilityClass
public class StringUtils {
    public static String format(String in, String... placeholders) {
        if (in == null || in.isEmpty())
            return null;
        String a = replacePlaceholders(in, placeholders);
        return CC.translate(a);
    }
    public static String replacePlaceholders(String str, Object... replace) {
        if (replace != null && replace.length != 0) {
            int i = 0;
            String finalReturn = str;
            if (replace != null && replace.length != 0) {
                Object[] var4 = replace;
                int var5 = replace.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    Object s = var4[var6];
                    if (s == null) {
                        continue;
                    }
                    ++i;
                    String toReplace = "%" + i;
                    finalReturn = finalReturn.replace(toReplace, s.toString());
                }

                return finalReturn;
            } else {
                return str;
            }
        } else {
            return str;
        }
    }

    public static String readFile(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}

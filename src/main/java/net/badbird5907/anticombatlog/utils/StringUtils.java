package net.badbird5907.anticombatlog.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

@UtilityClass
public class StringUtils {
    public static String replacePlaceholders(final String str, final String... replace) {
        if (replace == null)
            return str;
        int i = 0;
        String finalReturn = str;
        for (String s : replace) {
            i++;
            String toReplace = "%" + i;
            finalReturn = finalReturn.replace(toReplace, s);
        }
        return finalReturn;
    }

    public static String format(String in, String... placeholders) {
        String a = replacePlaceholders(in, placeholders);
        return CC.translate(a);
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

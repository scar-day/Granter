package dev.scarday.granter.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtil {
    private final Pattern HEX_PATTERN = Pattern.compile("#([a-fA-F\\d]{6})");
    private final char COLOR_CHAR = '\u00a7';

    public String colorize(@NotNull String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(builder,
                    COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR
                            + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4)
                            + COLOR_CHAR + group.charAt(5));
        }
        message = matcher.appendTail(builder).toString();

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

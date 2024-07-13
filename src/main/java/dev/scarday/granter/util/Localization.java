package dev.scarday.granter.util;

import dev.scarday.granter.Granter;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class Localization {

    public String getString(String key) {
        return getString(key, key);
    }

    public String getString(String key, String def) {
        String message = Granter.getPlugin().getConfig().getString(key, def);

        return ColorUtil.colorize(message);
    }

    public String getStringList(String key) {
        List<String> list = Granter.getPlugin().getConfig().getStringList(key);

        List<String> coloredList = list.stream()
                .map(ColorUtil::colorize)
                .collect(Collectors.toList());

        return String.join("\n", coloredList);
    }

}

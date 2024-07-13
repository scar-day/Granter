package dev.scarday.granter.util;

import dev.scarday.granter.Granter;
import io.github.slimjar.logging.ProcessLogger;

import java.text.MessageFormat;

public class SlimJarLogger implements ProcessLogger {
    private final Granter plugin;

    public SlimJarLogger(Granter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void log(String message, Object... args) {
        plugin.getLogger().info(MessageFormat.format(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        ProcessLogger.super.debug(message, args);
    }
}
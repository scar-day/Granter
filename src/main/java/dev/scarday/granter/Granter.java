package dev.scarday.granter;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.scarday.granter.command.GrantCommand;
import dev.scarday.granter.database.Database;
import dev.scarday.granter.handler.MissingPermissionHandler;
import dev.scarday.granter.util.Localization;
import dev.scarday.granter.util.SlimJarLogger;
import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.resolver.data.Repository;
import io.github.slimjar.resolver.mirrors.SimpleMirrorSelector;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collections;


@Getter
public final class Granter extends JavaPlugin {

    @Getter
    private static Granter plugin;

    @Getter
    private static Database database;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onLoad() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            Path downloadPath = Paths.get(getDataFolder().getPath() + File.separator + "libs");
            ApplicationBuilder.appending("Granter")
                    .logger(new SlimJarLogger(this))
                    .downloadDirectoryPath(downloadPath)
                    .mirrorSelector((a, b) -> a)
                    .internalRepositories(Collections.singleton(new Repository(new URL(SimpleMirrorSelector.ALT_CENTRAL_URL))))
                    .build();
        } catch (IOException | ReflectiveOperationException | URISyntaxException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        String databaseType = getConfig().getString("database.type", "H2");

        if (databaseType.equalsIgnoreCase("h2")) {
            String type = getConfig().getString("database.path", "{pluginFolder}/database.db")
                            .replace("{pluginFolder}", getDataFolder().getAbsolutePath());

            try {
                database = new Database(type);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (databaseType.equalsIgnoreCase("mysql")) {
            String url = getConfig().getString("database.url");
            String username = getConfig().getString("database.username");
            String password = getConfig().getString("database.password");

            try {
                database = new Database(url, username, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        this.liteCommands = LiteBukkitFactory.builder("granter", this)
                .commands(new GrantCommand())
                .message(LiteBukkitMessages.INVALID_USAGE, Localization.getStringList("messages.help_response"))
                .message(LiteBukkitMessages.PLAYER_ONLY, Localization.getString("messages.player_only"))
                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, Localization.getString("messages.no_found_player"))
                .missingPermission(new MissingPermissionHandler())
                .build();

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        if (liteCommands != null) {
            liteCommands.unregister();
        }
    }

}

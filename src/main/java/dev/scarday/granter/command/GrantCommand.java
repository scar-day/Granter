package dev.scarday.granter.command;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.scarday.granter.Granter;
import dev.scarday.granter.util.ColorUtil;
import dev.scarday.granter.util.Localization;
import dev.scarday.granter.util.LuckPermsUtil;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

@Command(name = "grant")
@Permission("granter.use")
public class GrantCommand {
    @SneakyThrows
    @Execute(name = "give")
    public void executeGiveCommand(
            @Context CommandSender commandSender,
            @OptionalArg Player player,
            @OptionalArg String groupName
    ) {
        Player sender = (Player) commandSender;
        User user = LuckPermsUtil.getUser(sender.getName());

        if (!Granter.getPlugin().getConfig().contains("groups." + user.getPrimaryGroup())) {
            commandSender.sendMessage(Localization.getString("messages.no_gived_donate"));
            return;
        }

        if (player == null) {
            commandSender.sendMessage("Игрок не найден!");
            return;
        }

        if (groupName == null || groupName.isEmpty()) {
            commandSender.sendMessage(Localization.getString("messages.no_entry_group"));
        }

        if (LuckPermsUtil.hasGroup(groupName)) {
            commandSender.sendMessage(Localization.getString("messages.no_found_group"));
            return;
        }

        if (!Granter.getPlugin().getConfig().contains("groups." + user.getPrimaryGroup() + "." + groupName)) {
            commandSender.sendMessage(Localization.getString("messages.no_gived_this_group"));
            return;
        }

        long gived = Granter.getDatabase().countGrantEntities(player, groupName);
        long limit = Granter.getPlugin().getConfig().getLong("groups." + user.getPrimaryGroup() + "." + groupName);

        long result = limit - gived;

        if (result <= 0) {
            commandSender.sendMessage(Localization.getString("messages.limit_gived_group"));
            return;
        }

        Group group = LuckPermsUtil.getGroup(groupName);

        Granter.getDatabase().givePlayer(player, groupName);

        for (String command : Granter.getPlugin().getConfig().getStringList("commands")) {
            command = command
                    .replace("{group}", group.getName())
                    .replace("{player}", player.getName());

            Bukkit.dispatchCommand(Granter.getPlugin().getServer().getConsoleSender(), command);
        }

        boolean isEnable = Granter.getPlugin().getConfig().getBoolean("notify.enable");

        if (isEnable) {
            for (Player p : Granter.getPlugin().getServer().getOnlinePlayers()) {

                boolean isEnableActionBar = Granter.getPlugin().getConfig().getBoolean("notify.actionbar.enable", false);
                boolean isEnableSound = Granter.getPlugin().getConfig().getBoolean("notify.sound.enable", false);

                String metaPrefix = group.getCachedData().getMetaData().getPrefix();

                String prefix = metaPrefix != null ?
                        ColorUtil.colorize(group.getCachedData().getMetaData().getPrefix()) : group.getDisplayName();

                    String message = Localization.getStringList("notify.message")
                                    .replace("{player}", commandSender.getName())
                                    .replace("{gived_player}", player.getName())
                                    .replace("{group}", prefix);

                    p.sendMessage(message);
                if (isEnableActionBar) {
                    String actionText = Localization.getString("notify.actionbar.message")
                            .replace("{player}", commandSender.getName())
                            .replace("{gived_player}", player.getName());

                    p.sendActionBar(Component.text(actionText));
                }

                if (isEnableSound) {
                    Sound sound = Sound.valueOf(Localization.getString("messages.sound.sound", "ENTITY_PLAYER_LEVELUP"));
                    p.playSound(p.getLocation(), sound, 1, 1);
                }
            }
        }

    }

    @SneakyThrows
    @Execute(name = "list")
    public void executeListCommand(@Context CommandSender commandSender) {
        Player player = (Player) commandSender;
        User user = LuckPermsUtil.getUser(player.getName());

        if (!Granter.getPlugin().getConfig().contains("groups." + user.getPrimaryGroup())) {
            commandSender.sendMessage(Localization.getString("messages.no_gived_donate"));
            return;
        }

        ConfigurationSection primaryGroupSection = Granter.getPlugin().getConfig()
                .getConfigurationSection("groups." +user.getPrimaryGroup());

        if (primaryGroupSection != null) {
            Map<String, Long> countGroups = listGroups(player, primaryGroupSection);

            commandSender.sendMessage(Localization.getString("messages.list_response")
                    .replace("{groups_count}", String.valueOf(countGroups.size())));

            if (countGroups.isEmpty()) {
                commandSender.sendMessage(Localization.getString("messages.no_groups"));
                return;
            }

            for (Map.Entry<String, Long> entry : countGroups.entrySet()) {
                String groupName = entry.getKey();
                Long count = entry.getValue();

                Group group = LuckPermsUtil.getGroup(groupName);

                String prefix = group.getCachedData().getMetaData().getPrefix() != null ?
                        ColorUtil.colorize(group.getCachedData().getMetaData().getPrefix()) : groupName;

                String message = Localization.getString("messages.list_format")
                        .replace("{group}", groupName)
                        .replace("{prefix}", prefix)
                        .replace("{count}", String.valueOf(count));

                commandSender.sendMessage(message);
            }
        }
    }

    @Async
    @Execute(name="reload")
    public void executeReloadCommand(@Context CommandSender commandSender) {
        Granter.getPlugin().reloadConfig();

        commandSender.sendMessage(Localization.getString("messages.reload_config"));
    }

    @SneakyThrows
    private Map<String, Long> listGroups(Player player, ConfigurationSection primaryGroupSection) {
        Map<String, Long> groups = new HashMap<>();
        for (String groupName : primaryGroupSection.getKeys(false)) {
            if (LuckPermsUtil.hasGroup(groupName)) continue;
            if (primaryGroupSection.getString(groupName) != null) {
                User user = LuckPermsUtil.getUser(player.getName());

                long gived = Granter.getDatabase().countGrantEntities(player, groupName);
                long limit = Granter.getPlugin().getConfig().getLong("groups." + user.getPrimaryGroup() + "." + groupName);

                long result = limit - gived;

                if (result <= 0) continue;

                groups.put(groupName, result);
            }
        }
        return groups;
    }



}

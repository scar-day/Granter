package dev.scarday.granter.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import dev.scarday.granter.util.Localization;
import org.bukkit.command.CommandSender;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> resultHandlerChain) {
        String permission = missingPermissions.asJoinedText();
        CommandSender sender = invocation.sender();

        sender.sendMessage(Localization.getString("messages.no-permission").replace("{permission}", permission));
    }
}

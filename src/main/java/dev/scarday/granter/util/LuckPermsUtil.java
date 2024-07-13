package dev.scarday.granter.util;

import lombok.experimental.UtilityClass;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

@UtilityClass
public class LuckPermsUtil {
    LuckPerms api = LuckPermsProvider.get();

    public Group getGroup(String groupName) {
        return api.getGroupManager().getGroup(groupName);
    }

    public User getUser(String userName) {
        return api.getUserManager().getUser(userName);
    }

    public boolean hasGroup(String groupName) {
        return api.getGroupManager().getGroup(groupName) == null;
    }
}

package org.telegram.android.core.engines;

import org.telegram.android.core.EngineUtils;
import org.telegram.android.core.model.Group;
import org.telegram.android.core.model.media.TLAbsLocalAvatarPhoto;
import org.telegram.android.core.model.media.TLLocalAvatarEmpty;
import org.telegram.api.*;

import java.util.List;

/**
 * Created by ex3ndr on 04.01.14.
 */
public class GroupsEngine {
    private GroupsDatabase database;

    public GroupsEngine(ModelEngine engine) {
        this.database = new GroupsDatabase(engine);
    }

    public Group getGroup(int groupId) {
        return database.getGroup(groupId);
    }

    public Group[] getGroups(int[] ids) {
        return database.getGroups(ids);
    }

    public Group[] getGroups(Integer[] ids) {
        return database.getGroups(ids);
    }

    public void onGroupsUpdated(Group... chats) {
        database.updateGroups(chats);
    }

    public void onGroupsUpdated(List<TLAbsChat> chats) {
        Group[] groups = new Group[chats.size()];
        for (int i = 0; i < groups.length; i++) {
            TLAbsChat chat = chats.get(i);
            groups[i] = new Group();
            groups[i].setChatId(chat.getId());
            if (chat instanceof TLChat) {
                groups[i].setTitle(((TLChat) chat).getTitle());
                groups[i].setAvatar(EngineUtils.convertAvatarPhoto(((TLChat) chat).getPhoto()));
                groups[i].setForbidden(((TLChat) chat).getLeft());
                groups[i].setUsersCount(((TLChat) chat).getParticipantsCount());
            } else if (chat instanceof TLChatEmpty) {
                groups[i].setTitle("Unknown");
                groups[i].setAvatar(new TLLocalAvatarEmpty());
                groups[i].setUsersCount(0);
                groups[i].setForbidden(true);
            } else if (chat instanceof TLChatForbidden) {
                groups[i].setTitle(((TLChatForbidden) chat).getTitle());
                groups[i].setAvatar(new TLLocalAvatarEmpty());
                groups[i].setUsersCount(0);
                groups[i].setForbidden(true);
            } else if (chat instanceof TLGeoChat) {
                groups[i].setTitle(((TLGeoChat) chat).getTitle());
                groups[i].setAvatar(EngineUtils.convertAvatarPhoto(((TLGeoChat) chat).getPhoto()));
                groups[i].setUsersCount(0);
                groups[i].setForbidden(true);
            } else {
                throw new RuntimeException("Unknown chat type");
            }
        }
        database.updateGroups(groups);
    }

    public void onGroupNameChanged(int id, String title) {
        Group group = getGroup(id);
        if (group != null) {
            group.setTitle(title);
            database.updateGroups(group);
        }
    }

    public void onGroupAvatarChanged(int id, TLAbsLocalAvatarPhoto avatar) {
        Group group = getGroup(id);
        if (group != null) {
            group.setAvatar(avatar);
            database.updateGroups(group);
        }
    }

    public void deleteGroup(int id) {
        database.deleteGroup(id);
    }

    public void clear() {
        database.clear();
    }
}

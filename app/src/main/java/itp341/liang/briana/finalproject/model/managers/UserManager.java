package itp341.liang.briana.finalproject.model.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

import itp341.liang.briana.finalproject.model.objects.UserInfo;

/**
 * User Manager class
 */

public class UserManager {
    private static final String USER_GROUP_IDENTIFIER = "Users";
    private static UserManager defaultManager = new UserManager();


    public static @NonNull
    UserManager getDefaultManager() {
        return defaultManager;
    }

    private StorageManager storageManager = StorageManager.getDefaultManager();

    /*
    * Constructor for UserInfo Manager
    * */
    private UserManager(){

    }
    /*
     * setUserInfo method, takes in a UserInfo object
     * */
    public void setUserInfo(@NonNull UserInfo userInfo)
    {
        if (userInfo == null) {
            return;
        }

        storageManager.setObject(userInfo, USER_GROUP_IDENTIFIER, userInfo.getIdentifier());
    }


    /**
     * Remove a UserInfo object.
     * @param UserInfo The UserInfo object to remove.
     */
    public void removeUserInfo(@Nullable UserInfo UserInfo) {
        if (UserInfo == null) {
            return;
        }

        this.removeUserInfoWithIdentifier(UserInfo.getIdentifier());
    }

    /**
     * Remove a UserInfo given its identifier.
     * @param UserInfoIdentifier The string identifier of the UserInfo to remove.
     */
    public void removeUserInfoWithIdentifier(@NonNull String UserInfoIdentifier)
    {
        if (UserInfoIdentifier == null) {
            return;
        }

        UserInfo removedUserInfo = this.getUserInfoWithIdentifier(UserInfoIdentifier);

        if (removedUserInfo != null) {
            this.storageManager.removeObject(USER_GROUP_IDENTIFIER, UserInfoIdentifier);
        }
    }

    /**
     * Purge all saved activities.
     */
    public void removeAllActivities() {
        this.storageManager.removeObjectsWithPrefix(USER_GROUP_IDENTIFIER);
    }

    /*
    * -------------------GETTERS-------------------------------
    */

    /*
    * getUserInfoWithName() method. Takes in a string with UserInfo name,
    * returns the corresponding UserInfo object.
    */
    public @Nullable UserInfo getUserInfoWithName(@NonNull String name)
    {
        if (name == null) {
            return null;
        }

        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(USER_GROUP_IDENTIFIER);
        ArrayList<UserInfo> activities = new ArrayList<UserInfo>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((UserInfo) obj);
            }
        }

        UserInfo s = null;
        for (UserInfo act : activities) {
            if (act.getName().equals(name))
                s = act;
        }

        return s;
    }

    /*
     * getUserInfo method, takes in a UserInfo object
     * */
    public @Nullable UserInfo getUserInfoWithIdentifier(@NonNull String identifier)
    {
        if (identifier == null) {
            return null;
        }

        return storageManager.getObject(USER_GROUP_IDENTIFIER, identifier);
    }

    /*
    * getAllUserInfos method, returns an array of all UserInfo objects
    * */
    public @NonNull ArrayList<UserInfo> getAllUserInfos()
    {
        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(USER_GROUP_IDENTIFIER);
        ArrayList<UserInfo> activities = new ArrayList<UserInfo>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((UserInfo) obj);
            }
        }

        return activities;
    }
}

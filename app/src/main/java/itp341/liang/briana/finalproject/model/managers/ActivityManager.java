package itp341.liang.briana.finalproject.model.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

import itp341.liang.briana.finalproject.model.objects.Activity;

/**
 * MyActivities Manager
 * Manages all active (daily) and general activities
 */

public class ActivityManager {
    public static final String ACTIVITY_GROUP_IDENTIFIER = "MyActivities";
    public static final String DAILY_ACTIVITY_GROUP_IDENTIFIER = "DailyActivities";
    private static ActivityManager defaultManager = new ActivityManager();


    public static @NonNull
    ActivityManager getDefaultManager() {
        return defaultManager;
    }


    private StorageManager storageManager = StorageManager.getDefaultManager();

    /*
    * Constructor for MyActivities Manager
    * */
    private ActivityManager(){

    }
    /*
     * setActivity method, takes in a Activity object
     * */
    public void setActivity(@NonNull Activity activity)
    {
        if (activity == null) {
            return;
        }

        storageManager.setObject(activity, ACTIVITY_GROUP_IDENTIFIER, activity.getIdentifier());
    }

    /*
     * setDailyActivity method, takes in a Activity object
     * */
    public void setDailyActivity(@NonNull Activity activity)
    {
        if (activity == null) {
            return;
        }

        storageManager.setObject(activity, DAILY_ACTIVITY_GROUP_IDENTIFIER, activity.getIdentifier());
    }

    /**
     * Remove a activity object.
     * @param activity The activity object to remove.
     */
    public void removeActivity(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }

        this.removeActivityWithIdentifier(activity.getIdentifier());
    }

    /**
     * Remove a daily activity object.
     * @param activity The activity object to remove.
     */
    public void removeDailyActivity(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }

        this.removeDailyActivityWithIdentifier(activity.getIdentifier());
    }

    /**
     * Remove a Activity given its identifier.
     * @param activityIdentifier The string identifier of the Activity to remove.
     */
    public void removeActivityWithIdentifier(@NonNull String activityIdentifier)
    {
        if (activityIdentifier == null) {
            return;
        }

        Activity removedActivity = this.getActivityWithIdentifier(activityIdentifier);

        if (removedActivity != null) {
            this.storageManager.removeObject(ACTIVITY_GROUP_IDENTIFIER, activityIdentifier);
        }
    }

    /**
     * Remove a Activity given its identifier.
     * @param activityIdentifier The string identifier of the Activity to remove.
     */
    public void removeDailyActivityWithIdentifier(@NonNull String activityIdentifier)
    {
        if (activityIdentifier == null) {
            return;
        }

        Activity removedActivity = this.getActivityWithIdentifier(activityIdentifier);

        if (removedActivity != null) {
            this.storageManager.removeObject(DAILY_ACTIVITY_GROUP_IDENTIFIER, activityIdentifier);
        }
    }

    /**
     * Purge all saved activities.
     */
    public void removeAllActivities() {
        this.storageManager.removeObjectsWithPrefix(ACTIVITY_GROUP_IDENTIFIER);
    }

    /*
    * -------------------GETTERS-------------------------------
    */

    /**
     * Get all activities that are active.
     * @return An ArrayList of active activities.
     */
    public @NonNull ArrayList<Activity> getActiveActivities() {
        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(DAILY_ACTIVITY_GROUP_IDENTIFIER);
        ArrayList<Activity> activities = new ArrayList<Activity>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((Activity) obj);
            }
        }

        return activities;
    }

    /*
    * getActivityWithName() method. Takes in a string with Activity name,
    * returns the corresponding Activity object.
    */
    public @Nullable Activity getActivityWithName(@NonNull String name)
    {
        if (name == null) {
            return null;
        }

        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(ACTIVITY_GROUP_IDENTIFIER);
        ArrayList<Activity> activities = new ArrayList<Activity>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((Activity) obj);
            }
        }

        Activity s = null;
        for (Activity act : activities) {
            if (act.getName().equals(name))
                s = act;
        }

        return s;
    }

    /*
     * getActivity method, takes in a Activity object
     * */
    public @Nullable Activity getActivityWithIdentifier(@NonNull String identifier)
    {
        if (identifier == null) {
            return null;
        }

        return storageManager.getObject(ACTIVITY_GROUP_IDENTIFIER, identifier);
    }

    /*
    * getAllactivities method, returns an array of all Activity objects
    * */
    public @NonNull ArrayList<Activity> getAllActivities()
    {
        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(ACTIVITY_GROUP_IDENTIFIER);
        ArrayList<Activity> activities = new ArrayList<Activity>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((Activity) obj);
            }
        }

        return activities;
    }
}

package itp341.liang.briana.finalproject.model.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * File based storage manager for all app data
 */

public class StorageManager {
    private static StorageManager defaultManager = new StorageManager();
    private Context context;

    private StorageManager() {

    }

    public static StorageManager getDefaultManager() {
        return defaultManager;
    }
    public static StorageManager getDefaultManagerWithContext(@NonNull Context context) {
        defaultManager.setContext(context);
        return defaultManager;
    }

    public void setContext(@NonNull Context context) {
        this.context = context;
    }
    public @Nullable
    Context getContext() {
        return this.context;
    }

    /* Setters */

    /**
     * Save an object to disk storage.
     * @param object The object to save.
     * @param group The name of the group to store the object in.
     * @param identifier A unique identifier for the object to store.
     */
    public <T extends Serializable> void setObject(@NonNull  T object, @NonNull String group, @NonNull String identifier) {
        this.setObject(object, group, identifier, this.context.getFilesDir().getAbsolutePath());
    }

    /**
     * Save an object to disk storage given a specific base folder path.
     * @param object The object to save.
     * @param group The name of the group to store the object in.
     * @param identifier A unique identifier for the object to store.
     * @param basePath The root folder path to save objects into.
     */
    public <T extends Serializable> void setObject(@NonNull  T object, @NonNull String group, @NonNull String identifier, @NonNull String basePath) {
        try {
            String fullPath = basePath + "/" + group;

            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(fullPath, identifier);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            FileOutputStream fileOutputStream = context.openFileOutput(identifier, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Write
            objectOutputStream.writeObject(object);

            // Clean up
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Getters */

    /**
     * Get an object from disk storage based on its namespaced identifier.
     * @source https://stackoverflow.com/a/33896724
     * @param group The name of the group to retrieve the object from.
     * @param identifier A unique identifier for the object to retrieve.
     * @return An in the group `group` and the identifier `identifier` if it exists.
     */
    public <T extends Serializable> T getObject(@NonNull String group, @NonNull String identifier) {
        return this.getObject(group, identifier, this.context.getFilesDir().getAbsolutePath());
    }

    /**
     * Get an object from disk storage based on its namespaced identifier.
     * @source https://stackoverflow.com/a/33896724
     * @param group The name of the group to retrieve the object from.
     * @param identifier A unique identifier for the object to retrieve.
     * @param basePath The root folder path to retrieve the object from.
     * @return An in the group `group` and the identifier `identifier` if it exists.
     */
    public <T extends Serializable> T getObject(@NonNull String group, @NonNull String identifier, @NonNull String basePath) {
        T object = null;

        try {
            String fullPath = basePath + "/" + group;

            File dir = new File(fullPath);
            if (!dir.exists()) {
                return object;
            }

            File file = new File(fullPath, identifier);
            if (!file.exists()) {
                return object;
            }

            FileInputStream fileInputStream = new FileInputStream(file);
//            FileInputStream fileInputStream = context.openFileInput(identifier);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Read
            object = (T) objectInputStream.readObject();

            // Clean up
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    /**
     * Get all objects from disk storage inside a particular group.
     * @param group The name of the group to retrieve objects from.
     * @return An ArrayList of all objects inside the grouop
     */
    public @NonNull
    ArrayList<Serializable> getObjectsWithPrefix(@NonNull String group) {
        if (this.context == null) {
            return new ArrayList<Serializable>();
        }
        return this.getObjectsWithPrefix(group, this.context.getFilesDir().getAbsolutePath());
    }

    /**
     * Get all objects from disk storage inside a particular group.
     * @param group The name of the group to retrieve objects from.
     * @param basePath The root folder path to retrieve objects from.
     * @return An ArrayList of all objects inside the grouop
     */
    public @NonNull ArrayList<Serializable> getObjectsWithPrefix(@NonNull String group, @NonNull String basePath) {
        ArrayList<Serializable> objects = new ArrayList<Serializable>();

        String fullPath = basePath + "/" + group;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            return objects;
        }

        for (final File fileEntry : dir.listFiles()) {
            String identifier = fileEntry.getName();
            Serializable object = this.getObject(group, identifier, basePath);
            if (object != null) {
                objects.add(object);
            } else {
                // Unable to deserialize the object, delete it
                this.removeObject(group, identifier);
            }
        }

        return objects;
    }
    /**
     * Remove the object matching the storage identifier.
     * @param group The name of the group to delete the object from.
     * @param identifier A unique identifier for the object to delete.
     */
    public void removeObject(@NonNull String group, @NonNull String identifier) {
        if (this.context == null) {
            return;
        }
        this.removeObject(group, identifier, this.context.getFilesDir().getAbsolutePath());
    }

    /**
     * Remove the object matching the storage identifier.
     * @param group The name of the group to delete the object from.
     * @param identifier A unique identifier for the object to delete.
     * @param basePath The root folder path to delete the object from.
     * @return true if removing the object was successful, false if the object couldn't be deleted.
     */
    public boolean removeObject(@NonNull String group, @NonNull String identifier, @NonNull String basePath) {
        String fullPath = basePath + "/" + group;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            return false;
        }

        File file = new File(fullPath, identifier);
        if (!file.exists()) {
            return false;
        }

        boolean success = file.delete();
        return success;
    }

    /**
     * Remove all objects inside a group.
     * @param group The name of the group to delete the objects from.
     */
    public void removeObjectsWithPrefix(@NonNull String group) {
        if (this.context == null) {
            return;
        }
        this.removeObjectsWithPrefix(group, this.context.getFilesDir().getAbsolutePath());
    }

    /**
     * Remove all objects inside a group.
     * @param group The name of the group to delete the objects from.
     * @param basePath The root folder path to delete the objects from.
     * @return true if removing the objects was successful, false if the objects couldn't be deleted.
     */
    public boolean removeObjectsWithPrefix(@NonNull String group, @NonNull String basePath) {
        String fullPath = basePath + "/" + group;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            return false;
        }

        // Recursively remove all files within the directory
        File[] fileList = dir.listFiles();
        for (final File fileEntry : fileList) {
            boolean success = fileEntry.delete();
            if (!success) {
                return false;
            }
        }

        return true;
    }

}

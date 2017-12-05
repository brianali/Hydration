package itp341.liang.briana.finalproject.model.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import itp341.liang.briana.finalproject.model.objects.Fluid;

/**
 * Fluid Manager class that manages daily water intake
 */

public class FluidManager {
    public static final String FLUID_GROUP_IDENTIFIER = "Fluids";
    private static FluidManager defaultManager = new FluidManager();
    private static final double MORNING_MINS = 12*60;
    private static final double AFTERNOON_MINS = 18*60;
    private static final double EVENING_MINS = 24*60;

    public static @NonNull
    FluidManager getDefaultManager() {
        return defaultManager;
    }
    
    private StorageManager storageManager = StorageManager.getDefaultManager();

    /*
    * Constructor for Fluids Manager
    * */
    private FluidManager(){

    }
    /*
     * setFluid method, takes in a Fluid object
     * */
    public void setFluid(@NonNull Fluid Fluid)
    {
        if (Fluid == null) {
            return;
        }

        storageManager.setObject(Fluid, FLUID_GROUP_IDENTIFIER, Fluid.getIdentifier());
    }

    /**
     * Remove a Fluid object.
     * @param Fluid The Fluid object to remove.
     */
    public void removeFluid(@Nullable Fluid Fluid) {
        if (Fluid == null) {
            return;
        }

        this.removeFluidWithIdentifier(Fluid.getIdentifier());
    }
    
    /**
     * Remove a Fluid given its identifier.
     * @param FluidIdentifier The string identifier of the Fluid to remove.
     */
    public void removeFluidWithIdentifier(@NonNull String FluidIdentifier)
    {
        if (FluidIdentifier == null) {
            return;
        }

        Fluid removedFluid = this.getFluidWithIdentifier(FluidIdentifier);

        if (removedFluid != null) {
            this.storageManager.removeObject(FLUID_GROUP_IDENTIFIER, FluidIdentifier);
        }
    }
    
    /**
     * Purge all saved activities.
     */
    public void removeAllActivities() {
        this.storageManager.removeObjectsWithPrefix(FLUID_GROUP_IDENTIFIER);
    }

    /*
    * -------------------GETTERS-------------------------------
    */
    
    /*
    * getFluidWithName() method. Takes in a string with Fluid name,
    * returns the corresponding Fluid object.
    */
    public @Nullable Fluid getFluidWithName(@NonNull String name)
    {
        if (name == null) {
            return null;
        }

        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(FLUID_GROUP_IDENTIFIER);
        ArrayList<Fluid> activities = new ArrayList<Fluid>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((Fluid) obj);
            }
        }

        Fluid s = null;
        for (Fluid act : activities) {
            if (act.getName().equals(name))
                s = act;
        }

        return s;
    }

    /*
     * getFluid method, takes in a Fluid object
     * */
    public @Nullable Fluid getFluidWithIdentifier(@NonNull String identifier)
    {
        if (identifier == null) {
            return null;
        }

        return storageManager.getObject(FLUID_GROUP_IDENTIFIER, identifier);
    }

    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull ArrayList<Fluid> getAllFluids()
    {
        ArrayList<Serializable> serials = storageManager.getObjectsWithPrefix(FLUID_GROUP_IDENTIFIER);
        ArrayList<Fluid> activities = new ArrayList<Fluid>();

        for (Serializable obj : serials) {
            if (obj != null) {
                activities.add((Fluid) obj);
            }
        }

        return activities;
    }

    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull ArrayList<Fluid> getMorningFluids()
    {
        ArrayList<Fluid> allFluids = getAllFluids();

        ArrayList<Fluid> morningFluids = new ArrayList<>();
        for (Fluid fluid: allFluids){
            int time = fluid.getTimestamp().getTime().getMinutes();
            int time2 = fluid.getTimestamp().get(Calendar.MINUTE);
            if ( 0 < time2 && time2 <= MORNING_MINS){
                morningFluids.add(fluid);
            }
        }

        return morningFluids;
    }
    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull double getTotalMorningFluids()
    {
        ArrayList<Fluid> allFluids = getAllFluids();

        ArrayList<Fluid> morningFluids = new ArrayList<>();
        double total=0;
        for (Fluid fluid: allFluids){
            int mins = fluid.getTimestamp().get(Calendar.MINUTE);
            int hours = fluid.getTimestamp().get(Calendar.HOUR_OF_DAY);
            int currMinOfDay = ((hours * 60) + mins);
            if ( 0 <= currMinOfDay && currMinOfDay < MORNING_MINS){
                morningFluids.add(fluid);
                total+=fluid.getAmount();
            }
        }

        return total;
    }
    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull ArrayList<Fluid> getAfternoonFluids()
    {
        ArrayList<Fluid> allFluids = getAllFluids();

        ArrayList<Fluid> afternoonFluids = new ArrayList<>();
        for (Fluid fluid: allFluids){
            int time = fluid.getTimestamp().getTime().getMinutes();
            int time2 = fluid.getTimestamp().get(Calendar.MINUTE);
            if ( MORNING_MINS < time2 && time2 <= AFTERNOON_MINS){
                afternoonFluids.add(fluid);
            }
        }

        return afternoonFluids;
    }
    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull double getTotalAfternoonFluids()
    {
        ArrayList<Fluid> allFluids = getAllFluids();

        double total=0;
        for (Fluid fluid: allFluids){
            int mins = fluid.getTimestamp().get(Calendar.MINUTE);
            int hours = fluid.getTimestamp().get(Calendar.HOUR_OF_DAY);
            int currMinOfDay = ((hours * 60) + mins);
            if ( MORNING_MINS <= currMinOfDay && currMinOfDay < AFTERNOON_MINS){
                total+=fluid.getAmount();
            }
        }

        return total;
    }
    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull ArrayList<Fluid> getEveningFluids()
    {
        ArrayList<Fluid> allFluids = getAllFluids();

        ArrayList<Fluid> eveningFluids = new ArrayList<>();
        for (Fluid fluid: allFluids){
            int time = fluid.getTimestamp().getTime().getMinutes();
            int time2 = fluid.getTimestamp().get(Calendar.MINUTE);
            if ( AFTERNOON_MINS < time2 && time2 <= EVENING_MINS){
                eveningFluids.add(fluid);
            }
        }

        return eveningFluids;
    }
    /*
    * getAllFluids method, returns an array of all Fluid objects
    * */
    public @NonNull double getTotalEveningFluids()
    {
        ArrayList<Fluid> allFluids = getAllFluids();

        ArrayList<Fluid> eveningFluids = new ArrayList<>();
        double total = 0;
        for (Fluid fluid: allFluids){
            int mins = fluid.getTimestamp().get(Calendar.MINUTE);
            int hours = fluid.getTimestamp().get(Calendar.HOUR_OF_DAY);
            int currMinOfDay = ((hours * 60) + mins);
            if ( AFTERNOON_MINS <= currMinOfDay && currMinOfDay < EVENING_MINS){
                eveningFluids.add(fluid);
                total+=fluid.getAmount();
            }
        }

        return total;
    }
}

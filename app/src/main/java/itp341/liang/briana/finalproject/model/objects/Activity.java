package itp341.liang.briana.finalproject.model.objects;

/**
 * MyActivities class
 */

public class Activity extends NamedObject {
    private static final long serialVersionUID = 4L;
    private int duration;
    private double addedWater;

    public Activity(String name, int duration, double addedWater) {
        super(name);
        this.duration = duration;
        this.addedWater = addedWater;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getAddedWater() {
        return addedWater;
    }

    public void setAddedWater(double addedWater) {
        this.addedWater = addedWater;
    }
}

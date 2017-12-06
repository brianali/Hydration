package itp341.liang.briana.finalproject.model.objects;

/**
 * User Info class
 */

public class UserInfo extends NamedObject {
    private static final long serialVersionUID = 7L;
    private double weight=0;
    private double dailyWaterGoal=0;

    public UserInfo(String username, double weight, double dailyWaterGoal) {
        super(username);
        this.weight = weight;
        this.dailyWaterGoal = dailyWaterGoal;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getDailyWaterGoal() {
        return dailyWaterGoal;
    }

    public void setDailyWaterGoal(double dailyWaterGoal) {
        this.dailyWaterGoal = dailyWaterGoal;
    }
}

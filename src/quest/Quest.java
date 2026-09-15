package quest;

public class Quest {
    public enum Status { ACTIVE, COMPLETED, FAILED }

    public String title;
    public String description;
    public Status status = Status.ACTIVE;
    public int progress = 0;
    public int target = 1;
    public String rewardXp = "0";

    public Quest(String title, String desc, int target) {
        this.title = title;
        this.description = desc;
        this.target = target;
    }

    public void progress(int amount) {
        progress += amount;
        if (progress >= target) status = Status.COMPLETED;
    }
}
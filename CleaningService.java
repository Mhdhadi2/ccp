/**
 * CleaningService simulates cleaning and restocking.
 */
public class CleaningService extends Thread {
    private final String planeName;

    public CleaningService(String planeName) {
        super(planeName + "-Cleaning");
        this.planeName = planeName;
    }

    @Override
    public void run() {
        try {
            Logger.log(getName(), "Cleaning and restocking supplies.");
            Thread.sleep(Utils.randomBetween(1000, 2000));
            Logger.log(getName(), "Cleaning complete.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

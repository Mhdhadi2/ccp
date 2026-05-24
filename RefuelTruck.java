/**
 * RefuelTruck allows only one plane to refuel at a time.
 * Uses synchronized for mutual exclusion.
 */
public class RefuelTruck {
    public synchronized void refuel(String planeName) throws InterruptedException {
        Logger.log(planeName + "-Refuel", "Refueling aircraft.");
        Thread.sleep(Utils.randomBetween(700, 1000)); // Updated timing
        Logger.log(planeName + "-Refuel", "Refueling complete.");
    }
}

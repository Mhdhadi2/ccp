/**
 * RefuelTruck allows only one plane to refuel at a time.
 */
public class RefuelTruck {
    public synchronized void refuel(String planeName) throws InterruptedException {
        Logger.log(planeName + "-Refuel", "Refueling aircraft.");
        Thread.sleep(Utils.randomBetween(1000, 2000));
        Logger.log(planeName + "-Refuel", "Refueling complete.");
    }
}

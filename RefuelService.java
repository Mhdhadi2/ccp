/**
 * RefuelService requests the shared RefuelTruck.
 * RefuelTruck uses 'synchronized' so only a single plane can refuel at any time.
 */
public class RefuelService extends Thread {
    private final String planeName;
    private final RefuelTruck truck;

    public RefuelService(String planeName, RefuelTruck truck) {
        super(planeName + "-Refuel");
        this.planeName = planeName;
        this.truck = truck;
    }

    @Override
    public void run() {
        try {
            truck.refuel(planeName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

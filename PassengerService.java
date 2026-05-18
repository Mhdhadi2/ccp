/**
 * PassengerService simulates passenger disembarkation and boarding.
 */
public class PassengerService extends Thread {
    private final String planeName;
    private final int passengerCount;

    public PassengerService(String planeName, int passengerCount) {
        super(planeName + "-Passengers");
        this.planeName = planeName;
        this.passengerCount = passengerCount;
    }

    @Override
    public void run() {
        try {
            Logger.log(getName(), passengerCount + " passengers disembarking.");
            Thread.sleep(Utils.randomBetween(1000, 2000));
            Logger.log(getName(), "Passengers boarding.");
            Thread.sleep(Utils.randomBetween(1000, 2000));
            Logger.log(getName(), "Passenger service complete.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

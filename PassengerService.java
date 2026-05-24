/**
 * PassengerService simulates passenger disembarkation and boarding.
 * Each is timed independently to more closely simulate real world.
 */
public class PassengerService extends Thread {
    private final String planeName;
    private final int passengersToDisembark;
    private final int passengersToBoard;

    public PassengerService(String planeName, int passengersToDisembark, int passengersToBoard) {
        super(planeName + "-Passengers");
        this.planeName = planeName;
        this.passengersToDisembark = passengersToDisembark;
        this.passengersToBoard = passengersToBoard;
    }

    @Override
    public void run() {
        try {
            Logger.log(getName(), passengersToDisembark + " passengers disembarking.");
            Thread.sleep(Utils.randomBetween(600, 1000));
            Logger.log(getName(), passengersToBoard + " passengers boarding.");
            Thread.sleep(Utils.randomBetween(600, 1000));
            Logger.log(getName(), "Passenger service complete.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

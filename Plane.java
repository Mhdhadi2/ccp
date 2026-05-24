/**
 * Plane represents a plane lifecycle as a Thread.
 * All mutating operations (landing, gate, takeoff) occur in correct thread context.
 */
public class Plane extends Thread {
    private final Airport airport;
    private final Statistics stats;
    private final boolean emergency;
    private final int passengersToDisembark;
    private final int passengersToBoard;
    private long landingRequestTime;

    private Gate assignedGate;    // Store the gate assigned at landing permission

    public Plane(String name, Airport airport, Statistics stats, boolean emergency,
                 int passengersToDisembark, int passengersToBoard) {
        super(name);
        this.airport = airport;
        this.stats = stats;
        this.emergency = emergency;
        this.passengersToDisembark = passengersToDisembark;
        this.passengersToBoard = passengersToBoard;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setLandingRequestTime(long time) {
        this.landingRequestTime = time;
    }

    public int getPassengersToDisembark() {
        return passengersToDisembark;
    }

    public int getPassengersToBoard() {
        return passengersToBoard;
    }

    @Override
    public void run() {
        try {
            Logger.log(getName(), "Arrived in airspace with " + passengersToDisembark +
                    " passengers to disembark, " + passengersToBoard + " to board.");
            Logger.log(getName(), "Requesting landing.");
            // Get permission (ATC assigns a gate up-front!)
            assignedGate = airport.requestLanding(this);

            // Landing (timing reduced for < 60 seconds wall time)
            Logger.log(getName(), (emergency ? "EMERGENCY LANDING: " : "") + "Landing at " + assignedGate.getName() + ".");
            Thread.sleep(Utils.randomBetween(600, 900)); // = Landing required range!
            airport.releaseRunway(getName());

            // Coasting to assigned gate
            Logger.log(getName(), "Coasting to " + assignedGate.getName() + ".");
            Thread.sleep(Utils.randomBetween(300, 500));

            // Docking
            Logger.log(getName(), "Docking at " + assignedGate.getName() + ".");
            Thread.sleep(Utils.randomBetween(250, 400));

            // Start concurrent servicing at gate
            PassengerService ps = new PassengerService(getName(), passengersToDisembark, passengersToBoard);
            CleaningService cs = new CleaningService(getName());
            RefuelService rs = new RefuelService(getName(), airport.getRefuelTruck());

            ps.start();
            cs.start();
            rs.start();

            ps.join();
            cs.join();
            rs.join();

            // Update statistics: only passengers boarded are counted toward total
            stats.addPassengers(passengersToBoard);

            // Undocking
            Logger.log(getName(), "Undocking from " + assignedGate.getName() + ".");
            Thread.sleep(Utils.randomBetween(250, 400));
            airport.releaseGate(assignedGate, getName());

            // Request takeoff (mutual exclusion: only one on runway)
            Logger.log(getName(), "Requesting takeoff.");
            airport.requestTakeoff(this);

            // Coasting to runway
            Logger.log(getName(), "Coasting to runway.");
            Thread.sleep(Utils.randomBetween(300, 500));

            // Takeoff
            Logger.log(getName(), "Taking off.");
            Thread.sleep(Utils.randomBetween(600, 900));
            airport.releaseRunway(getName());
            airport.planeDeparted(getName());
            Logger.log(getName(), "Departed.");

            stats.incrementPlanesServed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

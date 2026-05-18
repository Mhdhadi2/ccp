/**
 * Plane represents a plane lifecycle as a Thread.
 */
public class Plane extends Thread {
    private final Airport airport;
    private final Statistics stats;
    private final boolean emergency;
    private final int passengerCount;
    private long landingRequestTime;

    public Plane(String name, Airport airport, Statistics stats, boolean emergency) {
        super(name);
        this.airport = airport;
        this.stats = stats;
        this.emergency = emergency;
        this.passengerCount = Utils.randomBetween(10, 50);
    }

    public boolean isEmergency() {
        return emergency;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setLandingRequestTime(long time) {
        this.landingRequestTime = time;
    }

    @Override
    public void run() {
        try {
            Logger.log(getName(), "Arrived in airspace with " + passengerCount + " passengers.");
            Logger.log(getName(), "Requesting landing.");
            airport.requestLanding(this);

            // Landing
            Logger.log(getName(), "Landing.");
            Thread.sleep(Utils.randomBetween(1000, 2000));
            airport.releaseRunway(getName());

            // Taxi to gate
            Logger.log(getName(), "Coasting to gate.");
            Thread.sleep(Utils.randomBetween(500, 1000));
            Gate gate = airport.assignGate(this);

            // Docking
            Logger.log(getName(), "Docking at " + gate.getName() + ".");
            Thread.sleep(500);

            // Start concurrent services
            PassengerService ps = new PassengerService(getName(), passengerCount);
            CleaningService cs = new CleaningService(getName());
            RefuelService rs = new RefuelService(getName(), airport.getRefuelTruck());

            ps.start();
            cs.start();
            rs.start();

            ps.join();
            cs.join();
            rs.join();

            // Update stats after service (passengers boarded)
            stats.addPassengers(passengerCount);

            // Undock
            Logger.log(getName(), "Undocking from " + gate.getName() + ".");
            Thread.sleep(500);
            airport.releaseGate(gate, getName());

            // Request takeoff
            Logger.log(getName(), "Requesting takeoff.");
            airport.requestTakeoff(this);

            // Taxi to runway
            Logger.log(getName(), "Coasting to runway.");
            Thread.sleep(Utils.randomBetween(500, 1000));

            // Takeoff
            Logger.log(getName(), "Taking off.");
            Thread.sleep(Utils.randomBetween(1000, 2000));
            airport.releaseRunway(getName());
            airport.planeDeparted(getName());
            Logger.log(getName(), "Departed.");

            stats.incrementPlanesServed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

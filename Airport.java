import java.util.LinkedList;
import java.util.Queue;

/**
 * Airport manages runway, gates, and ground capacity.
 * Uses synchronized, wait(), and notifyAll() for coordination.
 * Emergency priority is enforced. ALL mutual exclusion uses synchronized.
 */
public class Airport {
    private final Gate[] gates = {
            new Gate("Gate-1"),
            new Gate("Gate-2"),
            new Gate("Gate-3")
    };

    private final RefuelTruck refuelTruck;
    private final Statistics stats;

    // Only one plane may use the runway at a time! (Runway mutual exclusion)
    private boolean runwayFree = true;
    // Planes counted on ground (runway + gates); enforced max is 3
    private int planesOnGround = 0;

    // Queues for fairness & emergency priority in air
    private final Queue<Plane> emergencyQueue = new LinkedList<>();
    private final Queue<Plane> normalQueue = new LinkedList<>();

    public Airport(RefuelTruck refuelTruck, Statistics stats) {
        this.refuelTruck = refuelTruck;
        this.stats = stats;
    }

    public RefuelTruck getRefuelTruck() {
        return refuelTruck;
    }

    /**
     * Request permission to land.
     * Plane will be blocked until all are true:
     * - Runway is free (no plane landing/taking off)
     * - Airport ground capacity < 3
     * - A gate is AVAILABLE and reserved IMMEDIATELY for this plane
     * - Emergency plane has priority if one is present
     * Returns the gate assigned (reserved) for this plane.
     * Plane will NOT land and then wait for a gate: gate is assigned up front.
     */
    public synchronized Gate requestLanding(Plane plane) {
        long requestTime = System.currentTimeMillis();
        plane.setLandingRequestTime(requestTime);
        Logger.log("ATC", "Received landing request from " + plane.getName()
                + (plane.isEmergency() ? " (EMERGENCY)" : ""));

        // Place plane in correct queue based on emergency
        if (plane.isEmergency()) {
            emergencyQueue.add(plane);
        } else {
            normalQueue.add(plane);
        }

        Gate assignedGate = null;
        // Wait until all conditions met for this plane (while = safe against spurious wakeups)
        while (true) {
            boolean capacityOk = planesOnGround < 3;
            boolean runwayOk = runwayFree;
            boolean hasPriority = isNextToLand(plane);
            assignedGate = findAvailableGate();

            // Must have an assigned, reservable gate up front!
            if (capacityOk && runwayOk && hasPriority && assignedGate != null) {
                // Reserve gate immediately
                assignedGate.occupy(plane.getName());

                // Remove this plane from the appropriate queue
                if (plane.isEmergency()) {
                    emergencyQueue.remove(plane);
                } else {
                    normalQueue.remove(plane);
                }
                runwayFree = false; // Lock runway
                planesOnGround++;

                long grantTime = System.currentTimeMillis();
                long waitTime = grantTime - requestTime;
                stats.recordWaitingTime(waitTime);

                Logger.log("ATC", (plane.isEmergency() ? "[EMERGENCY PRIORITY] " : "")
                    + "Landing permission granted for " + plane.getName()
                    + " (waited " + waitTime + " ms). Gate reserved: " + assignedGate.getName());
                notifyAll(); // Notify all planes of state change
                return assignedGate;
            }

            try {
                wait(); // Release airport lock; re-acquire later
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    // Find the next free (unoccupied) gate, or null if all gates full
    private Gate findAvailableGate() {
        for (Gate g : gates) {
            if (!g.isOccupied()) return g;
        }
        return null;
    }

    /**
     * Determines if plane is next in landing queue.
     * Emergency planes always have strict priority.
     */
    private boolean isNextToLand(Plane plane) {
        if (!emergencyQueue.isEmpty()) {
            return emergencyQueue.peek() == plane;
        }
        return normalQueue.peek() == plane;
    }

    /**
     * Release the runway after landing or takeoff.
     */
    public synchronized void releaseRunway(String planeName) {
        runwayFree = true;
        Logger.log("ATC", "Runway is now free (released by " + planeName + ")");
        notifyAll();
    }

    /**
     * Release a gate after plane departs.
     * (Gate reservation cleared. notifyAll() wakes up any plane waiting for a gate.)
     */
    public synchronized void releaseGate(Gate gate, String planeName) {
        gate.release();
        Logger.log("ATC", gate.getName() + " is now free (released by " + planeName + ")");
        notifyAll();
    }

    /**
     * Request permission to take off.
     * Only permitted if runway is free.
     * Note: plane is already undocked from gate and ready on taxiway.
     */
    public synchronized void requestTakeoff(Plane plane) {
        Logger.log("ATC", "Received takeoff request from " + plane.getName());
        while (!runwayFree) { // Proper waiting on runway
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        runwayFree = false;
        Logger.log("ATC", "Takeoff granted for " + plane.getName());
        notifyAll();
    }

    /**
     * Called when plane fully departs airport (after takeoff).
     * Updates planesOnGround and broadcasts change.
     */
    public synchronized void planeDeparted(String planeName) {
        planesOnGround--;
        Logger.log("ATC", planeName + " has left the airport grounds. Ground count now: " + planesOnGround);
        notifyAll();
    }

    // Sanity check for statistics
    public synchronized boolean allGatesEmpty() {
        for (Gate g : gates) {
            if (g.isOccupied()) return false;
        }
        return true;
    }
}

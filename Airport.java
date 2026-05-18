import java.util.LinkedList;
import java.util.Queue;

/**
 * Airport manages runway, gates, and ground capacity.
 * Uses synchronized, wait(), and notifyAll() for coordination.
 */
public class Airport {
    private final Gate[] gates = {
            new Gate("Gate-1"),
            new Gate("Gate-2"),
            new Gate("Gate-3")
    };

    private final RefuelTruck refuelTruck;
    private final Statistics stats;

    private boolean runwayFree = true;
    private int planesOnGround = 0; // includes runway and gates

    // Waiting queues (air holding pattern)
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
     * Blocks until runway is free, capacity allows, and plane has priority.
     */
    public synchronized void requestLanding(Plane plane) {
        long requestTime = System.currentTimeMillis();
        plane.setLandingRequestTime(requestTime);
        Logger.log("ATC", "Received landing request from " + plane.getName() + (plane.isEmergency() ? " (EMERGENCY)" : ""));

        // Enqueue plane
        if (plane.isEmergency()) {
            emergencyQueue.add(plane);
        } else {
            normalQueue.add(plane);
        }

        while (true) {
            boolean capacityOk = planesOnGround < 3;
            boolean runwayOk = runwayFree;
            boolean hasPriority = isNextToLand(plane);

            if (capacityOk && runwayOk && hasPriority) {
                // Grant permission
                runwayFree = false;
                planesOnGround++;

                // Remove from queue
                if (plane.isEmergency()) {
                    emergencyQueue.remove(plane);
                } else {
                    normalQueue.remove(plane);
                }

                long grantTime = System.currentTimeMillis();
                long waitTime = grantTime - requestTime;
                stats.recordWaitingTime(waitTime);

                Logger.log("ATC", "Landing permission granted for " + plane.getName() + " (waited " + waitTime + " ms)");
                notifyAll();
                return;
            }

            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

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
     * Assign a free gate to a plane. Blocks until a gate is free.
     */
    public synchronized Gate assignGate(Plane plane) {
        while (true) {
            for (Gate g : gates) {
                if (!g.isOccupied()) {
                    g.occupy(plane.getName());
                    Logger.log("ATC", g.getName() + " assigned to " + plane.getName());
                    notifyAll();
                    return g;
                }
            }
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    /**
     * Release a gate after plane departs.
     */
    public synchronized void releaseGate(Gate gate, String planeName) {
        gate.release();
        Logger.log("ATC", gate.getName() + " is now free (released by " + planeName + ")");
        notifyAll();
    }

    /**
     * Request permission to take off.
     * Blocks until runway is free.
     */
    public synchronized void requestTakeoff(Plane plane) {
        Logger.log("ATC", "Received takeoff request from " + plane.getName());
        while (!runwayFree) {
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
     */
    public synchronized void planeDeparted(String planeName) {
        planesOnGround--;
        Logger.log("ATC", planeName + " has left the airport grounds. Ground count now: " + planesOnGround);
        notifyAll();
    }

    public synchronized boolean allGatesEmpty() {
        for (Gate g : gates) {
            if (g.isOccupied()) return false;
        }
        return true;
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Statistics stores waiting times and totals. Thread-safe via synchronization.
 */
public class Statistics {
    private final List<Long> waitingTimes = new ArrayList<>();
    private int planesServed = 0;
    private int totalPassengers = 0;

    public synchronized void recordWaitingTime(long ms) {
        waitingTimes.add(ms);
    }

    public synchronized void incrementPlanesServed() {
        planesServed++;
    }

    public synchronized void addPassengers(int count) {
        totalPassengers += count;
    }

    public void printFinalReport(Airport airport) {
        long min = 0, max = 0, avg = 0;
        synchronized (this) {
            if (!waitingTimes.isEmpty()) {
                min = Collections.min(waitingTimes);
                max = Collections.max(waitingTimes);
                long sum = 0;
                for (long t : waitingTimes) sum += t;
                avg = sum / waitingTimes.size();
            }
        }

        Logger.log("STATS", "Sanity check - all gates empty: " + airport.allGatesEmpty());
        Logger.log("STATS", "Minimum waiting time: " + min + " ms");
        Logger.log("STATS", "Maximum waiting time: " + max + " ms");
        Logger.log("STATS", "Average waiting time: " + avg + " ms");
        Logger.log("STATS", "Total planes served: " + planesServed);
        Logger.log("STATS", "Total passengers boarded: " + totalPassengers);
    }
}

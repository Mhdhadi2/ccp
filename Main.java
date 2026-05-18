public class Main {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("SYSTEM", "Starting Asia Pacific Airport simulation.");

        Statistics stats = new Statistics();
        RefuelTruck refuelTruck = new RefuelTruck();
        Airport airport = new Airport(refuelTruck, stats);

        Plane[] planes = new Plane[6];
        for (int i = 0; i < 6; i++) {
            boolean emergency = (i == 5); // Plane-6 is emergency
            planes[i] = new Plane("Plane-" + (i + 1), airport, stats, emergency);
        }

        // Start planes with random arrival delays of 0-2 seconds
        for (Plane p : planes) {
            int delay = Utils.randomBetween(0, 2000);
            Thread.sleep(delay);
            p.start();
        }

        // Wait for all planes to finish
        for (Plane p : planes) {
            p.join();
        }

        Logger.log("SYSTEM", "All planes have departed. Printing final statistics...\n");
        stats.printFinalReport(airport);
    }
}

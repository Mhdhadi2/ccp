public class Main {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("SYSTEM", "Starting Asia Pacific Airport simulation.");

        Statistics stats = new Statistics();
        RefuelTruck refuelTruck = new RefuelTruck();
        Airport airport = new Airport(refuelTruck, stats);

        Plane[] planes = new Plane[6];
        // Generate passenger sets according to updated logic (disembark != board)
        for (int i = 0; i < 6; i++) {
            boolean emergency = (i == 5); // Plane-6 is emergency
            int disembark = Utils.randomBetween(10, 50);
            int board = Utils.randomBetween(10, 50);
            planes[i] = new Plane("Plane-" + (i + 1), airport, stats, emergency, disembark, board);
        }

        // --------- DETERMINISTIC EMERGENCY SCENARIO ----------
        // Plane-1, Plane-2, Plane-3 fill the gates (start "simultaneously")
        planes[0].start();
        Thread.sleep(100); // slight stagger for more readable logging
        planes[1].start();
        Thread.sleep(100);
        planes[2].start();

        // Wait for these planes to approach being assigned gates (simulate congestion)
        Thread.sleep(700);

        // Plane-4 and Plane-5 now waiting in the air: airport is at ground capacity
        Logger.log("ATC", "Airport congestion forming: All gates are about to be occupied.");
        planes[3].start();
        Thread.sleep(150); // small separation
        planes[4].start();

        // Wait for congestion. Then trigger emergency plane arrival.
        Thread.sleep(500);
        Logger.log("Plane-6", "Emergency fuel shortage reported! Declaring emergency on approach.");
        planes[5].start();

        // Wait for all planes to finish
        for (Plane p : planes) {
            p.join();
        }
        Logger.log("SYSTEM", "All planes have departed. Printing final statistics...\n");
        stats.printFinalReport(airport);
    }
}

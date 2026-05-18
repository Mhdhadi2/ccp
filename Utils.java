/**
 * Utility functions.
 */
public class Utils {
    public static int randomBetween(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }
}

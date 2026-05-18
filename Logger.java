/**
 * Logger utility to standardize output.
 */
public class Logger {
    public static synchronized void log(String who, String msg) {
        System.out.println(who + ": " + msg);
    }
}

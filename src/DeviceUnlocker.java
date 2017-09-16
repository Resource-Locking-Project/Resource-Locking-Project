/**
 * Provides for unlocking resource-control devices.
 *
 * @author Dr. Jody Paul
 * @version 1.1.5
 * @see Device
 * @see <a href="../projectDescription.html">Project Description</a>
 */
public abstract class DeviceUnlocker {
    /**
     * Unlocks a device-controlled resource.
     * This method must be guaranteed to halt, regardless of
     * whether or not it successfully unlocked the resource.
     * @param dev the device controlling the resource to unlock
     * @return true if the resource is unlocked (all bits in the
     *         device are now identical); false otherwise
     */
    public static boolean unlock(final Device dev) {
        return FourBitTwoDisclosureDeviceUnlocker.unlock(dev);
    }

    /** Establish a string for tracing progress. */
    private static String trace = "";

    /**
     * Utility to append a line to the trace.
     * @param message the line to append;
     *                if null, the trace is reset to empty
     */
    protected static void log(final String message) {
        if (message == null) {
            trace = "";
        } else {
            trace += message + "\n";
        }
    }

    /**
     * Retrieve trace of previous unlock process.
     * @return rendering of steps in the unlock process
     */
    public static String showTrace() {
        return trace;
    }
}

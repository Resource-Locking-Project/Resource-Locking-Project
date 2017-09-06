import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class DeviceUnlocker {

    /**
     * Retrieve trace of previous unlock process.
     * @return Rendering of steps in unlock process.
     */
    public static String showTrace() {
        throw new NotImplementedException();
    };

    /**
     * Unlocks a device-controlled resource.
     * @param dev the device controlling the resource to unlock.
     * @return true if the resource is unlocked (all bits are now identical); false otherwise
     */
    public static boolean unlock(Device dev) {
        throw new NotImplementedException();
    };

}

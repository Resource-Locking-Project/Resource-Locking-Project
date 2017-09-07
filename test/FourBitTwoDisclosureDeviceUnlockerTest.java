import org.junit.Test;

import static org.mockito.Mockito.*;

public class FourBitTwoDisclosureDeviceUnlockerTest {
    @Test
    public void nullDeviceTest() {
        FourBitTwoDisclosureDeviceUnlocker.unlock(null);
    }

    @Test
    /**
     * Ensures that the trace is showing accurate information.
     */
    public void traceTest() {
        Device dev = spy(Device.class);
        FourBitTwoDisclosureDeviceUnlocker.unlock(dev);
        StringBuilder stringBuilder = new StringBuilder(FourBitTwoDisclosureDeviceUnlocker.showTrace());
        // chop off the brackets
        String test = stringBuilder.substring(1,stringBuilder.length() - 1);
        // split it by comma
        String[] split = test.split(", ");

        int spins, pokes, peeks;
        spins = pokes = peeks = 0;
        for (String atom : split) {
            if (atom.startsWith("spin")) spins++;
            else if (atom.startsWith("poke")) pokes++;
            else if (atom.startsWith("peek")) peeks++;
        }
        // times spin was called matches trace
        verify(dev,times(spins)).spin();
        // times peek was called matches trace
        verify(dev,times(peeks)).peek(any());
        // times pokes was called matches trace
        verify(dev,times(pokes)).poke(any());
    }

    @Test(timeout=1000)
    /**
     * unlock should halt/finish within 1/10th of a second.
     */
    public void heuristicHaltingTest() {
        for (int i = 0; i < 10; i++) {
            Device dev = new Device();
            FourBitTwoDisclosureDeviceUnlocker.unlock(dev);
        }
    }
    @Test
    /**
     * This should test for 99% efficiency in unlocking all 3 kinds of devices: linear, polynomial, and random rotations.
     */
    public void efficiencyTest() {
        int successes = 0;
        for (int i = 0; i < 1000; i++) {
            Device dev = null;
            if (i % 3 == 0) dev = new Device();
            else if (i % 3 == 1) dev = new Device(true);
            else if (i % 3 == 2) dev = new Device(false, (int)(Math.random() * 4), (int)(Math.random() * 10));
            if (FourBitTwoDisclosureDeviceUnlocker.unlock(dev)) successes++;
            else System.out.println(dev + ": " + dev.superPeek());
        }
        System.out.println(successes);
        assert(successes > 1000 * 0.99);
    }
}

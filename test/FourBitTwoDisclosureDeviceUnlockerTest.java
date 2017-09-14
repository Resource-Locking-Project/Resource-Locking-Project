import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
            System.out.println(atom);
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
    public void heuristicHaltingTestForUnlock() {
        for (int i = 0; i < 10; i++) {
            Device dev = new Device();
            FourBitTwoDisclosureDeviceUnlocker.unlock(dev);
        }
    }
    @Test
    /**
     * This should test for 99% efficiency in unlocking all 3 kinds of devices: linear, polynomial, and random rotations.
     */
    public void efficiencyTestForUnlock() {
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


    @Test
    public void testDoSpinInvalidCall() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Device device = new Device();
        int stateCreated = 1;

        Method doSpin = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doSpin", int.class);
        doSpin.setAccessible(true);

        Field devField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("dev");
        devField.setAccessible(true);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);

        Field stateField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateCreated);
        int state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(stateCreated == state);

        boolean result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 5);
        Assert.assertFalse(result);
        Assert.assertTrue(stateCreated == state);

        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 0);
        Assert.assertFalse(result);
        Assert.assertTrue(stateCreated == state);

        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, -1);
        Assert.assertFalse(result);
        Assert.assertTrue(stateCreated == state);
    }


    @Test
    public void testDoSpinValidCalls() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Device device = new Device();
        int stateSpun= 2;
        int created = 1;
        int statePoked = 4;
        int statePeeked = 3;

        Method doSpin = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doSpin", int.class);
        doSpin.setAccessible(true);

        Field devField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("dev");
        devField.setAccessible(true);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);

        Field stateField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, 1);

        boolean result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        int state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);
        Assert.assertFalse(result);


        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 5);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);
        Assert.assertFalse(result);

        boolean[] bits = {true, true, true, true};
        device = new Device(bits, 2);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 5);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);
        Assert.assertTrue(result);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateSpun);
        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 5);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);
        Assert.assertTrue(result);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 5);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);
        Assert.assertTrue(result);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        result = (boolean) doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 5);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);
        Assert.assertTrue(result);
    }
}

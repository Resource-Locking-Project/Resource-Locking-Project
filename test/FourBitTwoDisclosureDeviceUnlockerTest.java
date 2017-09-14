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
    /*
     * Ensures that the trace is showing accurate information.
     */
    public void traceTest() {
        Device dev = spy(Device.class);
        FourBitTwoDisclosureDeviceUnlocker.unlock(dev);
        // split by new line character
        String[] split = FourBitTwoDisclosureDeviceUnlocker.showTrace().split("\n");

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
    /*
     * unlock should halt/finish within 1/10th of a second.
     */
    public void heuristicHaltingTestForUnlock() {
        for (int i = 0; i < 10; i++) {
            Device dev = new Device();
            FourBitTwoDisclosureDeviceUnlocker.unlock(dev);
        }
    }
    @Test
    /*
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


    @Test
    public void testInvalidPeekedPatternCall() throws Exception {
        Device device = new Device();
        int created = 1;
        int statePeeked = 3;
        int statePoked = 4;

        Field devField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("dev");
        devField.setAccessible(true);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);

        Field stateField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);

        Method doSpin = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doSpin", int.class);
        doSpin.setAccessible(true);

        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        Method doPeek = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doPeek", CharSequence.class);
        doPeek.setAccessible(true);

        CharSequence requestPattern = "----";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        CharSequence pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        int state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(state == created);

        requestPattern = "????";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == created);

        requestPattern = "?-??";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == created);

        requestPattern = "?---";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == created);

        requestPattern = "????--";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == created);


        requestPattern = "??-";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == created);

        requestPattern = "";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == created);

        requestPattern = "???----------?";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, created);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == created);


        requestPattern = "?--?";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePeeked);


        requestPattern = "?--?";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePoked);


        requestPattern = "?--?---";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePeeked);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        requestPattern = "?--?---";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePoked);


        requestPattern = "????";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePeeked);


        requestPattern = "????";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePoked);


        requestPattern = "";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePeeked);


        requestPattern = "";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        pattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(requestPattern.equals(pattern));
        Assert.assertTrue(pattern.length() == requestPattern.length());
        Assert.assertTrue(state == statePoked);
    }


    @Test
    public void testValidDoPeekCalls()  throws Exception {
        int stateCreated = 1;
        int statePeeked = 3;
        Device device = new Device();

        Field devField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("dev");
        devField.setAccessible(true);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);

        Field stateField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateCreated);
        Method doSpin = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doSpin", int.class);
        doSpin.setAccessible(true);

        Method doPeek = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doPeek", CharSequence.class);
        doPeek.setAccessible(true);

        CharSequence requestPattern = "??--";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        CharSequence returnPattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        int state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(returnPattern.length() == requestPattern.length());
        Assert.assertTrue((returnPattern.charAt(0) == 'T') || (returnPattern.charAt(0) == 'F'));
        Assert.assertTrue((returnPattern.charAt(1) == 'T') || (returnPattern.charAt(1) == 'F'));
        Assert.assertTrue(returnPattern.charAt(2) == '-');
        Assert.assertTrue(returnPattern.charAt(3) == '-');
        Assert.assertTrue(state == statePeeked);


        requestPattern = "?-?-";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        returnPattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(returnPattern.length() == requestPattern.length());
        Assert.assertTrue((returnPattern.charAt(0) == 'T') || (returnPattern.charAt(0) == 'F'));
        Assert.assertTrue((returnPattern.charAt(2) == 'T') || (returnPattern.charAt(2) == 'F'));
        Assert.assertTrue(returnPattern.charAt(1) == '-');
        Assert.assertTrue(returnPattern.charAt(3) == '-');
        Assert.assertTrue(state == statePeeked);

        requestPattern = "?--?";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        returnPattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(returnPattern.length() == requestPattern.length());
        Assert.assertTrue((returnPattern.charAt(0) == 'T') || (returnPattern.charAt(0) == 'F'));
        Assert.assertTrue((returnPattern.charAt(3) == 'T') || (returnPattern.charAt(3) == 'F'));
        Assert.assertTrue(returnPattern.charAt(1) == '-');
        Assert.assertTrue(returnPattern.charAt(2) == '-');
        Assert.assertTrue(state == statePeeked);

        requestPattern = "-?-?";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        returnPattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(returnPattern.length() == requestPattern.length());
        Assert.assertTrue((returnPattern.charAt(1) == 'T') || (returnPattern.charAt(1) == 'F'));
        Assert.assertTrue((returnPattern.charAt(3) == 'T') || (returnPattern.charAt(3) == 'F'));
        Assert.assertTrue(returnPattern.charAt(0) == '-');
        Assert.assertTrue(returnPattern.charAt(2) == '-');
        Assert.assertTrue(state == statePeeked);

        requestPattern = "-??-";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        returnPattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(returnPattern.length() == requestPattern.length());
        Assert.assertTrue((returnPattern.charAt(1) == 'T') || (returnPattern.charAt(1) == 'F'));
        Assert.assertTrue((returnPattern.charAt(2) == 'T') || (returnPattern.charAt(2) == 'F'));
        Assert.assertTrue(returnPattern.charAt(0) == '-');
        Assert.assertTrue(returnPattern.charAt(3) == '-');
        Assert.assertTrue(state == statePeeked);

        requestPattern = "--??";
        doSpin.invoke(FourBitTwoDisclosureDeviceUnlocker.class, 1);
        returnPattern = (CharSequence) doPeek.invoke(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);

        Assert.assertTrue(returnPattern.length() == requestPattern.length());
        Assert.assertTrue((returnPattern.charAt(2) == 'T') || (returnPattern.charAt(2) == 'F'));
        Assert.assertTrue((returnPattern.charAt(3) == 'T') || (returnPattern.charAt(3) == 'F'));
        Assert.assertTrue(returnPattern.charAt(0) == '-');
        Assert.assertTrue(returnPattern.charAt(1) == '-');
        Assert.assertTrue(state == statePeeked);
    }

    @Test
    public void testInvalidPokeCall() throws Exception {
        int stateNotCreated = 0;
        int stateCreated = 1;
        int stateSpun = 2;
        int statePeeked = 3;
        int statePoked = 4;
        Device device = new Device();

        Field devField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("dev");
        devField.setAccessible(true);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);

        Field stateField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateCreated);

        Field peekedPatternField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("peekedPattern");
        peekedPatternField.setAccessible(true);

        Field changeBitToField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("changeBitTo");
        changeBitToField.setAccessible(true);

        Method doPoke = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doPoke");
        doPoke.setAccessible(true);

        //Invalid State
        peekedPatternField.set(FourBitTwoDisclosureDeviceUnlocker.class, "?--?");
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateNotCreated);
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        int state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateNotCreated);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateCreated);
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateCreated);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, stateSpun);
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == stateSpun);

        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePoked);
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == statePoked);

        //Invalid peekedPattern
        peekedPatternField.set(FourBitTwoDisclosureDeviceUnlocker.class, null);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == statePeeked);

        //Invalid change bit
        peekedPatternField.set(FourBitTwoDisclosureDeviceUnlocker.class, "?--?");
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        changeBitToField.set(FourBitTwoDisclosureDeviceUnlocker.class, 'G');
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == statePeeked);
    }

    @Test
    public void testValidDoPokeCalls() throws Exception {
        int statePeeked = 3;
        int statePoked = 4;
        String requestPattern = "?--?";
        Device device = new Device();

        Field devField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("dev");
        devField.setAccessible(true);
        devField.set(FourBitTwoDisclosureDeviceUnlocker.class, device);

        Field stateField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);

        Field peekedPatternField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("peekedPattern");
        peekedPatternField.setAccessible(true);
        peekedPatternField.set(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);

        Field changeBitToField = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredField("changeBitTo");
        changeBitToField.setAccessible(true);

        Method doPoke = FourBitTwoDisclosureDeviceUnlocker.class.getDeclaredMethod("doPoke");
        doPoke.setAccessible(true);

        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        int state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == statePoked);


        peekedPatternField.set(FourBitTwoDisclosureDeviceUnlocker.class, requestPattern);
        stateField.set(FourBitTwoDisclosureDeviceUnlocker.class, statePeeked);
        doPoke.invoke(FourBitTwoDisclosureDeviceUnlocker.class);
        state = (int) stateField.get(FourBitTwoDisclosureDeviceUnlocker.class);
        Assert.assertTrue(state == statePoked);
    }
}

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.spy;

public class DeviceTest {
    @Test
    public void linearTest() {
        Constructor polynomial = null;
        try {
            polynomial = TestingDevice.class.getDeclaredConstructor(boolean[].class,int.class,int.class,int.class);
            polynomial.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        boolean[] initialBits = {true,true,false,true};
        try {
            for (int i = 0; i < initialBits.length * 2; i++) {
                TestingDevice dev = (TestingDevice) polynomial.newInstance(initialBits, 2, i, 0);
                Method superPeek = dev.getClass().getDeclaredMethod("superPeek");
                superPeek.setAccessible(true);
                for (int k = 0; k < initialBits.length; k++) {
                    dev.spin();
                }
                Field spins;
                try {
                    spins = dev.getClass().getDeclaredField("spins");
                    spins.setAccessible(true);
                    System.out.println("spins: " + spins.getInt(dev));
                } catch (Exception e) {
                    e.printStackTrace();
                    fail();
                }
                System.out.println(superPeek.invoke(dev));
                assert (superPeek.invoke(dev).equals("[T, T, F, T]"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void polynomialTest() {
        Constructor polynomial = null;
        try {
            polynomial = TestingDevice.class.getDeclaredConstructor(boolean[].class,int.class, int.class, int.class);
            polynomial.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        boolean[] initialBits = {true,true,false,true};
        boolean somePolynomialSpinsDoNotResetAfterSizeSpins = false;
        try {
            for (int i = 1; i < initialBits.length * 2; i++) {
                for (int j = 1; j < initialBits.length * 2; j++) {
                    System.out.println(i + ", " + j);
                    TestingDevice dev = (TestingDevice) polynomial.newInstance(initialBits, 2, i, j);
                    Method superPeek = dev.getClass().getDeclaredMethod("superPeek");
                    superPeek.setAccessible(true);
                    for (int k = 0; k < 5; k++) {
                        dev.spin();
                    }
                    Field spins;
                    try {
                        spins = dev.getClass().getDeclaredField("spins");
                        spins.setAccessible(true);
                        System.out.println("spins: " + spins.getInt(dev));
                    } catch (Exception e) {
                        fail();
                        e.printStackTrace();
                    }
                    System.out.println(superPeek.invoke(dev));
                    if (!superPeek.invoke(dev).equals("[T, T, F, T]")) somePolynomialSpinsDoNotResetAfterSizeSpins = true;
                }
            }
            assert (somePolynomialSpinsDoNotResetAfterSizeSpins);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void randomTest() {
        boolean[] initialBits = {true,true,false,true};
        ArrayList<String> possibleConfigurations = new ArrayList<>();
        possibleConfigurations.add("[T, T, F, T]");
        possibleConfigurations.add("[T, F, T, T]");
        possibleConfigurations.add("[F, T, T, T]");
        possibleConfigurations.add("[T, T, T, F]");
        Constructor polynomial = null;
        try {
            polynomial = TestingDevice.class.getDeclaredConstructor(boolean[].class,int.class, boolean.class);
            polynomial.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            TestingDevice dev = (TestingDevice) polynomial.newInstance(initialBits, 2, true);
            Method superPeek = dev.getClass().getDeclaredMethod("superPeek");
            superPeek.setAccessible(true);

            dev.spin();
            int lastIndex = possibleConfigurations.indexOf(superPeek.invoke(dev));
            ArrayList<Integer> offsets = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                dev.spin();
                int offset = Math.abs(lastIndex - possibleConfigurations.indexOf(superPeek.invoke(dev)));
                offset = offset % 4;
                if (!offsets.contains(offset)) offsets.add(offset);
                lastIndex = possibleConfigurations.indexOf(superPeek.invoke(dev));
            }
            System.out.println(offsets);
            assert (offsets.size() > 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}

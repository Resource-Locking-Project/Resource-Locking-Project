import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.spy;

public class DeviceTest {
    @Test
    public void linearTest() {
        boolean[] initialBits = {true,true,false,true};
        for (int i = 0; i < initialBits.length * 2; i++) {
            Device dev = new Device(initialBits,2,i,0);
            for (int k = 0; k < initialBits.length; k++) {
                dev.spin();
            }
            Field spins = null;
            try {
                spins = dev.getClass().getDeclaredField("spins");
                spins.setAccessible(true);
                if (spins != null) System.out.println("spins: " + spins.getInt(dev));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
            System.out.println(dev.superPeek());
            assert(dev.superPeek().equals("[T, T, F, T]"));
        }
    }
    @Test
    public void polynomialTest() {
        boolean[] initialBits = {true,true,false,true};
        boolean somePolynomialSpinsDoNotResetAfterSizeSpins = false;
        for (int i = 1; i < initialBits.length * 2; i++) {
            for (int j = 1; j < initialBits.length * 2; j++) {
                    System.out.println(i + ", " + j);
                    Device dev = new Device(initialBits, 2, i, j);
                    for (int k = 0; k < 5; k++) {
                        dev.spin();
                    }
                    Field spins = null;
                    try {
                        spins = dev.getClass().getDeclaredField("spins");
                        spins.setAccessible(true);
                        if (spins != null) System.out.println("spins: " + spins.getInt(dev));
                    } catch (Exception e) {
                        fail();
                        e.printStackTrace();
                    }
                    System.out.println(dev.superPeek());
                    if (!dev.superPeek().equals("[T, T, F, T]")) somePolynomialSpinsDoNotResetAfterSizeSpins = true;
                }
        }
        assert(somePolynomialSpinsDoNotResetAfterSizeSpins);
    }
    @Test
    public void randomTest() {
        boolean[] initialBits = {true,true,false,true};
        ArrayList<String> possibleConfigurations = new ArrayList<String>();
        possibleConfigurations.add("[T, T, F, T]");
        possibleConfigurations.add("[T, F, T, T]");
        possibleConfigurations.add("[F, T, T, T]");
        possibleConfigurations.add("[T, T, T, F]");
        Device dev = new Device(initialBits,2,true);
        dev.spin();
        int lastIndex = possibleConfigurations.indexOf(dev.superPeek());
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            dev.spin();
            int offset = Math.abs(lastIndex - possibleConfigurations.indexOf(dev.superPeek()));
            offset = offset % 4;
            if (!offsets.contains(offset)) offsets.add(offset);
            lastIndex = possibleConfigurations.indexOf(dev.superPeek());
        }
        System.out.println(offsets);
        assert(offsets.size() > 1);
    }
}

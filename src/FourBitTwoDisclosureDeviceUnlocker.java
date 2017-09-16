import javax.sound.midi.Sequence;
import java.util.List;

/**
 * Solution development for 4-bit/2-disclosure device.
 * @author Kendra Lamb
 * @author Daniel Dews
 * @author Hoyt Andres
 * @author Alice Rowan
 * @author Maxwell Stark
 */
public class FourBitTwoDisclosureDeviceUnlocker extends DeviceUnlocker {

    /**State before device is created, spun, poked, or peeked */
    private static final int STATE_NOT_CREATED = 0;

    /**State after device is created */
    private static final int STATE_CREATED = 1;

    /**State after device is spun.*/
    private static final int STATE_SPUN = 2;

    /**State after device is peeked */
    private static final int STATE_PEEKED = 3;

    /**State after device is poked */
    private static final int STATE_POKED = 4;

    /**Holds the state of the unlock.*/
    private static int state = STATE_NOT_CREATED;

    /**
     * Static device to unlock
     */
    private static Device dev;

    /** Pattern requested from doPeek. */
    private static CharSequence peekedPattern = null;

    /** Char we are changing device bits to default to 'T'*/
    private static char changeBitTo = 'T';

    /** Number of Bits for Device */
    private static final int numOfBits = 4;

    /** Number of bits that are disclosed*/
    private static final int numOfBitsDisclosed = 2;

    /** Log of all SPIN/PEEK/POKE actions performed. */
    private static StringBuilder traceLog = new StringBuilder();

    /**
     * Unlocks a device-controlled resource.
     * This method must be guaranteed to halt, regardless of
     * whether or not it successfully unlocked the resource.
     * @param dev the device controlling the resource to unlock
     * @return true if the resource is unlocked (all bits in the
     *         device are now identical); false otherwise
     */
    public static boolean unlock(final Device dev) {
        if (dev != null) {
            state = STATE_CREATED;
        }
        FourBitTwoDisclosureDeviceUnlocker.dev = dev;
        boolean isUnlocked = doSpin();
        List<CharSequence> perms = getPermutations();
        int n = numOfBits;
        while ((!isUnlocked) && (n > 0)) {
            for (CharSequence perm : perms) {
                doPeek(perm);
                doPoke();
                isUnlocked = doSpin(n);
                if (isUnlocked) {
                    break;
                }
            }
            n--;
        }
        return false;
    }

    /**
     * Retrieve trace of previous unlock process.
     * @return rendering of steps in the unlock process
     */
    public static String showTrace() {
      return traceLog.toString();
    }

    /**
     * Request's the device to spin the bits in a circular pattern. Pattern spins to the right.
     * Valid spin command:
     *  1) Can be called after device creation, after doPoke, after doPeek, after doSpin.
     * Result of an invalid doSpin is unspecified - our implementation will log an invalid doSpin
     * with unlock's current state.
     * @param numOfSpins number of spins requested
     * @return true if all bits are the same value. False if values are different.
     */
    private static boolean doSpin(final int numOfSpins) {
        boolean result = false;

        if(isValidSpin(numOfSpins)) {
            for (int i = 0; i < numOfSpins; i++) {
                appendTrace("spin : performing a spin");
                result = dev.spin();

                if (result) {
                    break;
                }
            }
            state = STATE_SPUN;
        } else {
          appendTrace("doSpin : Spin is not valid");
        }

        return result;
    }

    /**
     * Checks current state and spins the device once.
     * @return result of spin
     */
    private static boolean doSpin() {
        boolean result;
        if(state != STATE_NOT_CREATED) {
            result = dev.spin();
            appendTrace("spin : performing a spin");
        } else {
            appendTrace("doSpin : Spin not valid: State is NOT_CREATED");
            result = false;
        }
        return  result;
    }

    /**
     * Checks if the current state is valid. Also checks if the number of Spins
     * is greater than 0.
     * @param numOfSpins Amount of times wished to spin
     * @return if the spin call can continue
     */
    private static boolean isValidSpin(final int numOfSpins) {
        boolean continueSpin;

        if(numOfSpins <= 0) {
            appendTrace("Num of spins is negative, cannot spin a negative amount of times.");
            continueSpin = false;
        } else if(state == STATE_NOT_CREATED) {
            appendTrace("Invalid state for spin - no device is created");
            continueSpin = false;
        } else {
            continueSpin = true;
        }
        return continueSpin;
    }

    /**
     * Peeks at the requested Pattern in format like: ?--?, ?-?-, ??--, etc.
     * Once peeked doPeek returns the pattern with the values included
     * A valid doPeek call is met under these conditions:
     *  1) doPeeked is called after a valid SPIN call.
     *  2) The number of bit locations disclosed is exactly equal to the number
     *      of observable bit locations
     * The result of an invalid doPeek is unspecified - our implementation will log
     * and invalid peek with the current state of unlock.
     * @param pattern to view two bits
     * @return the pattern given with the '?' replaced by peeked values(T/F)
     */
    private static CharSequence doPeek(final CharSequence pattern) {
        CharSequence returnPattern;
        boolean validPattern = isPeekValid(pattern);

        if(validPattern) {
            appendTrace("peek : with pattern", pattern);
            returnPattern = dev.peek(pattern);
            peekedPattern = returnPattern;
            appendTrace("return peekedPattern", returnPattern);
            state = STATE_PEEKED;
        } else {
            appendTrace("doPeek : invalid doPeek call with bits", pattern);
            returnPattern = pattern;
        }

        return returnPattern;
    }

    private static boolean isPeekValid(CharSequence pattern) {
        boolean validLength = false;
        boolean validRequestPattern = false;
        if(pattern != null) {
            int patternLength = pattern.length();
            validLength = patternLength == numOfBits;

            int countOfRequestedBits = 0;
            for(int i = 0; i < patternLength; i++) {
                char bit = pattern.charAt(i);
                if(bit == '?') {
                    countOfRequestedBits++;
                }
            }

            validRequestPattern = countOfRequestedBits == numOfBitsDisclosed;
        }

        return validLength && validRequestPattern && state == STATE_SPUN;
    }

    /**
     * Modifies the state of the device pattern adhering to peekedPattern.
     * Can only modify the states of the pattern viewed by Peek.
     * doPoke can only be called under conditions:
     *  1) doPoke issued immediately following valid PEEK command
     *  2) The pattern parameter specifies T or F in each of the positions '?' in
     *      the previous doPeek request pattern.
     *  And invalid doPoke command is unspecified - in our implementation it will
     *  log 'invalid poke' and the current state unlock is in.
     */
    private static void doPoke() {
        if(isValidPoke()) {
            CharSequence patternToPoke = getPokedPattern();
            appendTrace("poke : Poking with pattern:", patternToPoke);
            dev.poke(patternToPoke);
            state = STATE_POKED;
        }
    }

    private static boolean isValidPoke() {
        boolean isValid;
        String validBits = "TF";
        if(state != STATE_PEEKED){
            appendTrace("isValidPoke : Poke is not valid, current state does not equal STATE_PEEKED");
            isValid = false;
        } else if(!validBits.contains(String.valueOf(changeBitTo))) {
            appendTrace("isValidPoke : Bit to change to is invalid: ", String.valueOf(changeBitTo));
            isValid = false;
        } else if(peekedPattern == null) {
            appendTrace("isValidPoke : Peeked Pattern is null and cannot determine poke pattern", peekedPattern);
            isValid = false;
        } else {
            isValid = true;
        }

        return isValid;
    }

    private static CharSequence getPokedPattern() {
        StringBuilder newPattern = new StringBuilder();
        for(int i = 0; i < peekedPattern.length(); i++) {
            char bit = peekedPattern.charAt(i);

            if(bit == '?') {
                newPattern.append(changeBitTo);
            } else {
                newPattern.append(bit);
            }
        }
        return newPattern.toString();
    }

    private static List<CharSequence> getPermutations()
    {
        return null;
    }

    /**
     * Appends a specified message to the trace log in DeviceUnlocker
     * @param message the message to be appended.
     */
    private static void appendTrace(final String message) {
        traceLog.append(message);
        traceLog.append("\n");
    }

    /**
     * Appends a specified message to trace log in DeviceUnlocker, but allowing bits to be formatted
     * in the message.
     * @param methodCallMessage Message for the method call
     * @param deviceBits the bits in the device returned.
     */
    private static void appendTrace(String methodCallMessage, CharSequence deviceBits) {
        // Produce a message that looks like:
        //   [string...] (T - F -  ... - T - F)\n
        traceLog.append(methodCallMessage);
        traceLog.append(" (");
        for(int i = 0; i < deviceBits.length(); i++) {
            if (i > 0) {
                traceLog.append(" - ");
            }
            traceLog.append(deviceBits.charAt(i));
        }
        traceLog.append(")\n");
    }
}

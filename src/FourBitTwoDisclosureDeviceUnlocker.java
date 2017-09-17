import java.util.ArrayList;
import java.util.List;

/**
 * Solution development for 4-bit/2-disclosure device.
 * @author Kendra Lamb
 * @author Daniel Dews
 * @author Hoyt Andres
 * @author Alice Rowan
 * @author Maxwell Stark
 * @version 4.1.4.1
 * @see <a href="../projectDescription.html">Project Description</a>
 */
public class FourBitTwoDisclosureDeviceUnlocker extends DeviceUnlocker {


    /**char representing true in device.**/
    private static final char TRUE = 'T';

    /**char representing false in device.**/
    private static final char FALSE = 'F';

    /**State before device is created, spun, poked, or peeked. */
    private static final int STATE_NOT_CREATED = 0;

    /**State after device is created. */
    private static final int STATE_CREATED = 1;

    /**State after device is spun.*/
    private static final int STATE_SPUN = 2;

    /**State after device is peeked. */
    private static final int STATE_PEEKED = 3;

    /**State after device is poked. */
    private static final int STATE_POKED = 4;

    /**Holds the state of the unlock.*/
    private static int state = STATE_NOT_CREATED;

    /**
     * Static device to unlock.
     */
    private static Device dev;

    /** Pattern requested from doPeek. */
    private static CharSequence peekedPattern = null;

    /** Char we are changing device bits to default to 'T'.*/
    private static char changeBitTo = TRUE;

    /** Number of Bits for Device. */
    private static final int NUM_OF_BITS = 4;

    /** Number of bits that are disclosed.*/
    private static final int NUM_OF_BITS_DISCLOSED = 2;

    /** Log of all SPIN/PEEK/POKE actions performed. */
    private static StringBuilder traceLog = new StringBuilder();

    /**
     * Unlocks a resource controlled by a 4-bit/2-disclosed device. Behavior is unspecified if parameter is not a reference to a valid 4-bit/2-disclosure device.
     * @param dev the device controlling the resource to unlock; must be a 4-bit device with 2 peek/poke bits.
     * @return true if the resource is unlocked (all bits in the device are now identical); false otherwise
     */
    public static boolean unlock(final Device dev) {
        if (dev == null) {
            return false;
        } else {
            state = STATE_CREATED;
        }
        clearTrace();
        FourBitTwoDisclosureDeviceUnlocker.dev = dev;
        boolean isUnlocked = doSpin();
        List<CharSequence> perms = getPermutations();
        int n = 1;
        while ((!isUnlocked) && (n <= NUM_OF_BITS)) {
            for (CharSequence perm : perms) {
                doPeek(perm);
                doPoke();
                isUnlocked = doSpin(n);
                if (isUnlocked) {
                    break;
                }
            }
            n++;
        }
        if (!isUnlocked) {
            n = NUM_OF_BITS;
            while (n > 0) {
                for (CharSequence perm : perms) {
                    doPeek(perm);
                    doPoke();
                    // we want a random number of spins
                    // from 1 to the number of bits.
                    // 0 spins is invalid doSpin()
                    isUnlocked = doSpin((int) Math.round(Math.random() * NUM_OF_BITS) + 1);
                    if (isUnlocked) {
                        break;
                    }
                }
                n--;
            }
        }
        if (isUnlocked) {
            appendTrace("device is unlocked");
        }
        return isUnlocked;
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
        if (isValidSpin(numOfSpins)) {
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
        if (isValidSpin(1)) {
            result = dev.spin();
            appendTrace("spin : performing a spin");
            state = STATE_SPUN;
        } else {
           result = false;
        }
        return result;
    }

    /**
     * Checks if the current state is valid. Also checks if the number of Spins
     * is greater than 0.
     * @param numOfSpins Amount of times wished to spin
     * @return if the spin call can continue
     */
    private static boolean isValidSpin(final int numOfSpins) {
        boolean continueSpin;
        if (dev == null) {
            appendTrace("doSpin : device is null");
            continueSpin = false;
        } else if (numOfSpins <= 0) {
            appendTrace("doSpin : Num of spins is negative, cannot spin a negative amount of times.");
            continueSpin = false;
        } else if (state == STATE_NOT_CREATED) {
            appendTrace("doSpin : Invalid state for spin - no device is created");
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
        if (isPeekValid(pattern)) {
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

    /**
     * Returns whether or not pattern is valid.
     * @param pattern to validate
     * @return boolean value representing if pattern is valid
     */
    private static boolean isPeekValid(final CharSequence pattern) {
        boolean validLength = false;
        boolean validRequestPattern = false;
        boolean deviceNotNull = false;
        if (dev != null && pattern != null) {
            deviceNotNull = true;
            int patternLength = pattern.length();
            validLength = patternLength == NUM_OF_BITS;

            int countOfRequestedBits = 0;
            for (int i = 0; i < patternLength; i++) {
                char bit = pattern.charAt(i);
                if (bit == '?') {
                    countOfRequestedBits++;
                }
            }

            validRequestPattern = countOfRequestedBits == NUM_OF_BITS_DISCLOSED;
        }

        return deviceNotNull && validLength && validRequestPattern && state == STATE_SPUN;
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
        if (isValidPoke()) {
            CharSequence patternToPoke = getPokedPattern();
            appendTrace("poke : Poking with pattern:", patternToPoke);
            dev.poke(patternToPoke);
            state = STATE_POKED;
        }
    }

    /**
     * Returns boolean value representing validity of last peek request.
     * @return true if the last peek CharSequence pattern was valid to poke, false if the last peek CharSequence pattern was invalid to poke
     */
    private static boolean isValidPoke() {
        boolean isValid;
        String validBits = "TF";
        if (state != STATE_PEEKED) {
            appendTrace("isValidPoke : Poke is not valid, current state does not equal STATE_PEEKED");
            isValid = false;
        } else if (!validBits.contains(String.valueOf(changeBitTo))) {
            appendTrace("isValidPoke : Bit to change to is invalid: ", String.valueOf(changeBitTo));
            isValid = false;
        } else if (peekedPattern == null) {
            appendTrace("isValidPoke : Peeked Pattern is null and cannot determine poke pattern", peekedPattern);
            isValid = false;
        } else {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Returns sequence of characters based on the last Peek by converting everything to ChangeBitTo.
     * @return CharSequence representing TRUE or FALSE, represented by ChangeBitTo, in respective places where a ? appeared in CharSequence used in last called doPeek
     */
    private static CharSequence getPokedPattern() {
        StringBuilder newPattern = new StringBuilder();
        for (int i = 0; i < peekedPattern.length(); i++) {
            char bit = peekedPattern.charAt(i);

            // if the bit peeked was a T or F...
            if (bit == TRUE || bit == FALSE) {
                // change to something else.
                newPattern.append(changeBitTo);
            } else {
                newPattern.append(bit);
            }
        }
        return newPattern.toString();
    }
    /**
     * Get a list of possible permutations for a valid doPeek.
     * @return list of CharSequences that can be used for a valid doPeek
     */
    private static List<CharSequence> getPermutations() {
        List<CharSequence> permutations = new ArrayList<CharSequence>();
        permutation(0, 0, new StringBuilder(), permutations);
        return permutations;
    }

    /**
     * 0-1 Knapsack algorithm to grab permutations of valid peeks.
     * @param index current position in the generated CharSequence
     * @param used number of peek '?' characters used in this generated CharSequence
     * @param accumulator the accumulated characters generating this CharSequence
     * @param perms the list of permutations to add a generated CharSequence to
     */
    private static void permutation(final int index, final int used, final StringBuilder accumulator, final List<CharSequence> perms) {
        // return if we have reached the end of this permutation
        if (accumulator.length() >= NUM_OF_BITS) {
            if (used == NUM_OF_BITS_DISCLOSED) {
                perms.add(accumulator.toString());
            }
            return;
        }

        // make a copy for unique permutations.
        StringBuilder copy = new StringBuilder(accumulator);

        // use up a ?
        if (used < 2) {
            copy.append("?");
            permutation(index + 1, used + 1, copy, perms);
        }
        // do not use a ?
        accumulator.append("-");
        permutation(index + 1, used, accumulator, perms);
    }
    /**
     * Clears trace log.
     */
    private static void clearTrace() {
        traceLog = new StringBuilder();
    }
    /**
     * Appends a specified message to the trace log in DeviceUnlocker.
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
    private static void appendTrace(final String methodCallMessage, final CharSequence deviceBits) {
        // Produce a message that looks like:
        //   [string...] (T - F -  ... - T - F)\n
        if (deviceBits != null) {
            traceLog.append(methodCallMessage);
            traceLog.append(" (");
            for (int i = 0; i < deviceBits.length(); i++) {
                traceLog.append(deviceBits.charAt(i));
            }
            traceLog.append(")\n");
        }
    }
}


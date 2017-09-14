import java.util.ArrayList;
import java.util.List;

public class Device {
    private Link<Character> head;
    /**
     * A list of requested positions from the last peek method
     */
    private List<Integer> requestedPositions;
    /**
     * Whether or not we are using pseudo-random number generators for arbitrary rotations per spin
     */
    private boolean random = false;
    /**
     * Default number of bits to reveal per peek
     */
    public static final int DEFAULT_PEEKS = 2;
    /**
     * Default number of bits stored.
     */
    public static final int DEFAULT_SIZE = 4;
    /**
     * Character indicator of false.
     */
    public static final char VALUE_FALSE = 'F';
    /**
     * Character indicator of true.
     */
    public static final char VALUE_TRUE = 'T';
    /**
     * State indicating that this device was just spun.
     */
    private static final int STATE_SPUN = 1;
    /**
     * State indicating that this device was just peeked.
     */
    private static final int STATE_PEEKED = 2;
    /**
     * State indicating that this device was just poked.
     */
    private static final int STATE_POKED = 3;
    /**
     * The actual number of rotations that are done per spin.
     */
    private int numRotatesPerSpin = 1;
    /**
     * The increment multiplier for number of rotations per spin.
     */
    private int rotatesPerSpinMultiplier = 0;
    /**
     *  The number of bits we can peek
     */
    public int bitsPerPeek;
    /**
     * The number of spins that have been done
     */
    private int spins = 0;
    /**
     * The number of bits stored
     */
    public int size;
    /**
     * The current state of the device.
     */
    private int state = 0;

    /**
     * Accumulator for number of spins to do.
     */
    private int accumulator = 0;
    /**
     * For testing, create a device and set its linear or polynomial turns relationship
     * @param initialBits the bit values for this test device
     * @param bitsPerPeek the number of bits to disclose via peek or set via poke
     * @param numRotatesPerSpin number of times to rotate per spin
     * @param rotatesPerSpinMultiplier a multiple we add to the numRotatesPerSpin each time spin is called to emulate polynomial rotations
     */
    public Device(boolean[] initialBits, int bitsPerPeek,  int numRotatesPerSpin, int rotatesPerSpinMultiplier) {
        this(initialBits, bitsPerPeek);
        this.numRotatesPerSpin = numRotatesPerSpin;
        this.rotatesPerSpinMultiplier = rotatesPerSpinMultiplier;
    }

    /**
     * For testing, create a device is either random or not, and with a numRotatesPerSpin rotations per spin, with rotatesPerSpinMultiplier polynomial rotations
     * @param isRandom the number of bits to disclose via peek or set via poke
     * @param numRotatesPerSpin number of times to rotate per spin
     * @param rotatesPerSpinMultiplier a multiple we add to the numRotatesPerSpin each time spin is called to emulate polynomial rotations
     */
    public Device(boolean isRandom, int numRotatesPerSpin, int rotatesPerSpinMultiplier) {
        this();
        this.random = isRandom;
        this.numRotatesPerSpin = numRotatesPerSpin;
        this.rotatesPerSpinMultiplier = rotatesPerSpinMultiplier;
    }
    /**
     * Construct device with specified bits for testing. Initial bit values are represented by an array of boolean primitives.
     * @param initialBits the bit values for this test device
     * @param bitsPerPeek the number of bits to disclose via peek or set via poke
     */
    public Device(boolean[] initialBits, int bitsPerPeek) {
        createLinks(initialBits);
        this.bitsPerPeek = bitsPerPeek;
        this.size = initialBits.length;
    }

    /**
     * Create a device that is pseudo random in rotations
     * @param initialBits the bit values for this test device
     * @param bitsPerPeek the number of bits to disclose via peek or set via poke
     * @param isRandom whether or not this device uses pseudo-random rotations per spin
     */
    public Device(boolean[] initialBits, int bitsPerPeek, boolean isRandom) {
        this(initialBits,bitsPerPeek);
        this.random = isRandom;
    }

    /**
     * Create a device with a choice of pseudo randomness
     * @param isRandom whether or not to use pseudo-random rotations per spin
     */
    public Device(boolean isRandom) {
        this();
        this.random = isRandom;
    }
    /**
     * Construct device with specified size and number of peek/poke bits.
     * @param size the number of bits stored in this device
     * @param bitsPerPeek the number of bits to disclose via peek or set via poke
     */
    public Device(int size, int bitsPerPeek) {
        this.size = size;
        boolean[] initialBits = new boolean[this.size];
        for (int i = 0; i < size; i++) {
            if (Math.random() > 0.5) initialBits[i] = true;
        }
        if (Math.random() > 0.5) rotatesPerSpinMultiplier = (int)(Math.random() * 10);
        this.bitsPerPeek = bitsPerPeek;
        setSpinner();
        createLinks(initialBits);
    }

    /**
     * Construct device using defaults.
     */
    public Device() {
        this(DEFAULT_SIZE,DEFAULT_PEEKS);
        setSpinner();
    }

    /**
     * Create the linked list to store the bits used in the locking of this device.
     * @param initialBits
     */
    private void createLinks(boolean[] initialBits) {
        setSpinner();
        if (initialBits.length < 1) return;
        char firstValue = VALUE_FALSE;
        if (initialBits[0]) firstValue = VALUE_TRUE;
        Link first = new Link(firstValue);
        Link last = first;
        for (int i = 1; i < initialBits.length; i++) {
            boolean bit = initialBits[i];
            Link<Character> link = null;
            if (bit) link = new Link<Character>(VALUE_TRUE);
            else link = new Link<Character>(VALUE_FALSE);
            last.add(link);
            last = link;
        }
        last.add(first);
        head = first;
    }

    /**
     * Set the number of arbitrary spins for the next spin.
     */
    private void setSpinner() {
        this.numRotatesPerSpin = (int)Math.round(Math.random() * size);
        if (Math.random() > 0.5) rotatesPerSpinMultiplier = (int)Math.round(Math.random() * size);
    }

    /**
     * Print out the bits of this device.
     */
    private void print() {
        Link current = head;
        System.out.print("[");
        do {
            System.out.print(current.data());
            if (current.next != head) System.out.print(", ");
            current = current.next;
        } while (current != head);
        System.out.println("]");
    }

    /**
     * A string representing the bits in this device
     * @return A string showign all bits in this device for testing.
     */
    protected String superPeek() {
        Link current = head;
        StringBuilder out = new StringBuilder("[");
        do {
            out.append(current.data());
            if (current.next != head) out.append(", ");
            current = current.next;
        } while (current != head);
        out.append("]");
        return out.toString();
    }

    /**
     * Render device information as a string.
     * @return rendering that reveals partial state
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Device[size: ");
        out.append(size);
        out.append(", bitsPerPeek: ");
        out.append(bitsPerPeek);
        out.append("]");
        return out.toString();
    }

    /**
     * Initiate device rotation.
     * @return true if all bits have identical value; false otherwise
     */
    public boolean spin() {
        state = STATE_SPUN;
        boolean all_true = true;
        boolean all_false = true;
        Link current = head;
        do {
            if (current.data().equals(VALUE_TRUE)) all_false = false;
            else if (current.data().equals(VALUE_FALSE)) all_true = false;
            current = current.next;
        } while (current != head);
        if (all_false || all_true) return true;
        spins++;
        int i = 1;
        if (random) accumulator = (int)(Math.random() * size);
        accumulator += spins * rotatesPerSpinMultiplier;
        while (i < accumulator % size) {
            head = head.next;
            i++;
        }
        //System.out.println("spun " + (numRotatesPerSpin % size) + " times");
        //System.out.println(superPeek());
        return all_true || all_false;
    }

    /**
     * Deletes all occurrences of substring in StringBuilder
     * @param q The StringBuilder instance to remove found substrings from
     * @param what The substring to remove
     */
    private void del(StringBuilder q, String what) {
        int f = -1;
        while ((f = q.indexOf(what)) != -1) { q.delete(f,f +1); }
    }

    /**
     * Peek at bits of device.
     * @param pattern indicating which bits to show as '?'
     * @return a pattern that discloses the values of the indicated bits
     */
    public CharSequence peek(CharSequence pattern) {
        if (state != STATE_SPUN) return null;
        state = STATE_PEEKED;
        if (pattern.length() != size) throw new IllegalArgumentException("pattern must be exactly " + size + " characters long");
        requestedPositions = new ArrayList<Integer>();
        StringBuilder q = new StringBuilder(pattern);
        int numPeeked = 0;
        int f = -1;
        Link c = head;
        StringBuilder out = new StringBuilder("");
        for (int i = 0; i < q.length() && i < size; i++) {
            if (numPeeked < bitsPerPeek && q.charAt(i) == '?') {
                requestedPositions.add(i);
                out.append(c.data());
                numPeeked++;
            }
            else out.append("-");
            c = c.next;
        }
        return out.toString();
    }

    /**
     * Poke bits into device.
     * @param pattern indicator of values of bits to poke
     */

    public void poke(CharSequence pattern) {
        if (state != STATE_PEEKED) return;
        state = STATE_POKED;
        if (pattern.length() != size) throw new IllegalArgumentException("pattern must be exactly " + size + " characters long");
        StringBuilder q = new StringBuilder(pattern);
        int numPoked = 0;
        int f = -1;
        Link c = head;
        for (int i = 0; i < q.length() && i < size; i++) {
            // they requested this location in their last peek
            if (requestedPositions.contains(i)) {
                // and it is within the limit of peeks
                if (numPoked < bitsPerPeek && (q.charAt(i) == VALUE_TRUE || q.charAt(i) == VALUE_FALSE)) {
                    numPoked++;
                    c.setData(q.charAt(i));
                }
            }
            c = c.next;
        }
    }
}

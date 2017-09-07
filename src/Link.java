public class Link<Type> {
    /**
     * The next link in this linked-list
     */
    protected Link next;
    /**
     * The data this link holds
     */
    private Type data;

    /**
     * Create a new link with data.
     * @param data data to be stored in this link
     */
    protected Link(Type data) {
        this.data = data;
    }

    /**
     * Append a link to the end of this one.
     * @param other the link to append
     */
    protected void add(Link other) {
        next = other;
    }

    /**
     * Retrieve the data stored in this link
     * @return the data stored in this link
     */
    protected Type data() {
        return this.data;
    }

    /**
     * Set the data in this link
     * @param data the data in this link
     */
    protected void setData(Type data) {
        this.data = data;
    }
}

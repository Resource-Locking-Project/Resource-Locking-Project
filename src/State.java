public enum State {
    NOTCREATED("NOT CREATED"),
    CREATED("CREATED"),
    SPUN("SPUN"),
    POKED("POKED"),
    PEEKED("PEEKED");

    private String state;

    State(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}


class Shared {
    private boolean containsState;

    public Shared() {
        this.containsState = false;
    }

    public synchronized void setContainsState(boolean containsState) {
        this.containsState = containsState;
    }

    public synchronized boolean containsState() {
        return this.containsState;
    }
}
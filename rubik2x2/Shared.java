
class Shared {
    private boolean containsState;
    private String movesForState;

    public Shared() {
        this.containsState = false;
        this.movesForState = "";
    }

    public synchronized void containsState(boolean containsState, String moves) {
        this.containsState = containsState;
        this.movesForState = moves;
    }

    public synchronized boolean containsState() {
        return this.containsState;
    }

    public synchronized String movesForState() {
        return this.movesForState;
    }
}
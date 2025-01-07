
class Shared {
    private boolean containsState;
    private String movesForState;
    private boolean stopGenerating;

    public Shared() {
        this.containsState = false;
        this.movesForState = "";
        this.stopGenerating = false;
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

    public synchronized void stopGenerating(boolean stopGenerating) {
        this.stopGenerating = stopGenerating;
    }

    public synchronized boolean stopGenerating() {
        return this.stopGenerating;
    }
}
import java.util.ArrayList;
import java.util.List;

class Search implements Runnable {
    private LinkedList list;
    private String state;
    private boolean isFirst;

    public Search(LinkedList list, String state, boolean isFirst) {
        this.list = list;
        this.state = state;
        this.isFirst = isFirst;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (isFirst) {
                list.searchFromFirst(state);
            } else {
                list.searchFromLast(state);
            }
        }
        System.out.println(Thread.currentThread().getName() + " interrupted");
    }
}

class Node {
    public String[] value;
    public List<Node> children;
    public Node next;
    public Node prev;
    public boolean isExpanded;

    public Node(String state, String movesUsed) {
        value = new String[] { state, movesUsed };
        next = null;
        prev = null;
        children = new ArrayList<>();
        isExpanded = false;
    }
}

public class LinkedList {
    private long size = 0;
    private Node first = null;
    private Node last = null;
    Shared shared = new Shared();

    public LinkedList() {
        first = null;
        last = null;
        shared = new Shared();
    }

    public synchronized void add(String state, String movesUsed) {
        Node newNode = new Node(state, movesUsed);
        if (first == null) { // Empty list
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }
    }

    public void remove(String state) {
        Node current = first;
        while (current != null) {
            if (current.value[0] == state) {
                current.prev.next = current.next;
                current.next.prev = current.prev;
                current = null;
                break;
            }
            current = current.next;
        }
    }

    public boolean containsState(String state) {
        shared.setContainsState(false);
        Search fromFirst = new Search(this, state, true);
        Search fromLast = new Search(this, state, false);
        Thread firstT = new Thread(fromFirst, "first");
        Thread lastT = new Thread(fromLast, "last");
        firstT.start();
        lastT.start();
        while (!shared.containsState() && (firstT.isAlive() || lastT.isAlive()))
            ;
        firstT.interrupt();
        lastT.interrupt();
        return shared.containsState();
    }

    public void searchFromFirst(String state) {
        Node current = first;
        while (current != null && !shared.containsState()) {
            System.out.println(current.value[0] + "first");
            if (current.value[0] == state) {
                shared.setContainsState(true);
                break;
            }
            current = current.next;
        }
    }

    public void searchFromLast(String state) {
        Node current = last;
        while (current != null && !shared.containsState()) {
            System.out.println(current.value[0] + "last");
            if (current.value[0] == state) {
                shared.setContainsState(true);
                break;
            }
            current = current.prev;
        }
    }

    public String movesUsed(String state) {
        Node current = first;
        while (current != null) {
            if (current.value[0] == state) {
                return current.value[1];
            }
            current = current.next;
        }
        return "";
    }

    public void print() {
        Node current = first;
        while (current != null) {
            System.out.println(current.value[0]);
            current = current.next;
        }
    }

}

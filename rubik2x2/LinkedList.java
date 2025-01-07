import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            Thread.currentThread().interrupt();
        }
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

    public void clear() {
        first = null;
        last = null;
        shared = new Shared();
    }

    public boolean isEmpty() {
        return first == null ? true : false;
    }

    public void add(String state, String movesUsed) {
        Node newNode = new Node(state, movesUsed);
        if (first == null) { // Empty list
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            newNode.prev = first;
            last = newNode;
        }
    }

    public String[] dequeue() {
        if (first == null) {
            return null;
        }
        String[] result = first.value;
        if (first.next == null) {
            first = null;
        } else {
            first.next.prev = null;
            first = first.next;
        }
        return result;
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
        shared.containsState(false, "");
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
            if (current.value[0].contains(state)) {
                shared.containsState(true, current.value[1]);
                return;
            }
            current = current.next;
        }
    }

    public void searchFromLast(String state) {
        Node current = last;
        while (current != null && !shared.containsState()) {
            if (current.value[0].contains(state)) {
                shared.containsState(true, current.value[1]);
                return;
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
            System.out.println(current.value[0] + " " + current.value[1] + ",");
            current = current.next;
        }
    }

    public void fileOutput(String filename, boolean append) {
        try {
            FileWriter myWriter = new FileWriter(filename, append);
            Node current = first;
            while (current != null) {
                myWriter.write(current.value[0] + " " + current.value[1] + ",");
                current = current.next;
            }
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fileInput(boolean clearList, LinkedList list, String filename) {
        try {
            Scanner scanner = new Scanner(new FileReader(filename));
            if (clearList) {
                list.clear();
                String text = scanner.nextLine();
                String[] state = text.split(",");
                for (String s : state) {
                    String[] stateMove = s.split(" ");
                    if (stateMove.length > 0) {
                        list.add(stateMove[0], stateMove[1]);
                    }
                }
            } else {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

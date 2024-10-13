public class Queue<TreeNode> {
    private class Node<TreeNode> {
        private TreeNode value;
        private Node<TreeNode> next;
        private Node<TreeNode> prev;
    }

    private Node<TreeNode> first = null;
    private Node<TreeNode> last = null;
    private int size = 0;

    public void enqueue(TreeNode item) {
        Node<TreeNode> nn = new Node<>();
        nn.value = item;
        if (first == null) {
            first = nn;
            last = first;
        } else {
            if (last != null) {
                if (last.prev == null)
                    last.prev = nn;
            }
            nn.next = first;
            first.prev = nn;
            first = nn;
        }
        size++;
    }

    public TreeNode dequeue() throws Exception {
        if (!isEmpty() && last != null) {
            if (last == first) {
                first = null;
            }
            TreeNode tmp = last.value;
            last = last.prev;
            size--;
            return tmp;
        } else
            throw new Exception("Cannot dequeue from an empty stack.");
    }

    public TreeNode peek() throws Exception {
        if (!isEmpty())
            return last.value;
        else
            throw new Exception("Cannot peek into an empty stack.");
    }

    public TreeNode itemAt(int index) throws Exception {
        Node<TreeNode> curr = first;
        // Iterate 'index' amount of times through the stack elements and store the Node
        // at that index
        for (int i = 0; i < index; i++) {
            if (curr.next != null) {
                curr = curr.next;
            } else {
                throw new Exception("Index out of bounds");
            }
        }
        return curr.value;// return value at chosen index
    }

    public boolean contains(TreeNode item) {
        Node<TreeNode> curr = first; // Set the current node to first
        while (curr != null) { // Loop while the current node exists
            if (curr.value == item) {
                return true; // return true if the current node value is the same as item (queue contains
                             // item)
            }
            curr = curr.next; // Set the current node to the next node
        }
        return false; // queue does not contain item
    }

    public int size() {
        return size;
    }

    public void remove(TreeNode item) {
        if (first == null)
            return; // size = 0
        if (first != null && compareByteArrays(item.getValue(), first.value.getValue()) == 0) {
            // removing first
            first = first.next;
            if (first != null)
                first.prev = null; // size > 1
            size--;
            return;
        }
        Node<TreeNode> tmp = first;
        while (tmp != null && compareByteArrays(item.getValue(), tmp.value.getValue()) != 0)
            tmp = tmp.next;
        if (tmp == null)
            return; // item not found
        // else tmp points to node to be removed;
        // remove it
        tmp.prev.next = tmp.next;
        if (tmp.next == null)
            tmp.prev = null; // first end
        else
            tmp.next.prev = tmp.prev;
        size--;
    }

    public void clear() {
        size = 0;
        first = null;
        last = null;
    }

    public boolean isFull() {
        return false;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public boolean compareByteArrays(byte[] arr1, byte[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }
}
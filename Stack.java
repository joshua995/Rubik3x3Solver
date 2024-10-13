public class Stack<T> {
    private class Node<T> {
        private T value;
        private Node<T> next;
    }

    private Node<T> top = null;
    private int size = 0;

    public void push(T item) {
        Node<T> nn = new Node<T>();
        nn.value = item;
        nn.next = top;
        top = nn;
        size++;
    }

    public T pop() throws Exception {
        if (!isEmpty()) {
            T tmp = top.value;
            top = top.next;
            size--;
            return tmp;
        } else
            throw new Exception("Cannot popfrom an empty stack.");
    }

    public T peek() throws Exception {
        if (!isEmpty())
            return top.value;
        else
            throw new Exception("Cannot peek into an empty stack.");
    }

    public T itemAt(int index) throws Exception {
        Node<T> curr = top;

        // Iterate 'index' amount of times through the stack elements and store the Node
        // at that index
        for (int i = 0; i < index; i++) {
            if (curr.next != null) {
                curr = curr.next;
            } else {
                throw new Exception("Index out of bounds");
            }
        }
        return curr.value; // return value at chosen index
    }

    public int size() {
        return size;
    }

    public boolean isFull() {
        return false;
    }

    public boolean isEmpty() {
        return top == null;
    }
}

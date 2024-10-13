public class TreeNode {
    private byte[] value;
    private String moves;
    private Queue<TreeNode> childNodes;
    private TreeNode parent;

    public TreeNode(byte[] src, String moves, TreeNode parent) { // Node tracks its value, its children and its parent
        this.value = src;
        this.moves = moves;
        this.childNodes = new Queue<TreeNode>();
        this.parent = parent;
    }

    // Add a child to a node
    public void add(TreeNode childNode) {
        this.childNodes.enqueue(childNode);
    }

    // Get the value of this node
    public byte[] getValue() {
        byte[] valueCopy = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            valueCopy[i] = value[i];
        }
        return valueCopy;
    }

    public boolean contains(TreeNode item) throws Exception {
        if (compareByteArrays(value, item.getValue())) {
            return true;
        }
        recContains(this, this.getChildNodes().size());
        return false;
    }

    public boolean recContains(TreeNode node, int i) throws Exception {
        if (i == 0) {
            return false;
        }
        if (compareByteArrays(value, node.getValue())) {
            return true;
        }
        for (int j = 0; j < node.getChildNodes().size(); j++) {
            TreeNode tempNode = node.getChildNodes().itemAt(j);
            if (compareByteArrays(value, tempNode.getValue())) {
                return true;
            }
            recContains(tempNode, tempNode.getChildNodes().size());
        }
        return false;
    }

    public boolean compareByteArrays(byte[] arr1, byte[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }

    // Return a stack containing the children of this node
    public Queue<TreeNode> getChildNodes() {
        return childNodes;
    }

    // Get the parent of this node
    public TreeNode getParent() {
        return parent;
    }

    public String getMoves() {
        return moves;
    }
}

public class GenericRedBlackTree<K extends Comparable<K>, V> {
    /**
     * Root node of the red black tree.
     */
    private Node root = null;

    /**
     * Size of the tree
     */
    private static int size = 0;

    /**
     * Red/Black variables for simpler modification of node colors
     */
    private static boolean RED = GenericRedBlackTree.Node.RED;
    private static boolean BLACK = GenericRedBlackTree.Node.BLACK;

    /**
     * Return parent node of a reference node 
     * 
     * @param node {@code Node} the reference node
     * @return {@code Node} the parent of reference node
     */
    public Node getParent(Node node) { 
        return node.parent;
    }

    /**
     * Return grandparent node of a reference node 
     * 
     * @param node {@code Node} the reference node
     * @return {@code Node} the grandparent of reference node
     */
    public Node getGrandParent(Node node) {
        return node.parent.parent;
    }

    /**
     * Return sibling node of a reference node 
     * 
     * @param node {@code Node} the reference node
     * @return {@code Node} the sibling of reference node
     */
    public Node getSibling(Node node) {
        Node parent = node.parent;
        if (parent == null) {
            return null;
        }
        if (node == parent.lChild) {
            return parent.rChild;
        } else {
            return parent.lChild;
        }
    }

    /**
     * Return uncle node of a reference node 
     * 
     * @param node {@code Node} the reference node
     * @return {@code Node} the uncle of reference node
     */
    public Node getUncle(Node node) {
        Node parent = node.parent;
        Node gp = parent.parent;
        if (gp == null) {
            return null;
        }
        return getSibling(parent);
    }

    /**
     * Search for the node by key, and return the corresponding value
     * 
     * @param key {@code K} the key for searching
     * @return {@code V} the value of the node, or {@code NULL} if not found
     */
    public V find(K key) {
        Node current = root;
        while (true) {
            if (current.value == null) {
                return null;
            }
            if (current.key.compareTo(key) == 0) {
                return current.value;
            } else if (key.compareTo(current.key) < 0) { // key < current.key
                current = current.lChild;
            } else if (key.compareTo(current.key) > 0) { // key > current.key
                current = current.rChild;
            }
        }
    }

    /**
     * Insert an element to the tree, repair RBT properties after insertion, find new root 
     * 
     * @param key   {@code K} the key of the new element
     * @param value {@code V} the value of the new element
     */
    public void insert(K key, V value) {
        // System.out.println("Inserting value: " + value);
        Node node = new Node(key, value); // create node with new value

        insertRecurse(root, node); // insert as binary search tree
        insertRepairTree(node); // fix RBT properties

        // find current root after insertion
        findNewRoot(node);
        
    }    

    /**
     * find new root after operations that may change it 
     * 
     * @param node   {@code Node} starting point to work up from  
     */
    private void findNewRoot(Node node) {
        root = node;
        while (getParent(root) != null) {
            root = getParent(root);
        }
    }

    /**
     * Insert an element to the tree using binary search
     * 
     * @param rooT {@code Node} the subroot to evaluate
     * @param node {@code Node} the node being inserted
     */
    private void insertRecurse(Node rooT, Node node) {
        if (rooT == null) { // first insertion
            root = node;
            root.value = node.value;
            root.color = RED; // always insert red
        } else if (rooT != null && node.key.compareTo(rooT.key) < 0) { // node < root
            if (rooT.lChild.value != null) {
                insertRecurse(rooT.lChild, node);
                return;
            } else {
                rooT.lChild = node;
                node.parent = rooT;
                node.color = RED; // always insert red
            }
        } else if (rooT != null) {
            if (rooT.rChild.value != null) {
                insertRecurse(rooT.rChild, node);
                return;
            } else {
                rooT.rChild = node;
                node.parent = rooT;
                node.color = RED; // always insert red
            }
        }
    }

    /**
     * Search for the node by key, and return the corresponding value
     * 
     * @param node {@code Node} the reference node to be repaired around
     * @return {@code void} tree is balanced 
     */
    public void insertRepairTree(Node node) {
        // CASE 1 - first insertion
        if (node.equals(root)) {  
            node.color = BLACK;
            return;
        }

        Node parent = getParent(node);

        //CASE 2 - parent exists black
        if (parent != null && parent.color == BLACK) { 
            return;
        }
        Node uncle = getUncle(node);
        Node gp = getGrandParent(node);

        //CASE 3 - parent & uncle exist, both red 
        if (parent != null && uncle != null) { 
            if (parent.color == RED && uncle.color == RED) {
                parent.color = BLACK; // set parent & uncle black
                uncle.color = BLACK;
                if (gp != null) { // grandparent exists
                    gp.color = RED; // set gp red
                    insertRepairTree(gp); // fix around gp
                }
            }
        }

        //CASE 4 - parent exists red, uncle exists black
        if (parent != null && parent.color == RED) { 
            if (uncle != null && uncle.color == BLACK) {

                // CASE 4.1.1
                if (node.isRightChild() && parent.isLeftChild()) { // node is rChild, parent is lChild
                    rotateLeft(parent);
                    node = node.lChild;
                }

                // CASE 4.1.2
                else if (node.isLeftChild() && parent.isRightChild()) { // node is lChild, parent is rChild
                    rotateRight(parent);
                    node = node.rChild;
                }
            }

            // CASE 4.2.1 - node is lChild
            if (node.isLeftChild()) { 
                rotateRight(gp);
            }

            // CASE 4.2.2 - node is rChild 
            else {
                rotateLeft(gp);
            }
            
            gp.parent.color = BLACK;
            gp.color = RED;
        }
    }

    /**
     * Rotate the tree right around a node by reassigning each parent, lChild, rChild
     * 
     * @param node {@code Node} the node to be rotated around
     */
    public void rotateRight(Node node) {
        Node parent = node.parent;
        Node lChild = node.lChild;
        Node LR = lChild.rChild;
        node.lChild = LR;
        lChild.rChild = node;
        node.parent = lChild;

        if (node.lChild != null) {
            node.lChild.parent = node;
        }
        if (parent != null) {
            if (node == parent.lChild) {
                parent.lChild = lChild;
            } else if (node == parent.rChild) {
                parent.rChild = lChild;
            }
        }
        lChild.parent = parent;
    }

    /**
     * Rotate the tree left around a node by reassigning each parent, lChild, rChild
     * 
     * @param node {@code Node} the node to be rotated around
     */
    public void rotateLeft(Node node) {
        Node parent = null;
        if (node.parent != null) {
            parent = node.parent;
        }

        Node rChild = node.rChild;
        Node RL = rChild.lChild;

        node.rChild = RL;
        rChild.lChild = node;
        node.parent = rChild;

        if(node.rChild != null){
            node.rChild.parent = node;

        }
        if (parent != null) {
            if (node == parent.lChild) {
                parent.lChild = rChild;
            } else if (node == parent.rChild) {
                parent.rChild = rChild;
            }
        }
        rChild.parent = parent;
    }

    /**
     * Swap two nodes positions in the tree by exchanging their values 
     * 
     * @param node {@code larger} the next largest value after node to be swapped with node
     * @param node {@code node} the node to be swapped with subroot
     */
    public void swap(Node larger, Node node) {
        V v1 = larger.value;
        K k1 = larger.key;
        boolean c1 = larger.color;
        larger.color = node.color;
        node.color = c1;
        larger.key = node.key;
        larger.value = node.value;
        node.key = k1;
        node.value = v1;
    }

    /**
     * Search for the node by key, and return the corresponding Node
     * 
     * @param key {@code K} the key for searching
     * @param value {@code V} the value for searching
     * 
     * @return {@code Node} the node found
     */
    private GenericRedBlackTree<K, V>.Node findNode(K key, V value) {
        //equivalent node to find
        Node findThis = new Node(key, value);
        //start at root of tree 
        Node current = root;
        while (true) {
            // System.out.println("current: "+ current.key);
            // System.out.println("findthis: "+ findThis.key);
            if (findThis.key.compareTo(current.key) == 0) { //match
                return current;
            } else if (findThis.key.compareTo(current.key) < 0) { // node to find < current node 
                current = current.lChild;
            } else if (findThis.key.compareTo(current.key) > 0) { // node to find > current node
                current = current.rChild;
            }
        }
    }
    
    /**
     * Remove an element from the tree
     * 
     * @param key {@code K} the key of the element
     * @return {@code V} the value of the removed element
     */
    public V remove(K key) {
        //find value of key to remove
        V value = find(key);
        //get Node of key, value
        Node node = findNode(key, value);
        
        if(value == null){
            return null;
        }

        //1. if node has 2 non nil children, next larger node is left most node in rChild subtree
        if(node.lChild.value != null && node.rChild.value != null){
            Node larger = node.rChild;
            while(larger.lChild.value != null){
                larger = larger.lChild;
            }
            swap(larger, node);
            node = larger;
        }
        //2. node has at least one nil child, set node to nils sibling 
        Node child;
        if(node.lChild.value == null){
            child = node.rChild;
        }
        else{
            child = node.lChild;
        }

        //3. If node is not root (parent not null), then link child to parent;
        if(node.parent != null){
            child.parent = node.parent;
            if(node.isLeftChild()){
                node.parent.lChild = child;
            }
            else{
                node.parent.rChild = child;
            }
        }
        // 3.1 otherwise if child is NIL, then empty the tree
        else if(child.value == null){
            root = null;
            return value;
        }
        // 3.2 otherwise set root to child
        else{
            root = child;
            child.parent = null;
        }
        //4. if nodes color was black 
        if(node.color == BLACK){
            //4.1 If child’s color is red, set child’s color to black
            if(child.color == RED){
                child.color = BLACK;
            }
            //4.2 Otherwise, fix child’s color by Step 2 
            else{
                fixDelColor(child); 
            }
        }
        return value; 
    }

    private void fixDelColor(Node node) {
        //CASE 1 - node is root
        // make sure root is black, done 
        if(node == root){
            node.color = BLACK;
            return;
        }

        //CASE 2 - sibling of node is red
        // Set parent’s color to red, and sibling’s color to black 
        // Rotate left/right on sibling if node is a lChild/rChild 
        // Update sibling to node’s new sibling
        Node sibling = getSibling(node);
        if (sibling.color == RED){
            node.parent.color = RED;
            sibling.color = BLACK;
            //left child
            if(node.isLeftChild()){
                rotateLeft(node.parent); 
                findNewRoot(node);
            }
            //right child
            else{
                rotateRight(node.parent);
                findNewRoot(node);
            }
            //update sibling to new nodes sibling
            if (node.isLeftChild()) {
                sibling = node.parent.rChild;
            } else {
                sibling = node.parent.lChild;
            }
        }

        //CASE 3 - parent, sibling, sibling.lChild and sibling.rChild are all black
        // set sibling’s color to red
        // fix parent’s color - invoke fixDelColor(parent)
        if(node.parent.color == BLACK){
            if(sibling.value == null){
                sibling.color = RED;
                fixDelColor(node.parent);
            }
            else if(node.parent.color == BLACK
                && sibling.color == BLACK
                && sibling.lChild.color == BLACK
                && sibling.rChild.color == BLACK){
                    sibling.color = RED;
                    fixDelColor(node.parent);
            }
        }
        
        //CASE 4 - parent is red, sibling, sibling.lChild and sibling.rChild are all black
        // set sibling to red and parent to black
        if(node.parent.color == RED){
            if(sibling.value == null){
                sibling.color = RED;
                node.parent.color = BLACK;
            }
            else if(sibling.color == BLACK
                && sibling.lChild.color == BLACK
                && sibling.rChild.color == BLACK){
                    sibling.color = RED;
                    node.parent.color = BLACK;
            }
        }

        //CASE 5 - sibling’s color is black
        if(sibling.color == BLACK){
            sibling.color = RED;
            //5.1 - node is a lChild, sibling.lChild is red, and sibling.rChild is black
            // set sibling.lChild to black 
            // rotate right on sibling.lChild
            if(node.isLeftChild() 
                && sibling.lChild.color == RED
                && sibling.rChild.color == BLACK){
                    sibling.lChild.color = BLACK;
                    rotateRight(sibling);
                    findNewRoot(node);
            }
            //5.2 - node is a rChild, sibling.lChild is black, and siblng.rChild is red
            // set sibling.rChild to black 
            // rotate left on sibling.rChild
            if(node.isRightChild()
                && sibling.lChild.color == BLACK
                && sibling.rChild.color == RED){
                    sibling.rChild.color = BLACK;
                    rotateLeft(sibling);
                    findNewRoot(node);
            }
        }

        //CASE 6 - set sibling’s color to parent’s color, and then parent’s color to black
        sibling.color = node.parent.color;
        node.parent.color = BLACK;
        //6.1 - If node is a lChild
        // set sibling.rChild’s color to black
        // rotate left on sibling
        if(node.isLeftChild()){
            if(sibling.rChild != null){
                if(sibling.rChild.key != null){
                    sibling.rChild.color = BLACK;
                    rotateLeft(node.parent);
                    findNewRoot(node);
                }
            }
        }
        //6.2 - If node is a rChild
        // set sibling.lChild’s color to black
        // rotate right on sibling
        if(node.isRightChild()){
            if(sibling.lChild != null){
                if(sibling.lChild.key != null){
                    sibling.lChild.color = BLACK;
                    rotateRight(node.parent);
                    findNewRoot(node);
                }
            }
        }
    }

    /**
     * Get the size of the tree
     * 
     * @return {@code int} size of the tree
     */
    public int size() {
        return size;
    }

    /** 
     * In order traversal to print tree
     * 
     * all lChilds -> subroot -> rChilds
     * @return {@code String} formatted string representing the tree
     */
    public String traverse(Node node) {
        if (node == null || node.value == null) {
            return "NIL";
        } else {
            return String.format("%s = {L: %s R: %s}", node.toString(), traverse(node.lChild), traverse(node.rChild));
        }
    }

    /**
     * Recurse on root to print via traverse
     */
    @Override
    public String toString() {
        return traverse(root);
    }

    /**
     * Main entry
     * 
     * @param args {@code String[]} Command line arguments
     */
    public static void main(String[] args) {
        GenericRedBlackTree<Integer, String> rbt = new GenericRedBlackTree<Integer, String>();

        // int[] keys = new int[10];
        // for (int i = 0; i < 10; i++) {
        //     keys[i] = (int) (Math.random() * 200);
        //     System.out.println(String.format("%2d Insert: %-3d ", i + 1, keys[i]));
        //     rbt.insert(keys[i], "\"" + keys[i] + "\"");
        //     size++; 
        // }
        // System.out.println("size: "+rbt.size());
        // assert rbt.root.color == BLACK;
        // System.out.println("root: " + rbt.root);  //helps to figure out the tree structure
        // System.out.println(rbt);
        

        // for (int i = 0; i < 10; i++) {
        //     System.out.println("i: "+i);
        //     System.out.println("removing: "+keys[i]);            
        //     System.out.println(String.format("%2d Delete: %3d(%s)", i+1, keys[i], rbt.remove(keys[i])));
        //     if ((i + 1) % 5 == 0) {
        //         System.out.println(rbt);
        //     } // if ((i + 1) % 5 == 0)
        // } // for (int i = 0; i < 10; i++)

        //TESTING

        int[] testkeys = {186,78,170,132,191,102,45,28,52,158};
        for(int i = 0; i < 10; i++){
            System.out.println(String.format("%2d Insert: %-3d ", i + 1, testkeys[i]));
            rbt.insert(testkeys[i], "\"" + testkeys[i] + "\"");
            size++;
        }
        for(int i = 0; i < 10; i++){
            System.out.println(rbt);
            System.out.println(i+1 +" Deleting: " + testkeys[i]);
            rbt.remove(testkeys[i]);
            size++;
        }
    }


    /**
     * The {@code Node} class for {@code GenericRedBlackTree}
     */
    private class Node {
        public static final boolean BLACK = true;
        public static final boolean RED = false;

        public K key;
        public V value;
        public boolean color = BLACK;
        public Node parent = null, lChild = null, rChild = null;

        public Node(K key, V value) {                   // By default, a new node is black with two NIL children
            this.value = value;
            this.key = key; 
            if (value != null) {
                lChild = new Node(null, null);          // And the NIL children are both black
                lChild.parent = this;
                rChild = new Node(null, null);
                rChild.parent = this;
            }
        }

        public boolean isLeftChild() {
            return this == this.parent.lChild;
        }

        public boolean isRightChild() {
            return this == this.parent.rChild;
        }

        /**
         * Print the tree node: red node wrapped by "<>"; black node by "[]"
         * 
         * @return {@code String} The printed string of the tree node
         */
        @Override public String toString() {
            if (value == null)
                return "";
            return (color == RED) ? "<" + value + "(" + key + ")>" : "[" + value + "(" + key + "]";
        }
    }

}

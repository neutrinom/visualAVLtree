package ua.neutrino.test.avltree;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

public class AVLTreeAnimation extends JApplet {
  private AVLTree<Integer> tree = new AVLTree<Integer>();
  private AVLTree<Integer> treeCopy;
  
  public AVLTreeAnimation() {
/*	tree.insert(17);
    tree.insert(3);
    tree.insert(75);
    tree.insert(26);
    tree.insert(49);
    tree.insert(111);
    tree.insert(34);
    tree.insert(87);
    tree.insert(9); 
 */
    setUI();
  }
  
  public static void main(String[] args) {
    JFrame frame = new JFrame("AVLTreeAnimation");
    JApplet applet = new AVLTreeAnimation();
    frame.add(applet);
    frame.setSize(500, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
  
  private JButton jbtSearch = new JButton("Search");
  private JButton jbtInsert = new JButton("Insert");
  private JButton jbtDelete = new JButton("Delete");
  private JTextField jtfKey = new JTextField(5);
  private PaintTree paintTree = new PaintTree();
  
  private void setUI() {
    setLayout(new BorderLayout());
    
    add(paintTree, BorderLayout.CENTER);   
    JPanel panel = new JPanel();
    panel.add(new JLabel("Enter a key: "));
    panel.add(jtfKey);
    panel.add(jbtSearch);
    panel.add(jbtInsert);
    panel.add(jbtDelete);
    add(panel, BorderLayout.SOUTH);

    jbtSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int key = Integer.parseInt(jtfKey.getText());
        if (!tree.search(key)) {
          JOptionPane.showMessageDialog(null, key + " is not in the tree");
        }
        else {
          paintTree.setOfHighlightedNodes.clear();
          timer = new Timer(1000, new AnimationListener(null, 0));
          paths = tree.path(key);       
          timer.start();
        }
      }
    });
    
    jbtInsert.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int key = Integer.parseInt(jtfKey.getText());
        if (tree.search(key)) {
          JOptionPane.showMessageDialog(null, key + " is already in the tree");
        }
        else {
          treeCopy = (AVLTree<Integer>)(tree.clone());          
          paintTree.setOfHighlightedNodes.clear();
          paths = tree.path(key);       
          timer = new Timer(1000, new AnimationListener(key, 1));
          timer.start();
        }
      }
    });
    
    jbtDelete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int key = Integer.parseInt(jtfKey.getText());
        if (!tree.search(key)) {
          JOptionPane.showMessageDialog(null, key + " is not in the tree");
        }
        else {
          treeCopy = (AVLTree<Integer>)(tree.clone());          
          paintTree.setOfHighlightedNodes.clear();
          paths = tree.path(key);       
          timer = new Timer(1000, new AnimationListener(key, 2));
          timer.start();
        }
      }
    });
  }
  
  Timer timer;
  
  AnimationRotationAfterDeletion deleteAnimation = new AnimationRotationAfterDeletion(null);
  Timer timerForDelete = new Timer(500, deleteAnimation);  
  
  protected ArrayList<AVLTree.TreeNode<Integer>> paths = 
    new ArrayList<AVLTree.TreeNode<Integer>>();
  
  class AnimationListener implements ActionListener {
    int mode = 0; // 0 for search, 1 for insert, and 2 for delete
    Integer key = null;
    
    public AnimationListener(Integer key, int mode) {
      this.key = key;
      this.mode = mode;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {      
      if (!paths.isEmpty())
        paintTree.setOfHighlightedNodes.add(paths.remove(0));
      else {
        timer.stop();
        if (mode == 1) {
          tree.justInsert(key);
          timer = new Timer(500, new AnimationRotationAfterInsertion(key));
          timer.start();
        }
        else if (mode == 2) {
          deleteAnimation.setKey(key);
          timerForDelete.start();          
        }
      }
      
      paintTree.repaint();
    }
  }
  
  class AnimationRotationAfterInsertion implements ActionListener {
    int mode = 0; 
    Integer key = null;
    int count = 0;
    HashSet<AVLTree.TreeNode<Integer>> set1 = new HashSet<AVLTree.TreeNode<Integer>>();
    HashSet<AVLTree.TreeNode<Integer>> set2 = new HashSet<AVLTree.TreeNode<Integer>>();
    
    public void setKey(Integer key) {
      this.key = key;
    }
    
    public AnimationRotationAfterInsertion(Integer key) {
      this.key = key;     
      set2 = tree.findImbalanceSubtree(key);
      
      if (set2.size() == 0)
        count = 16;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {      
      if (count++ <= 15) {
        if (count % 2 == 0) 
          paintTree.setOfHighlightedNodes = set2;
        else
          paintTree.setOfHighlightedNodes = set1;
      }
      else {
        timer.stop();
        tree = treeCopy;
        tree.insert(key);        
      }
      
      paintTree.repaint();
    }
  }

  class AnimationRotationAfterDeletion implements ActionListener {
    int mode = 0; 
    Integer key = null;
    int count = 0;
    HashSet<AVLTree.TreeNode<Integer>> set1 = new HashSet<AVLTree.TreeNode<Integer>>();
    HashSet<AVLTree.TreeNode<Integer>> set2 = new HashSet<AVLTree.TreeNode<Integer>>();

    public void setKey(Integer key) {
      this.key = key;

      Integer startingElement = tree.findStartingNodeForDeletion(key);
      tree.justDelete(key);
      set2 = tree.findImbalanceSubtreeForDeletion(startingElement);
      
      if (set2.size() == 0) 
        count = 16;
      else
        count = 0;
    }
    
    public AnimationRotationAfterDeletion(Integer key) {
      this.key = key;     
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {      
      if (count++ <= 15) {
        if (count % 2 == 0) 
          paintTree.setOfHighlightedNodes = set2;
        else
          paintTree.setOfHighlightedNodes = set1;
      }
      else {
        timerForDelete.stop();
        tree = treeCopy;
        tree.delete(key);        
      }
      
      paintTree.repaint();
    }
  }
  
  class PaintTree extends JPanel {      
    protected HashSet<AVLTree.TreeNode<Integer>> setOfHighlightedNodes = 
      new HashSet<AVLTree.TreeNode<Integer>>();
    protected int radius = 20;
    protected int virticalGap = 50;
    
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      // Display root     
      displayTree(g, tree.getRoot(), getWidth() / 2, 30, getWidth() / 4);
    }
    
    private void displayTree(Graphics g, AVLTree.TreeNode root, 
        int x, int y, int gap) {
      if (root != null) {
        // Display root
        if (setOfHighlightedNodes.contains(root)) {
          g.setColor(Color.GREEN);
          g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
          g.setColor(Color.BLACK);
        }
        else {
          g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        g.drawString(root.element + "", x - 6, y + 4);
        
        // Draw a line to the left node
        if (root.left != null)
          connectLeftChild(g, x - gap, y + virticalGap, x, y);
        
        // Draw left subtree
        displayTree(g, root.left, x - gap, y + virticalGap, gap / 2);
      
        // Draw a line to the right node
        if (root.right != null)
          connectRightChild(g, x + gap, y + virticalGap, x, y);
        
        // Draw right subtree
        displayTree(g, root.right, x + gap, y + virticalGap, gap / 2);      
      }
    }
    
    private void connectLeftChild(Graphics g, 
        int x1, int y1, int x2, int y2) { 
      double r = Math.sqrt(virticalGap * virticalGap + 
          (x2 - x1) * (x2 - x1));
      int x11 = (int)(x1 + radius * (x2 - x1) / r);
      int y11 = (int)(y1 - radius * virticalGap / r);
      int x21 = (int)(x2 - radius * (x2 - x1) / r);
      int y21 = (int)(y2 + radius * virticalGap / r);
      g.drawLine(x11, y11, x21, y21);
    }
    
    private void connectRightChild(Graphics g, 
        int x1, int y1, int x2, int y2) {
      double r = Math.sqrt(virticalGap * virticalGap + 
          (x2 - x1) * (x2 - x1));
      int x11 = (int)(x1 - radius * (x1 - x2) / r);
      int y11 = (int)(y1 - radius * virticalGap / r);
      int x21 = (int)(x2 + radius * (x1 - x2) / r);
      int y21 = (int)(y2 + radius * virticalGap / r);
      g.drawLine(x11, y11, x21, y21);
    }
  }
  
  static class AVLTree<E extends Comparable<E>> extends BST<E>
      implements Cloneable {
    public AVLTree() {
    }
    public AVLTree(E[] objects) {
      super(objects);
    }
    protected AVLTreeNode<E> createNewNode(E o) {
      return new AVLTreeNode<E>(o);
    }
    public boolean justInsert(E o) {
      return super.insert(o);
    }
    public boolean justDelete(E element) {
      return super.delete(element); 
    }
    public boolean insert(E o) {
      boolean successful = super.insert(o);
      if (!successful)
        return false;
      else {
        balancePath(o); 
      }

      return true; 
    }

    public java.util.HashSet<TreeNode<E>> findImbalanceSubtree(E o) {
      java.util.HashSet<TreeNode<E>> set = new java.util.HashSet<TreeNode<E>>();
      if (findImbalanceNode(o) == null) 
        return set;
      else {
        AVLTreeNode<E> root = findImbalanceNode(o);
        addDecendantsToSet(root, set);
        return set;
      }
    }
    
    public java.util.HashSet<TreeNode<E>> findImbalanceSubtreeForDeletion(E o) {
      java.util.HashSet<TreeNode<E>> set = new java.util.HashSet<TreeNode<E>>();
      if (findImbalanceNode(o) == null) 
        return set;
      else {
        AVLTreeNode<E> root = findImbalanceNode(o);
        addDecendantsToSet(root, set);
        return set;
      }
    }
    
    private void addDecendantsToSet(TreeNode<E> root, java.util.HashSet<TreeNode<E>> set) {
      if (root != null) {
        set.add(root);
        addDecendantsToSet((AVLTreeNode<E>)root.left, set);
        addDecendantsToSet((AVLTreeNode<E>)root.right, set);
      }
    }
    
    private AVLTreeNode<E> findImbalanceNode(E o) {
      if (o == null) return null;
      
      java.util.ArrayList<TreeNode<E>> path = path(o);
      for (int i = path.size() - 1; i >= 0; i--) {
        AVLTreeNode<E> A = (AVLTreeNode<E>)(path.get(i));
        updateHeight(A);
        AVLTreeNode<E> parentOfA = (A == root) ? null :
          (AVLTreeNode<E>)(path.get(i - 1));

        switch (balanceFactor(A)) {
          case -2:
            return A;
          case +2:
            return A;
        }
      }
      
      return null;
    }
    
    private E findStartingNodeForDeletion(E element) {
      if (root == null)
        return null; 

      TreeNode<E> parent = null;
      TreeNode<E> current = root;
      while (current != null) {
        if (element.compareTo(current.element) < 0) {
          parent = current;
          current = current.left;
        }
        else if (element.compareTo(current.element) > 0) {
          parent = current;
          current = current.right;
        }
        else
          break;
      }

      if (current == null)
        return null; 

      if (current.left == null) {
        if (parent == null) {
          return null;
        }
        else {
          return parent.element;
        }
      }
      else {
        TreeNode<E> parentOfRightMost = current;
        TreeNode<E> rightMost = current.left;

        while (rightMost.right != null) {
          parentOfRightMost = rightMost;
          rightMost = rightMost.right; 
        }
        return parentOfRightMost.element;
      }
    }
    
    private void updateHeight(AVLTreeNode<E> node) {
      if (node.left == null && node.right == null) 
        node.height = 0;
      else if (node.left == null)
        node.height = 1 + ((AVLTreeNode<E>)(node.right)).height;
      else if (node.right == null) 
        node.height = 1 + ((AVLTreeNode<E>)(node.left)).height;
      else
        node.height = 1 +
          Math.max(((AVLTreeNode<E>)(node.right)).height,
          ((AVLTreeNode<E>)(node.left)).height);
    }

    private void balancePath(E o) {
      java.util.ArrayList<TreeNode<E>> path = path(o);
      for (int i = path.size() - 1; i >= 0; i--) {
        AVLTreeNode<E> A = (AVLTreeNode<E>)(path.get(i));
        updateHeight(A); 
        AVLTreeNode<E> parentOfA = (A == root) ? null :
          (AVLTreeNode<E>)(path.get(i - 1));

        switch (balanceFactor(A)) {
          case -2:
            if (balanceFactor((AVLTreeNode<E>)A.left) <= 0) {
              balanceLL(A, parentOfA); 
            }
            else {
              balanceLR(A, parentOfA); 
            }
            break;
          case +2:
            if (balanceFactor((AVLTreeNode<E>)A.right) >= 0) {
              balanceRR(A, parentOfA);
            }
            else {
              balanceRL(A, parentOfA);
            }
        }
      }
    }

    private int balanceFactor(AVLTreeNode<E> node) {
      if (node.right == null)
        return -node.height;
      else if (node.left == null) 
        return +node.height;
      else
        return ((AVLTreeNode<E>)node.right).height -
          ((AVLTreeNode<E>)node.left).height;
    }

    private void balanceLL(TreeNode<E> A, TreeNode<E> parentOfA) {
      TreeNode<E> B = A.left; 

      if (A == root) {
        root = B;
      }
      else {
        if (parentOfA.left == A) {
          parentOfA.left = B;
        }
        else {
          parentOfA.right = B;
        }
      }

      A.left = B.right; 
      B.right = A; 
      updateHeight((AVLTreeNode<E>)A);
      updateHeight((AVLTreeNode<E>)B);
    }

    private void balanceLR(TreeNode<E> A, TreeNode<E> parentOfA) {
      TreeNode<E> B = A.left; 
      TreeNode<E> C = B.right; 

      if (A == root) {
        root = C;
      }
      else {
        if (parentOfA.left == A) {
          parentOfA.left = C;
        }
        else {
          parentOfA.right = C;
        }
      }

      A.left = C.right; 
      B.right = C.left; 
      C.left = B;
      C.right = A;

      
      updateHeight((AVLTreeNode<E>)A);
      updateHeight((AVLTreeNode<E>)B);
      updateHeight((AVLTreeNode<E>)C);
    }

    private void balanceRR(TreeNode<E> A, TreeNode<E> parentOfA) {
      TreeNode<E> B = A.right; 

      if (A == root) {
        root = B;
      }
      else {
        if (parentOfA.left == A) {
          parentOfA.left = B;
        }
        else {
          parentOfA.right = B;
        }
      }

      A.right = B.left; 
      B.left = A;
      updateHeight((AVLTreeNode<E>)A);
      updateHeight((AVLTreeNode<E>)B);
    }

    private void balanceRL(TreeNode<E> A, TreeNode<E> parentOfA) {
      TreeNode<E> B = A.right; 
      TreeNode<E> C = B.left; 

      if (A == root) {
        root = C;
      }
      else {
        if (parentOfA.left == A) {
          parentOfA.left = C;
        }
        else {
          parentOfA.right = C;
        }
      }

      A.right = C.left; 
      B.left = C.right; 
      C.left = A;
      C.right = B;

      updateHeight((AVLTreeNode<E>)A);
      updateHeight((AVLTreeNode<E>)B);
      updateHeight((AVLTreeNode<E>)C);
    }

    public boolean delete(E element) {
      if (root == null)
        return false; 
      TreeNode<E> parent = null;
      TreeNode<E> current = root;
      while (current != null) {
        if (element.compareTo(current.element) < 0) {
          parent = current;
          current = current.left;
        }
        else if (element.compareTo(current.element) > 0) {
          parent = current;
          current = current.right;
        }
        else
          break; 
      }
      if (current == null)
        return false; 
      if (current.left == null) {
        if (parent == null) {
          root = current.right;
        }
        else {
          if (element.compareTo(parent.element) < 0)
            parent.left = current.right;
          else
            parent.right = current.right;
          balancePath(parent.element);          
        }
      }
      else {
        TreeNode<E> parentOfRightMost = current;
        TreeNode<E> rightMost = current.left;

        while (rightMost.right != null) {
          parentOfRightMost = rightMost;
          rightMost = rightMost.right;
        }
        current.element = rightMost.element;
        if (parentOfRightMost.right == rightMost)
          parentOfRightMost.right = rightMost.left;
        else
          parentOfRightMost.left = rightMost.left; 
        balancePath(parentOfRightMost.element);
      }
      size--;
      return true; 
    }

    protected static class AVLTreeNode<E extends Comparable<E>>
        extends BST.TreeNode<E> {
      int height = 0; 

      public AVLTreeNode(E o) {
        super(o);
      }
    }  
    public Object clone() {
      AVLTree<E> tree = new AVLTree<E>();

      LinkedList<AVLTreeNode<E>> queue = new LinkedList<AVLTreeNode<E>>();
      
      if (root == null) return tree;
        
      queue.add((AVLTreeNode<E>)root);
      
      while (queue.size() > 0) {
        AVLTreeNode<E> node = queue.remove(0);
        tree.insert(node.element);
    
        if (node.left != null) 
          queue.add((AVLTreeNode<E>)(node.left));
        
        if (node.right != null) 
          queue.add((AVLTreeNode<E>)(node.right));   
      }  
      return tree;
    }
  }
}
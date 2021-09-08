package ru.vsu.csf.Sashina;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


public class SimpleBinaryTree<T> implements BinaryTree<T>{

    Comparator<T> compareValues = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            if ((int) o1 > (int) o2) {
                return 1;
            } else if ((int) o1 == (int) o2) {
                return 0;
            } else {
                return -1;
            }
        }
    };

    protected class SimpleTreeNode implements BinaryTree.TreeNode<T> {
        public T value;
        public SimpleTreeNode left;
        public SimpleTreeNode right;

        public SimpleTreeNode (T value, SimpleTreeNode left, SimpleTreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public SimpleTreeNode (T value) {
            this(value, null, null);
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public TreeNode<T> getLeft() {
            return left;
        }

        @Override
        public TreeNode<T> getRight() {
            return right;
        }

        public boolean isLeaf () {
            return left == null && right == null;
        }
    }

    protected SimpleTreeNode root = null;

    protected Function<String, T> fromStrFunc;
    protected Function<T, String> toStrFunc;

    public SimpleBinaryTree(Function<String, T> fromStrFunc, Function<T, String> toStrFunc) {
        this.fromStrFunc = fromStrFunc;
        this.toStrFunc = toStrFunc;
    }

    public SimpleBinaryTree(Function<String, T> fromStrFunc) {
        this(fromStrFunc, Object::toString);
    }

    public SimpleBinaryTree() {
        this(null);
    }

    @Override
    public TreeNode<T> getRoot() {
        return root;
    }

    public boolean isRoot (T value) {
        return value == getRoot().getValue();
    }

    public void clear() {
        root = null;
    }

    private T fromString(String s) {
        try {
            s = s.trim();
            if (s.length() > 0 && s.charAt(0) == '"') {
                s = s.substring(1);
            }
            if (s.length() > 0 && s.charAt(s.length() - 1) == '"') {
                s = s.substring(0, s.length() - 1);
            }
            return fromStrFunc.apply(s);
        } catch (Exception exp) {
            return null;
        }
    }

    private static class IndexWrapper {
        public int index = 0;
    }

    private void skipSpaces(String bracketStr, IndexWrapper iw) {
        while (iw.index < bracketStr.length() && Character.isWhitespace(bracketStr.charAt(iw.index))) {
            iw.index++;
        }
    }

    private T readValue(String bracketStr, IndexWrapper iw) {
        try {
            skipSpaces(bracketStr, iw); //пропуск пробелов
            if (iw.index >= bracketStr.length()) {
                return null;
            }
            int from = iw.index;
            boolean quote = bracketStr.charAt(iw.index) == '"';
            if (quote) {
                iw.index++;
            }
            while (iw.index < bracketStr.length() && (
                    quote && bracketStr.charAt(iw.index) != '"' ||
                            !quote && !Character.isWhitespace(bracketStr.charAt(iw.index)) && "(),".indexOf(bracketStr.charAt(iw.index)) < 0
            )) {
                iw.index++;
            }
            if (quote && bracketStr.charAt(iw.index) == '"') {
                iw.index++;
            }
            String valueStr = bracketStr.substring(from, iw.index);
            T value = fromString(valueStr);
            skipSpaces(bracketStr, iw);
            return value;
        } catch (Exception exp) {
            return null;
        }
    }

    private SimpleTreeNode fromBracketStr(String bracketStr, IndexWrapper iw) {
        try {
            T parentValue = readValue(bracketStr, iw);
            SimpleTreeNode parentNode = new SimpleTreeNode(parentValue);
            if (bracketStr.charAt(iw.index) == '(') {
                iw.index++;
                skipSpaces(bracketStr, iw);
                if (bracketStr.charAt(iw.index) != ',') {
                    parentNode.left = fromBracketStr(bracketStr, iw);
                    skipSpaces(bracketStr, iw);
                }
                if (bracketStr.charAt(iw.index) == ',') {
                    iw.index++;
                    skipSpaces(bracketStr, iw);
                }
                if (bracketStr.charAt(iw.index) != ')') {
                    parentNode.right = fromBracketStr(bracketStr, iw);
                    skipSpaces(bracketStr, iw);
                }
                iw.index++;
            }
            return parentNode;
        } catch (Exception exp) {
            return null;
        }
    }

    public void fromBracketNotation(String bracketStr) {
        try {
            IndexWrapper iw = new IndexWrapper();
            SimpleTreeNode root = fromBracketStr(bracketStr, iw);
            this.root = root;
        } catch (Exception exp) {
            return;
        }
    }

    private void deleteLeaf (SimpleTreeNode node, T value) throws Exception {
        if (node.left != null) {
            if (node.left.isLeaf() && node.left.value == value) {
                node.left = null;
                return;
            }
        }
        if (node.right != null) {
            if (node.right.isLeaf() && node.right.value == value) {
                node.right = null;
                return;
            }
        }
        if (node.left != null) {
            deleteLeaf(node.left, value);
        } else if (node.right != null) {
            deleteLeaf(node.right, value);
        } else {return;}
    }

    private int amountOfLeaves (SimpleTreeNode node) {
        int result = 0;
        if (node.left == null && node.right == null)
            result += 1;
        if (node.left != null)
            result += amountOfLeaves(node.left);
        if (node.right != null)
            result += amountOfLeaves(node.right);
        return result;
    }

    private void leaves (SimpleTreeNode node, Set<T> set) {
        if (node.left == null && node.right == null)
            set.add(node.getValue());
        if (node.left != null)
            leaves(node.left, set);
        if (node.right != null)
            leaves(node.right, set);
    }

    @Override
    public Set<T> leaves () {
        Set<T> set = new HashSet<>();
        leaves(root, set);
        return set;
    }

    @Override
    public int amountOfLeaves(int sum) {
        return amountOfLeaves(root);
    }

    @Override
    public void deleteLeaves (T value) throws Exception {
        deleteLeaf(root, value);
    }
}

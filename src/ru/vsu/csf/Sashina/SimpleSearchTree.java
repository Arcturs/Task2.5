package ru.vsu.csf.Sashina;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class SimpleSearchTree <T extends Comparable<? super T>> extends SimpleBinaryTree<T> implements SearchTree<T> {

    private static class CheckBSTResult<T> {
        public boolean result;
        public int size;
        public T min;
        public T max;

        public CheckBSTResult(boolean result, int size, T min, T max) {
            this.result = result;
            this.size = size;
            this.min = min;
            this.max = max;
        }
    }

    int size = 0;

    public SimpleSearchTree(Function<String, T> fromStrFunc) {
        super(fromStrFunc);
    }

    private static <T extends Comparable<? super T>> CheckBSTResult<T> isBSTInner(BinaryTree.TreeNode<T> node) {
        if (node == null) {
            return null;
        }

        CheckBSTResult<T> leftResult = isBSTInner(node.getLeft());
        CheckBSTResult<T> result = new CheckBSTResult<>(true, 1, node.getValue(), node.getValue());
        CheckBSTResult<T> rightResult = isBSTInner(node.getRight());
        if (leftResult != null) {
            result.result &= leftResult.result; //является ли левое дерево деревом поиска
            result.result &= leftResult.max.compareTo(node.getValue()) < 0;
            result.size += leftResult.size;
            result.min = leftResult.min;
        }
        if (rightResult != null) {
            result.result &= rightResult.result;
            result.result &= rightResult.min.compareTo(node.getValue()) > 0;
            result.size += rightResult.size;
            result.max = rightResult.max;
        }
        return result;
    }

    private static <T extends Comparable<? super T>> Set<T> check (BinaryTree.TreeNode<T> node, Optional<T> min, Optional<T> max, Set<T> leaves) {
        if (node == null) {
            return leaves;
        }
        if (node.getValue().compareTo(min.orElse(node.getValue())) < 0 || node.getValue().compareTo(max.orElse(node.getValue())) > 0) {
            if (node.isLeaf() && leaves != null && leaves.size() == 0) {
                leaves.add(node.getValue());
                //System.out.println(node.getValue());
            } else if (node.isLeaf() && leaves != null && leaves.size() != 0) {
                return null;
            } else {
                return leaves;
            }
        }
        Optional<T> max1 = Optional.ofNullable(node.getValue());
        Optional<T> min1 = Optional.ofNullable(node.getValue());
        leaves = check (node.getLeft(), min, max1, leaves);
        leaves = check (node.getRight(), min1, max, leaves);
        return leaves;
    }

    private static <T extends Comparable<? super T>> Set<T> isBinaryTree (BinaryTree.TreeNode<T> node, Optional<T> min, Optional<T> max, Set<T> leaves) {
        return check(node, min, max, leaves);
    }

    @Override
    public void fromBracketNotation(String bracketStr) {
        try {
            SimpleBinaryTree tempTree = new SimpleBinaryTree(this.fromStrFunc);
            tempTree.fromBracketNotation(bracketStr);
            CheckBSTResult<T> tempTreeResult = isBSTInner(tempTree.getRoot());
            super.fromBracketNotation(bracketStr);
            this.size = tempTreeResult.size;
        } catch (Exception exp) {
            return;
        }
    }

    private T put(SimpleTreeNode node, T value) {
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            T oldValue = node.value;
            node.value = value;
            return oldValue;
        } else {
            if (cmp < 0) {
                if (node.left == null) {
                    node.left = new SimpleTreeNode(value);
                    size++;
                    return null;
                } else {
                    return put(node.left, value);
                }
            } else {
                if (node.right == null) {
                    node.right = new SimpleTreeNode(value);
                    size++;
                    return null;
                } else {
                    return put(node.right, value);
                }
            }
        }
    }

    // Реализация BSTree<T>
    @Override
    public T put(T value) {
        if (root == null) {
            root = new SimpleTreeNode(value);
            size++;
            return null;
        }
        return put(root, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Set<T> isBSTree(BinaryTree.TreeNode<T> node, Optional<T> min, Optional<T> max, Set<T> leaves) {
        return isBinaryTree(node, min, max, leaves);
    }
}

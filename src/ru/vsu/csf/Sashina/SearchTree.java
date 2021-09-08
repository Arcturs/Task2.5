package ru.vsu.csf.Sashina;

import java.util.Optional;
import java.util.Set;

public interface SearchTree <T extends Comparable<? super T>> extends BinaryTree<T> {

    T put(T value);

    void clear();

    int size();

    //boolean isBSTree(BinaryTree.TreeNode<T> node, T value);

    Set<T> isBSTree(BinaryTree.TreeNode<T> node, Optional<T> min, Optional<T> max, Set<T> leaves);
}
